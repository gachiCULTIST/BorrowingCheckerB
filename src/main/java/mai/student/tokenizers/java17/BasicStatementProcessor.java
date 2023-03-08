package mai.student.tokenizers.java17;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import mai.student.intermediateStates.DefinedFunction;
import mai.student.intermediateStates.FileRepresentative;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Базовый токенизатор
// Просто вставляет элемент в представление при этом,
// если конкретного элемента нет в словаре токенов,
// то он туда добавляется с генерируемым идентификатором
// + все литералы преобразуются к обобщающему литеральному токену
// + Объявление списка элементов в виде перечня отделенных объявлений
// Игнорирование локальных и анонимных классов и локальных записей.
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
    protected static final String FINALLY = "finally";
    protected static final String THROW = "throw";
    protected static final String SWITCH = "switch";
    protected static final String CASE = "case";
    protected static final String DEFAULT = "default";
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

    protected final int TOKEN_SPAN = 1000;

    protected int indexForNextElement;

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
        this.indexForNextElement = tokenDictionary.size() + TOKEN_SPAN;

        this.function = function;
        this.files = files;
        this.visitor = visitor;
    }

    @Override
    public void run() {
        function.tokenized();
        process(function.getBody());
    }

    @Override
    public void process(ArrayAccessExpr arrayAccessExpr) {
        arrayAccessExpr.getName().accept(visitor, this);
        addToken(LEFT_BRACKET);
        arrayAccessExpr.getIndex().accept(visitor, this);
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void process(ArrayCreationExpr arrayCreationExpr) {
        addToken(NEW);

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
        addExpressionElemList(arrayInitializerExpr.getValues(), COMMA);
        addToken(RIGHT_BRACE);
    }

    @Override
    public void process(AssignExpr assignExpr) {
        assignExpr.getTarget().accept(visitor, this);
        addToken(assignExpr.getOperator().asString());
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
        addTypeAsTokens(castExpr.getType());
        addToken(RIGHT_PAREN);
        castExpr.getExpression().accept(visitor, this);
    }

    @Override
    public void process(ClassExpr classExpr) {
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
        addParameterElemList(lambdaExpr.getParameters());
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
        Optional<NodeList<Type>> typeArguments = methodCallExpr.getTypeArguments();
        if (typeArguments.isPresent()) {
            addToken(LOWER);
            addTypeElemList(typeArguments.get(), COMMA);
            addToken(GREATER);
        }

        // id + params
        addToken(methodCallExpr.getName().asString());
        addToken(LEFT_PAREN);
        addExpressionElemList(methodCallExpr.getArguments(), COMMA);
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(MethodReferenceExpr methodReferenceExpr) {
        // scope
        methodReferenceExpr.getScope().accept(visitor, this);
        addToken(COLON);
        addToken(COLON);

        // generic params
        Optional<NodeList<Type>> typeArguments = methodReferenceExpr.getTypeArguments();
        if (typeArguments.isPresent()) {
            addToken(LOWER);
            addTypeElemList(typeArguments.get(), COMMA);
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
        addExpressionElemList(objectCreationExpr.getArguments(), COMMA);
        addToken(RIGHT_PAREN);
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

    // !!! Parser don't recognize this (Switch expression with return via break):
    // int value = switch (count) {
    //            case 1:
    //                break 1 + 3;
    //            case 2:
    //                break 32;
    //            case 3:
    //                break 52;
    //            default:
    //                break 0;
    //        };
    @Override
    public void process(SwitchExpr switchExpr) {
        addSwitch(switchExpr.getSelector(), switchExpr.getEntries());
    }

    @Override
    public void process(ThisExpr thisExpr) {
        thisExpr.getTypeName().ifPresent(name -> {
            addComplexName(name);
            addToken(DOT);
        });
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
    // PS: shortly - "int[] ar1, ar2[];" -> "int[] ar1; int[][] ar2;"
    @Override
    public void process(VariableDeclarationExpr variableDeclarationExpr) {

        // var list
        boolean isNotFirst = false;
        for (VariableDeclarator var : variableDeclarationExpr.getVariables()) {
            if (isNotFirst) {
                addToken(SEMICOLON);
            } else {
                isNotFirst = true;
            }

            // modifiers
            //  PS: parser returns modifier with whitespace
            variableDeclarationExpr.getModifiers().forEach(modifier -> addToken(modifier.toString().strip()));

            // Type + id
            addTypeAsTokens(var.getType());
            addToken(var.getNameAsString());

            // initializer
            var.getInitializer().ifPresent(init -> {
                addToken(ASSIGN);
                init.accept(visitor, this);
            });
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

        // arguments
        addToken(LEFT_PAREN);
        addExpressionElemList(explicitConstructorInvocationStmt.getArguments(), COMMA);
        addToken(RIGHT_PAREN);
    }

    @Override
    public void process(ForEachStmt forEachStmt) {
        addToken(FOR);

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
        addExpressionElemList(forStmt.getInitialization(), COMMA);
        addToken(SEMICOLON);

        // condition
        forStmt.getCompare().ifPresent(condition -> condition.accept(visitor, this));
        addToken(SEMICOLON);

        // progress
        addExpressionElemList(forStmt.getUpdate(), COMMA);
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

    @Override
    public void process(SwitchStmt switchStmt) {
        addSwitch(switchStmt.getSelector(), switchStmt.getEntries());
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
        addToken(SEMICOLON);
    }

    @Override
    public void process(TryStmt tryStmt) {
        addToken(TRY);

        // resources
        if (tryStmt.getResources().size() != 0) {
            addToken(LEFT_PAREN);
            addExpressionElemList(tryStmt.getResources(), SEMICOLON);
            addToken(RIGHT_PAREN);
        }

        // try-block
        tryStmt.getTryBlock().accept(visitor, this);

        // catch-blocks
        for (CatchClause catchClause : tryStmt.getCatchClauses()) {
            addToken(CATCH);

            // exception type
            addToken(LEFT_PAREN);
            addParameterElemList(Collections.singleton(catchClause.getParameter()));
            addToken(RIGHT_PAREN);

            // body
            catchClause.getBody().accept(visitor, this);
        }

        // final-block
        tryStmt.getFinallyBlock().ifPresent(finalBlock -> {
            addToken(FINALLY);
            finalBlock.accept(visitor, this);
        });
    }

    @Override
    public void process(UnparsableStmt unparsableStmt) {
        // mere add as a string
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

    // Обработка SwitchExpr и SwitchStmt
    protected void addSwitch(Expression selector, NodeList<SwitchEntry> entries) {
        addToken(SWITCH);

        // selector
        addToken(LEFT_PAREN);
        selector.accept(visitor, this);
        addToken(RIGHT_PAREN);

        // entries
        addToken(LEFT_BRACE);
        for (SwitchEntry entry : entries) {
            // case conditions
            if (entry.getLabels().size() == 0) {
                addToken(DEFAULT);
            } else {
                addToken(CASE);
                addExpressionElemList(entry.getLabels(), COMMA);
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

    // Добавляет список параметров с их модификаторами, типом и идентификатором
    protected void addParameterElemList(Iterable<Parameter> iterable) {
        boolean isNotFirst = false;
        for (Parameter param : iterable) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            param.getModifiers().forEach(modifier -> addToken(modifier.toString().strip()));
            addTypeAsTokens(param.getType());
            addToken(param.getNameAsString());
        }
    }

    // Добавить список типов с указанным разделителем
    protected void addTypeElemList(Iterable<? extends Type> iterable, String delimiter) {
        boolean isNotFirst = false;
        for (Type type : iterable) {
            if (isNotFirst) {
                addToken(delimiter);
            } else {
                isNotFirst = true;
            }

            addTypeAsTokens(type);
        }
    }

    // Добавить список выражений с указанным разделителем
    protected void addExpressionElemList(Iterable<Expression> iterable, String delimiter) {
        boolean isNotFirst = false;
        for (Expression arg : iterable) {
            if (isNotFirst) {
                addToken(delimiter);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
        }
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
                addTypeElemList(t.getTypeArguments().get(), COMMA);
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
            addTypeElemList(intersectionType.getElements(), INTERSECTION);
            return;
        }
        if (type.isUnionType()) {
            UnionType unionType = (UnionType) type;
            addTypeElemList(unionType.getElements(), UNION);
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
        if (type.isVarType()) {
            addToken(type.toString());
            return;
        }

        throw new UnsupportedOperationException("BasicStatementProcessor.addTypeAsTokens: unsupported type of type! ");
    }
}
