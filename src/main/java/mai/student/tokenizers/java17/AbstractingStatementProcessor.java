package mai.student.tokenizers.java17;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.utils.Pair;
import mai.student.intermediateStates.*;
import mai.student.utility.EntitySearchers;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractingStatementProcessor extends BasicStatementProcessor {

    protected static String IDENTIFIER = "*ident*";

    protected Map<CallableDeclaration<?>, DefinedFunction> methodMatcher;

    public AbstractingStatementProcessor(Map<String, Integer> tokenDictionary, DefinedFunction function, List<FileRepresentative> files, VoidVisitor<StatementProcessor> visitor, Map<CallableDeclaration<?>, DefinedFunction> methodMatcher) {
        super(tokenDictionary, function, files, visitor);

        this.methodMatcher = methodMatcher;
    }

    // Учитываем изменение значений только локальных переменных
    @Override
    public void process(AssignExpr assignExpr) {
        boolean skip = true;
        VariableOrConst variable = null;

        // resolve local var
        assignExpr.getTarget().accept(visitor, this);
        if (assignExpr.getTarget().isNameExpr()) {
            VariableOrConst var = EntitySearchers.findVariable(files, function,
                    assignExpr.getTarget().asNameExpr().getNameAsString(), false);

            if (var.getParent().getStrucType() == StructureType.Function) {
                variable = var;
                skip = false;
            }
        }


        addToken(assignExpr.getOperator().asString());

        // resolve value
        assignExpr.getValue().accept(visitor, this);

        try {
            if (!skip) {
                assignExpr.getValue().accept(new ExpressionModifierVisitor(files, function), null);
                mai.student.intermediateStates.Type newType = resolvedTypeToMyType(assignExpr.getValue().calculateResolvedType());
                if (newType == null) {
                    return;
                }

                newType.updateLink(function, files);
                variable.setRealType(newType);
            }
        } catch (UnsolvedSymbolException e) {
            if (e.getCause() == null) {
                // Откуда-то появились сторонние классы
                if (EntitySearchers.findClass(files, function, e.getName()) == null) {
                    throw new MissingTypeException("Missing type in source code: " + e.getName(), e);
                }
            } else {
                if (e.getCause() instanceof UnsolvedSymbolException &&
                        EntitySearchers.findClass(files, function,
                                ((UnsolvedSymbolException) e.getCause()).getName()) == null) {
                    throw new MissingTypeException("Missing type in source code: " +
                            ((UnsolvedSymbolException) e.getCause()).getName(), e);
                }
            }

            throw e;
        } catch (RuntimeException e) {
            if (checkMissingTypeInRuntimeException(e)) {
                throw new MissingTypeException("Missing type in source code: " +
                        getNameOfInnerUnsolvedSymbolException(e.getCause()), e);
            }

            throw e;
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

            // Find variable in structure
            VariableOrConst variable = EntitySearchers.findVariable(files, function,
                    var.getNameAsString(), false);


            // initializer
            var.getInitializer().ifPresent(init -> {
                addToken(ASSIGN);
                init.accept(visitor, this);

                if (init.isArrayInitializerExpr()) {
                    return;
                }

                try {
                    init.accept(new ExpressionModifierVisitor(files, function), null);

                    mai.student.intermediateStates.Type newType = resolvedTypeToMyType(init.calculateResolvedType());
                    if (newType == null) {
                        return;
                    }

                    newType.updateLink(function, files);
                    variable.setRealType(newType);
                } catch (UnsolvedSymbolException e) {
                    if (e.getCause() == null) {
                        // Откуда-то появились сторонние классы
                        if (EntitySearchers.findClass(files, function, e.getName()) == null) {
                            throw new MissingTypeException("Missing type in source code: " + e.getName(), e);
                        }
                    } else {
                        if (e.getCause() instanceof UnsolvedSymbolException &&
                                EntitySearchers.findClass(files, function,
                                        ((UnsolvedSymbolException) e.getCause()).getName()) == null) {
                            throw new MissingTypeException("Missing type in source code: " +
                                    ((UnsolvedSymbolException) e.getCause()).getName(), e);
                        }
                    }

                    throw e;
                } catch (RuntimeException e) {
                    if (checkMissingTypeInRuntimeException(e)) {
                        throw new MissingTypeException("Missing type in source code: " +
                                getNameOfInnerUnsolvedSymbolException(e.getCause()), e);
                    }

                    throw e;
                }
            });
        }
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

        // params processing
        addToken(LEFT_PAREN);
        boolean isNotFirst = false;
        for (Expression arg : objectCreationExpr.getArguments()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
            arg.accept(new ExpressionModifierVisitor(files, function), null);
        }
        addToken(RIGHT_PAREN);

        // method resolving
        try {
            objectCreationExpr.resolve().toAst().ifPresent(method -> {
                if (methodMatcher.containsKey(method) && methodMatcher.get(method) != function) {
                    DefinedFunction matchedFunc = methodMatcher.get(method);

                    matchedFunc.actuateTypes(files);

                    if (!matchedFunc.isTokenized()) {
                        AbstractingStatementProcessor processor = new AbstractingStatementProcessor(tokenDictionary, matchedFunc, files, new TokenizerVisitor(), methodMatcher);
                        processor.run();
                    }

                    function.addFunctionTokens(matchedFunc);
                }
            });
        } catch (UnsolvedSymbolException e) {
            if (e.getCause() == null) {
                // Откуда-то появились сторонние классы
                if (EntitySearchers.findClass(files, function, e.getName()) == null) {
                    //TODO: uncomment
//                    throw new MissingTypeException("Missing type in source code: " + e.getName(), e);
                }
            } else {
                if (e.getCause() instanceof UnsolvedSymbolException &&
                        EntitySearchers.findClass(files, function,
                                ((UnsolvedSymbolException) e.getCause()).getName()) == null) {

                    //TODO: uncomment
//                    throw new MissingTypeException("Missing type in source code: " +
//                            ((UnsolvedSymbolException) e.getCause()).getName(), e);
                }
            }

//            throw e;
        } catch (RuntimeException e) {
            if (checkMissingTypeInRuntimeException(e)) {
                //TODO: uncomment
//                throw new MissingTypeException("Missing type in source code: " +
//                        getNameOfInnerUnsolvedSymbolException(e.getCause()), e);
            }

            throw e;
        }
    }

    @Override
    public void process(MethodCallExpr methodCallExpr) {
        // scope
        methodCallExpr.getScope().ifPresent(scope -> {
            scope.accept(visitor, this);
            scope.accept(new ExpressionModifierVisitor(files, function), null);
            addToken(DOT);
        });


        // generic types
        Optional<NodeList<Type>> typeArguments = methodCallExpr.getTypeArguments();
        if (typeArguments.isPresent()) {
            addToken(LOWER);
            addTypeElemList(typeArguments.get(), COMMA);
            addToken(GREATER);
        }

        // id
        addToken(methodCallExpr.getName().asString());

        // params processing
        addToken(LEFT_PAREN);
        boolean isNotFirst = false;
        for (Expression arg : methodCallExpr.getArguments()) {
            if (isNotFirst) {
                addToken(COMMA);
            } else {
                isNotFirst = true;
            }

            arg.accept(visitor, this);
            arg.accept(new ExpressionModifierVisitor(files, function), null);
        }
        addToken(RIGHT_PAREN);

        // method resolving
        try {
            methodCallExpr.resolve().toAst().ifPresent(method -> {
                if (methodMatcher.containsKey(method) && methodMatcher.get(method) != function) {
                    DefinedFunction matchedFunc = methodMatcher.get(method);

                    matchedFunc.actuateTypes(files);

                    if (!matchedFunc.isTokenized()) {
                        AbstractingStatementProcessor processor = new AbstractingStatementProcessor(tokenDictionary, matchedFunc, files, new TokenizerVisitor(), methodMatcher);
                        processor.run();
                    }

                    function.addFunctionTokens(matchedFunc);
                }
            });
        } catch (UnsolvedSymbolException e) {
            if (e.getCause() == null) {
                // Откуда-то появились сторонние классы
                if (EntitySearchers.findClass(files, function, e.getName()) == null) {
                    throw new MissingTypeException("Missing type in source code: " + e.getName(), e);
                }
            } else {
                if (e.getCause() instanceof UnsolvedSymbolException &&
                        EntitySearchers.findClass(files, function, ((UnsolvedSymbolException) e.getCause()).getName()) == null) {
                    throw new MissingTypeException("Missing type in source code: " +
                            ((UnsolvedSymbolException) e.getCause()).getName(), e);
                }
            }

            throw e;
        } catch (RuntimeException e) {
            if (checkMissingTypeInRuntimeException(e)) {
                throw new MissingTypeException("Missing type in source code: " +
                        getNameOfInnerUnsolvedSymbolException(e.getCause()), e);
            }

            throw e;
        }
    }

    private boolean checkMissingTypeInRuntimeException(Throwable e) {
        if (e == null) {
            return false;
        }

        if (e instanceof UnsolvedSymbolException) {
            UnsolvedSymbolException use = (UnsolvedSymbolException) e;

            return EntitySearchers.findClass(files, function, use.getName()) == null;
        } else {
            return checkMissingTypeInRuntimeException(e.getCause());
        }
    }

    private String getNameOfInnerUnsolvedSymbolException(Throwable e) {
        if (e == null) {
            return null;
        }

        if (e instanceof UnsolvedSymbolException) {
            return ((UnsolvedSymbolException) e).getName();
        } else {
            return getNameOfInnerUnsolvedSymbolException(e.getCause());
        }
    }

    @Override
    protected void addToken(String lexeme) {
        if (tokenDictionary.containsKey(lexeme)) {
            function.addToken(tokenDictionary.get(lexeme));
            return;
        }

        function.addToken(tokenDictionary.get(IDENTIFIER));
    }

    @Override
    protected void addTypeAsTokens(Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType t = type.asClassOrInterfaceType();

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

        super.addTypeAsTokens(type);
    }

    @Override
    protected void addComplexName(Name name) {
        addToken(IDENTIFIER);
    }

    protected static mai.student.intermediateStates.Type resolvedTypeToMyType(ResolvedType type) {
        if (type.isPrimitive()) {
            return new mai.student.intermediateStates.Type(type.toString(), true);
        }
        if (type.isReferenceType()) {
            ResolvedReferenceType t = (ResolvedReferenceType) type;

            // Обработка квалифаера
            Qualifier qualifier = null;
            String[] qParts = t.getId().split("\\.");
            if (qParts.length > 1) {
                qualifier = new Qualifier(Arrays.stream(qParts).limit(qParts.length - 1).collect(Collectors.joining(".")));
            }

            // Обработка параметров типа
            ArrayList<mai.student.intermediateStates.Type> params = new ArrayList<>();
            for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : t.getTypeParametersMap()) {
                mai.student.intermediateStates.Type param = resolvedTypeToMyType(pair.b);
                if (param == null) {
                    return null;
                }

                params.add(param);
            }

            return new mai.student.intermediateStates.Type(qParts[qParts.length - 1], params, qualifier);
        }

        return null;
    }
}
