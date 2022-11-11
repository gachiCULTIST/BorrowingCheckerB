package mai.student.tokenizers.java17;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import mai.student.intermediateStates.DefinedFunction;
import mai.student.intermediateStates.FileRepresentative;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Базовый токенизатор
// Просто вставляет элемент в представление при этом,
// если конкретного элемента нет в словаре токенов,
// то он туда добавляется с генерируемым идентификатором
// + все литералы преобразуются к обобщающему литеральному токену
public class BasicStatementProcessor implements StatementProcessor {

    protected static final String LITERAL = "*literal*";
    protected static final String NEW = "new";
    protected static final String CLASS = "class";
    protected static final String RETURN = "return";
    protected static final String SYNCHRONIZED = "synchronized";
    protected static final String SUPER = "super";
    protected static final String THIS = "this";
    protected static final String INSTANCE_OF = "instanceof";
    protected static final String TRY = "try";
    protected static final String CATCH = "catch";
    protected static final String FINAL = "final";
    protected static final String THROW = "throw";
    protected static final String SWITCH = "switch";
    protected static final String CASE = "case";
    protected static final String BREAK = "break";
    protected static final String YIELD = "yield";
    protected static final String CONTINUE = "continue";
    protected static final String DO = "do";
    protected static final String WHILE = "while";
    protected static final String FOR = "for";
    protected static final String IF = "if";
    protected static final String ELSE = "else";
    protected static final String ASSERT = "assert";
    protected static final String DOT = ".";
    protected static final String COMMA = ",";
    protected static final String COLON = ":";
    protected static final String SEMICOLON = ";";
    protected static final String LEFT_PAREN = "(";
    protected static final String RIGHT_PAREN = ")";
    protected static final String LEFT_BRACKET = "[";
    protected static final String RIGHT_BRACKET = "]";
    protected static final String LEFT_BRACE = "{";
    protected static final String RIGHT_BRACE = "}";
    protected static final String LAMBDA = "->";
    protected static final String ASSIGN = "=";
    protected static final String QUESTION = "?";
    protected static final String GREATER = ">";
    protected static final String LOWER = "<";
    protected static final String UNION = "|";
    protected static final String INTERSECTION = "&";

    protected static final int START_INDEX = 1000;

    protected static int indexForNextElement = START_INDEX;

    protected final Map<String, Integer> tokenDictionary;

    protected final DefinedFunction function;

    protected final List<FileRepresentative> files;

    // Вообще визитор используется для определения типа Expression,
    // по скольку все элементы составных выражений представляет данный тип
    protected final VoidVisitor<StatementProcessor> visitor;


    public BasicStatementProcessor(Map<String, Integer> tokenDictionary, DefinedFunction function,
                                   List<FileRepresentative> files, VoidVisitor<StatementProcessor> visitor) {

        if (tokenDictionary == null || function == null || files == null || visitor == null) {
            throw new IllegalArgumentException("BasicStatementProcessor: constructor arguments must be not null!");
        }

        this.tokenDictionary = tokenDictionary;
        this.function = function;
        this.files = files;
        this.visitor = visitor;
    }

    // TODO: add modifier printer for Parameters where are used

    // TODO: check type with arrays or generics in some expressions
    //  PS: when scope uses type Type, i just addToken(type.asString())

    @Override
    public void run() {
        process(function.getBody());
    }

    @Override
    public void process(ArrayAccessExpr arrayAccessExpr) {
        addToken(LEFT_BRACKET);
        arrayAccessExpr.getIndex().accept(visitor, this);
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void process(ArrayCreationExpr arrayCreationExpr) {
        addToken(NEW);

        // TODO: здесь парсер возвращает тип Type,
        //  хотя там может выражение типа java.util.ArrayList + Type<>
        addTypeAsTokens(arrayCreationExpr.getElementType());

        // brackets with possible insides
        for (ArrayCreationLevel level : arrayCreationExpr.getLevels()) {
            addToken(LEFT_BRACKET);
            if (level.getDimension().isPresent()) {
                level.getDimension().get().accept(visitor, this);
            }
            addToken(RIGHT_BRACKET);
        }


        // initializer
        Optional<ArrayInitializerExpr> init = arrayCreationExpr.getInitializer();
        init.ifPresent(arrayInitializerExpr -> arrayInitializerExpr.accept(visitor, this));
    }

    @Override
    public void process(ArrayInitializerExpr arrayInitializerExpr) {
        addToken(LEFT_BRACE);

        for (Expression value : arrayInitializerExpr.getValues()) {
            // TODO: check with multidimensional array
            value.accept(visitor, this);
        }

        addToken(RIGHT_BRACE);
    }

    @Override
    public void process(AssignExpr assignExpr) {
        assignExpr.getTarget().accept(visitor, this);
        addToken(ASSIGN);
        assignExpr.getValue().accept(visitor, this);
    }

    @Override
    public void process(BinaryExpr binaryExpr) {
        binaryExpr.getLeft().accept(visitor, this);
        addToken(binaryExpr.getOperator().asString());
        binaryExpr.getRight().accept(visitor, this);
    }

    @Override
    public void process(CastExpr castExpr) {
        addToken(LEFT_PAREN);
        // TODO: check expression == type or right part??
        castExpr.getExpression().accept(visitor, this);
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(ClassExpr classExpr) {
        // TODO: check java.util.Type.class + Type<>
        addTypeAsTokens(classExpr.getType());
        addToken(DOT);
        addToken(CLASS);
    }

    @Override
    public void process(ConditionalExpr conditionalExpr) {
        conditionalExpr.getCondition().accept(visitor, this);
        addToken(QUESTION);
        conditionalExpr.getThenExpr().accept(visitor, this);
        addToken(COLON);
        conditionalExpr.getElseExpr().accept(visitor, this);
    }

    @Override
    public void process(EnclosedExpr enclosedExpr) {
        addToken(LEFT_PAREN);
        enclosedExpr.getInner().accept(visitor, this);
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(FieldAccessExpr fieldAccessExpr) {
        fieldAccessExpr.getScope().accept(visitor, this);
        addToken(DOT);
        addToken(fieldAccessExpr.getName().asString());
    }

    @Override
    public void process(InstanceOfExpr instanceOfExpr) {
        // TODO: check processing
        instanceOfExpr.getExpression().accept(visitor, this);
        addToken(INSTANCE_OF);

        // if presented full operator version, his right part wrapped by PatternExpr
        Optional<PatternExpr> pattern = instanceOfExpr.getPattern();
        if (pattern.isPresent()) {
            pattern.get().accept(visitor, this);
        } else {
            addTypeAsTokens(instanceOfExpr.getType());
        }
    }


    // При обработке нельзя определить, были ли указаны фигурные скобки у параметров или нет
    @Override
    public void process(LambdaExpr lambdaExpr) {
        if (lambdaExpr.isEnclosingParameters()) {
            addToken(LEFT_PAREN);
        }
        for (Parameter param : lambdaExpr.getParameters()) {
            // TODO: check type processing if it is not present
            addTypeAsTokens(param.getType());
            addToken(param.getNameAsString());
        }
        if (lambdaExpr.isEnclosingParameters()) {
            addToken(RIGHT_PAREN);
        }

        addToken(LAMBDA);
        lambdaExpr.getBody().accept(visitor, this);
    }

    @Override
    public void process(BooleanLiteralExpr booleanLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(StringLiteralExpr stringLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(CharLiteralExpr charLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(DoubleLiteralExpr doubleLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(IntegerLiteralExpr integerLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(LongLiteralExpr longLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(TextBlockLiteralExpr textBlockLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(NullLiteralExpr nullLiteralExpr) {
        addToken(LITERAL);
    }

    @Override
    public void process(MethodCallExpr methodCallExpr) {
        // scope
        methodCallExpr.getScope().ifPresent(scope -> {
            scope.accept(visitor, this);
            addToken(DOT);
        });

        // generic types
        boolean isNotFirst = false;
        Optional<NodeList<Type>> typeArguments = methodCallExpr.getTypeArguments();
        if (typeArguments.isPresent()) {
            addToken(LOWER);


            for (Type type : typeArguments.get()) {
                if (isNotFirst) {
                    addToken(COMMA);
                } else {
                    isNotFirst = true;
                }

                addTypeAsTokens(type);
            }

            addToken(GREATER);
        }

        // id + params
        addToken(methodCallExpr.getName().asString());
        addToken(LEFT_PAREN);
        isNotFirst = false;
        for (Expression arg : methodCallExpr.getArguments()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
        }
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(MethodReferenceExpr methodReferenceExpr) {
        // scope
        methodReferenceExpr.getScope().accept(visitor, this);
        addToken(COLON);
        addToken(COLON);

        // generic params
        boolean isNotFirst = false;
        Optional<NodeList<Type>> typeArguments = methodReferenceExpr.getTypeArguments();
        if (typeArguments.isPresent()) {
            addToken(LOWER);


            for (Type type : typeArguments.get()) {
                if (isNotFirst) {
                    addToken(COMMA);
                } else {
                    isNotFirst = true;
                }

                addTypeAsTokens(type);
            }

            addToken(GREATER);
        }

        // id
        addToken(methodReferenceExpr.getIdentifier());
    }

    @Override
    public void process(NameExpr nameExpr) {
        addToken(nameExpr.getNameAsString());
    }

    @Override
    public void process(ObjectCreationExpr objectCreationExpr) {
        // scope (never seen usage)
        objectCreationExpr.getScope().ifPresent(scope -> {
            scope.accept(visitor, this);
            addToken(DOT);
        });

        // id
        addToken(NEW);
        addTypeAsTokens(objectCreationExpr.getType());

        // params
        addToken(LEFT_PAREN);
        boolean isNotFirst = false;
        for (Expression arg : objectCreationExpr.getArguments()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
        }
        addToken(RIGHT_PAREN);

        // anonymous class
        // TODO: check
        objectCreationExpr.getAnonymousClassBody().ifPresent(bodyDeclarations -> {
            addToken(LEFT_BRACE);

            for (BodyDeclaration<?> body : bodyDeclarations) {
                body.accept(visitor, this);
            }

            addToken(RIGHT_BRACE);
        });
    }

    @Override
    public void process(PatternExpr patternExpr) {
        addTypeAsTokens(patternExpr.getType());
        addToken(patternExpr.getNameAsString());
    }

    @Override
    public void process(SuperExpr superExpr) {
        superExpr.getTypeName().ifPresent(type -> {
            addComplexName(type);
            addToken(DOT);
        });

        addToken(SUPER);
    }

    @Override
    public void process(SwitchExpr switchExpr) {
        addToken(SWITCH);

        // selector
        addToken(LEFT_PAREN);
        switchExpr.getSelector().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // entries
        addToken(LEFT_BRACE);
        for (SwitchEntry entry : switchExpr.getEntries()) {
            addToken(CASE);

            // case conditions
            boolean isNotFirst = false;
            for (Expression label : entry.getLabels()) {
                if (isNotFirst) {
                    addToken(COMMA);
                } else {
                    isNotFirst = true;
                }

                label.accept(visitor, this);
            }

            // after case label there can be ":" or "->"
            if (entry.getType() == SwitchEntry.Type.STATEMENT_GROUP) {
                addToken(COLON);
            } else {
                addToken(LAMBDA);
            }

            // entry body
            entry.getStatements().forEach(body -> body.accept(visitor, this));
        }
        addToken(RIGHT_BRACE);
    }

    @Override
    public void process(ThisExpr thisExpr) {
        thisExpr.getTypeName().ifPresent(this::addComplexName);
        addToken(THIS);
    }

    @Override
    public void process(TypeExpr typeExpr) {
        addTypeAsTokens(typeExpr.getType());
    }

    @Override
    public void process(UnaryExpr unaryExpr) {
        if (unaryExpr.isPrefix()) {
            addToken(unaryExpr.getOperator().asString());
            unaryExpr.getExpression().accept(visitor, this);
        } else {
            unaryExpr.getExpression().accept(visitor, this);
            addToken(unaryExpr.getOperator().asString());
        }
    }

    // every var in the list are represented as "type id = Expr",
    // cause there can be construction "int[] ar1, ar2[];"
    // PS: shortly - "int[] ar1, ar2[];" -> "int[] ar1, int[][] ar2;"
    // TODO: check semicolon insertion in stmt processing
    @Override
    public void process(VariableDeclarationExpr variableDeclarationExpr) {
        // TODO: check modifiers
        // modifiers
        variableDeclarationExpr.getModifiers().forEach(modifier -> addToken(modifier.toString()));

        // var list
        boolean isNotFirst = false;
        for (VariableDeclarator var : variableDeclarationExpr.getVariables()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }


            // TODO: rework
            addTypeAsTokens(var.getType());
            addToken(var.getNameAsString());

            // TODO: check "=" insertion
            addToken(ASSIGN);
            var.getInitializer().ifPresent(init -> init.accept(visitor, this));
        }
    }

    @Override
    public void process(AssertStmt assertStmt) {
        addToken(ASSERT);
        assertStmt.getCheck().accept(visitor, this);
        assertStmt.getMessage().ifPresent(message -> {
            addToken(COLON);
            message.accept(visitor, this);
        });
        addToken(SEMICOLON);
    }

    @Override
    public void process(BlockStmt blockStmt) {
        addToken(LEFT_BRACE);
        blockStmt.getStatements().forEach(statement -> statement.accept(visitor, this));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void process(BreakStmt breakStmt) {
        addToken(BREAK);
        breakStmt.getLabel().ifPresent(label -> addToken(label.asString()));
        addToken(SEMICOLON);
    }

    @Override
    public void process(ContinueStmt continueStmt) {
        addToken(CONTINUE);
        continueStmt.getLabel().ifPresent(label -> addToken(label.asString()));
        addToken(SEMICOLON);
    }

    @Override
    public void process(DoStmt doStmt) {
        addToken(DO);

        // body
        doStmt.getBody().accept(visitor, this);

        // condition
        addToken(WHILE);
        addToken(LEFT_PAREN);
        doStmt.getCondition().accept(visitor, this);
        addToken(RIGHT_PAREN);
        addToken(SEMICOLON);
    }

    @Override
    public void process(EmptyStmt emptyStmt) {
        // skip
    }

    @Override
    public void process(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt) {
        // super or this
        if (explicitConstructorInvocationStmt.isThis()) {
            addToken(THIS);
        } else {
            addToken(SUPER);
        }
        addToken(DOT);

        // arguments
        addToken(LEFT_PAREN);
        boolean isNotFirst = false;
        for (Expression arg : explicitConstructorInvocationStmt.getArguments()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
        }
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(ForEachStmt forEachStmt) {
        addToken(FOR);

        // TODO: check
        // var definition
        addToken(LEFT_PAREN);
        forEachStmt.getVariable().accept(visitor, this);

        addToken(COLON);

        forEachStmt.getIterable().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // body
        forEachStmt.getBody().accept(visitor, this);
    }

    @Override
    public void process(ForStmt forStmt) {
        addToken(FOR);

        addToken(LEFT_PAREN);
        // var definition
        boolean isNotFirst = false;
        // TODO: check
        for (Expression definition : forStmt.getInitialization()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            definition.accept(visitor, this);
        }
        addToken(SEMICOLON);

        // condition
        forStmt.getCompare().ifPresent(condition -> condition.accept(visitor, this));
        addToken(SEMICOLON);

        // progress
        isNotFirst = false;
        for (Expression update : forStmt.getUpdate()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            update.accept(visitor, this);
        }
        addToken(RIGHT_PAREN);

        // body
        forStmt.getBody().accept(visitor, this);
    }

    @Override
    public void process(IfStmt ifStmt) {
        addToken(IF);

        // condition
        addToken(LEFT_PAREN);
        ifStmt.getCondition().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // then
        // TODO: check with body and without (single statement)
        ifStmt.getThenStmt().accept(visitor, this);

        // else
        ifStmt.getElseStmt().ifPresent(elseBlock -> {
            addToken(ELSE);
            elseBlock.accept(visitor, this);
        });
    }

    @Override
    public void process(LabeledStmt labeledStmt) {
        addToken(labeledStmt.getLabel().asString());
        addToken(COLON);
        labeledStmt.getStatement().accept(visitor, this);
    }

    @Override
    public void process(LocalClassDeclarationStmt localClassDeclarationStmt) {
        // skip because it is represented in structure
    }

    @Override
    public void process(LocalRecordDeclarationStmt localRecordDeclarationStmt) {
        // skip because it is represented in structure
    }

    @Override
    public void process(ReturnStmt returnStmt) {
        addToken(RETURN);
        returnStmt.getExpression().ifPresent(result -> result.accept(visitor, this));
        addToken(SEMICOLON);
    }

    //TODO: same as SwitchExpr
    @Override
    public void process(SwitchStmt switchStmt) {
        addToken(SWITCH);

        // selector
        addToken(LEFT_PAREN);
        switchStmt.getSelector().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // entries
        addToken(LEFT_BRACE);
        for (SwitchEntry entry : switchStmt.getEntries()) {
            addToken(CASE);

            // case conditions
            boolean isNotFirst = false;
            for (Expression label : entry.getLabels()) {
                if (isNotFirst) {
                    addToken(COMMA);
                } else {
                    isNotFirst = true;
                }

                label.accept(visitor, this);
            }

            // after case label there can be ":" or "->"
            if (entry.getType() == SwitchEntry.Type.STATEMENT_GROUP) {
                addToken(COLON);
            } else {
                addToken(LAMBDA);
            }

            // entry body
            entry.getStatements().forEach(body -> body.accept(visitor, this));
        }
        addToken(RIGHT_BRACE);
    }

    @Override
    public void process(SynchronizedStmt synchronizedStmt) {
        addToken(SYNCHRONIZED);

        // lock
        addToken(LEFT_PAREN);
        synchronizedStmt.getExpression().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // body
        synchronizedStmt.getBody().accept(visitor, this);
    }

    @Override
    public void process(ThrowStmt throwStmt) {
        addToken(THROW);
        throwStmt.getExpression().accept(visitor, this);
    }

    @Override
    public void process(TryStmt tryStmt) {
        addToken(TRY);

        // resources
        boolean isNotFirst = false;
        if (tryStmt.getResources().size() != 0) {
            addToken(LEFT_PAREN);

            // TODO: extract printing list of something in other method
            for (Expression resource : tryStmt.getResources()) {
                if (isNotFirst) {
                    addToken(COMMA);
                } else {
                    isNotFirst = true;
                }

                resource.accept(visitor, this);
            }

            addToken(RIGHT_PAREN);
        }

        // try-block
        tryStmt.getTryBlock().accept(visitor, this);

        // catch-blocks
        for (CatchClause catchClause : tryStmt.getCatchClauses()) {
            addToken(CATCH);

            // exception type
            addToken(LEFT_PAREN);
                // TODO: check type processing if it is not present
            addTypeAsTokens(catchClause.getParameter().getType());
            addToken(catchClause.getParameter().getNameAsString());
            addToken(RIGHT_PAREN);

            // body
            catchClause.getBody().accept(visitor, this);
        }

        // final-block
        tryStmt.getFinallyBlock().ifPresent(finalBlock -> {
            addToken(FINAL);
            finalBlock.accept(visitor, this);
        });
    }

    @Override
    public void process(UnparsableStmt unparsableStmt) {
        // TODO: poka tak
        addToken(unparsableStmt.toString());
    }

    @Override
    public void process(WhileStmt whileStmt) {
        addToken(WHILE);

        // condition
        addToken(LEFT_PAREN);
        whileStmt.getCondition().accept(visitor, this);
        addToken(RIGHT_PAREN);

        // body
        // TODO: check with single statement body and with block
        whileStmt.getBody().accept(visitor, this);
    }

    @Override
    public void process(YieldStmt yieldStmt) {
        addToken(YIELD);
        yieldStmt.getExpression().accept(visitor, this);
        addToken(SEMICOLON);
    }

    @Override
    public void process(ExpressionStmt expressionStmt) {
        expressionStmt.getExpression().accept(visitor, this);
        addToken(SEMICOLON);
    }

    protected void addToken(String lexeme) {
        if (!tokenDictionary.containsKey(lexeme)) {
            tokenDictionary.put(lexeme, indexForNextElement);
            ++indexForNextElement;
        }

        function.addToken(tokenDictionary.get(lexeme));
    }

    // insert qualifier.identifier name
    protected void addComplexName(Name name) {
        name.getQualifier().ifPresent(qualifier -> {
            addComplexName(qualifier);
            addToken(DOT);
        });
        addToken(name.getIdentifier());
    }

    // insert type representation
    protected void addTypeAsTokens(Type type) {
        if (type.isPrimitiveType()) {
            addToken(type.asPrimitiveType().asString());
            return;
        }

        // Ссылочные типы
        if (type.isArrayType()) {
            ArrayType t = type.asArrayType();
            addTypeAsTokens(t.getElementType());

            for (int i = 0; i < t.getArrayLevel(); i++) {
                addToken(LEFT_BRACKET);
                addToken(RIGHT_BRACKET);
            }
            return;
        }
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType t = type.asClassOrInterfaceType();

            // scope
            t.getScope().ifPresent(scope -> {
                addTypeAsTokens(scope);
                addToken(DOT);
            });

            // id
            addToken(t.getNameAsString());

            // generic params
            if (t.getTypeArguments().isPresent()) {
                addToken(LOWER);

                boolean isNotFirst = false;
                for (Type param : t.getTypeArguments().get()) {
                    if (isNotFirst) {
                        addToken(COMMA);
                    } else {
                        isNotFirst = true;
                    }

                    addTypeAsTokens(param);
                }

                addToken(GREATER);
            }
            return;
        }
        if (type.isTypeParameter()) {
            addToken(type.asTypeParameter().getNameAsString());
            return;
        }
        if (type.isIntersectionType()) {
            IntersectionType intersectionType = (IntersectionType) type;
            boolean isNotFirst = false;

            for (ReferenceType t : intersectionType.getElements()) {
                if (isNotFirst) {
                    addToken(INTERSECTION);
                } else {
                    isNotFirst = true;
                }

                addTypeAsTokens(t);
            }
            return;
        }
        if (type.isUnionType()) {
            UnionType unionType = (UnionType) type;
            boolean isNotFirst = false;

            for (ReferenceType t : unionType.getElements()) {
                if (isNotFirst) {
                    addToken(UNION);
                } else {
                    isNotFirst = true;
                }

                addTypeAsTokens(t);
            }
            return;
        }
        if (type.isWildcardType()) {
            addToken(QUESTION);
            return;
        }
        if (type.isUnknownType()) {
            // uses in lambda
            return;
        }

        throw new UnsupportedOperationException("BasicStatementProcessor.addTypeAsTokens: unsupported type of type! ");
    }
}
