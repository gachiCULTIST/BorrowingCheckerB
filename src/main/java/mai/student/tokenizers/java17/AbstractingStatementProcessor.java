package mai.student.tokenizers.java17;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
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

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: release
public class AbstractingStatementProcessor extends BasicStatementProcessor {

    // TODO: test
    public static Clock clock = Clock.systemDefaultZone();
    public static long test1 = 0;
    public static long test2 = 0;
    public static long test3 = 0;
    public static long test4 = 0;
    public static long test5 = 0;
    public static long test6 = 0;
    public static long test7 = 0;

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

        // resolve var
        assignExpr.getTarget().accept(visitor, this);
        if (assignExpr.getTarget().isNameExpr()) {
            IStructure search = IStructure.findEntity(files, function,
                    assignExpr.getTarget().asNameExpr().getNameAsString(), false, null);

            if (search.getStrucType() == StructureType.Variable &&
                    ((VariableOrConst) search).parent.getStrucType() == StructureType.Function) {
                variable = (VariableOrConst) search;
                skip = false;
            }
        }


        addToken(assignExpr.getOperator().asString());

        // resolve value
        assignExpr.getValue().accept(visitor, this);

        if (!skip) {
            assignExpr.getValue().accept(new ExpressionModifierVisitor(files, function), null);
            mai.student.intermediateStates.Type newType = resolvedTypeToMyType(assignExpr.getValue().calculateResolvedType());
            if (newType == null) {
                return;
            }

            newType.updateLink(function, (ArrayList<FileRepresentative>) files);
            variable.setRealType(newType);
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

        // TODO: delete test
        long start = clock.millis();
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
        test1 += clock.millis() - start;

        // TODO: delete test
        start = clock.millis();
        // method resolving
        try {
            objectCreationExpr.resolve().toAst().ifPresent(method -> {
                long innerStart = clock.millis();
                if (methodMatcher.containsKey(method) && methodMatcher.get(method) != function) {
                    DefinedFunction matchedFunc = methodMatcher.get(method);
                    test6 += clock.millis() - innerStart;

                    // TODO: rework type casting
                    innerStart = clock.millis();
                    matchedFunc.actuateTypes((ArrayList<FileRepresentative>) files);
                    test3 += clock.millis() - innerStart;

                    innerStart = clock.millis();
                    if (!matchedFunc.isTokenized()) {
                        AbstractingStatementProcessor processor = new AbstractingStatementProcessor(tokenDictionary, matchedFunc, files, new TokenizerVisitor(), methodMatcher);
                        processor.run();
                    }
                    test4 += clock.millis() - innerStart;

                    innerStart = clock.millis();
                    function.addFunctionTokens(matchedFunc);
                    test5 += clock.millis() - innerStart;
                }
            });
        } catch (UnsolvedSymbolException e) {
            if (e.getCause() == null) {
                // Откуда-то появились сторонние классы
                if (IStructure.findEntity(files, function, e.getName(), false, null) != null) {
                    throw e;
                }
            } else {
                if (e.getCause() instanceof UnsolvedSymbolException &&
                        IStructure.findEntity(files, function, ((UnsolvedSymbolException) e.getCause()).getName(),
                                false, null) != null) {
                    throw e;
                }
            }
        } catch (RuntimeException e) {
            if (!checkRuntimeException(e)) {
                throw e;
            }

            // TODO: rework - for multifile programs throw this expression wrapped by new custom class
        }
        test2 += clock.millis() - start;
    }

    @Override
    public void process(MethodCallExpr methodCallExpr) {
        // scope
        methodCallExpr.getScope().ifPresent(scope -> {
            addToken(IDENTIFIER);
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

        // TODO: delete test
        long start = clock.millis();
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
        test1 += clock.millis() - start;


        // TODO: delete
        System.out.println(methodCallExpr);


        // TODO: delete test
        start = clock.millis();
            // method resolving
        try {
            methodCallExpr.resolve().toAst().ifPresent(method -> {
                long innerStart = clock.millis();
                if (methodMatcher.containsKey(method) && methodMatcher.get(method) != function) {
                    DefinedFunction matchedFunc = methodMatcher.get(method);
                    test6 += clock.millis() - innerStart;

                    // TODO: rework type casting
                    innerStart = clock.millis();
                    matchedFunc.actuateTypes((ArrayList<FileRepresentative>) files);
                    test3 += clock.millis() - innerStart;

                    innerStart = clock.millis();
                    if (!matchedFunc.isTokenized()) {
                        AbstractingStatementProcessor processor = new AbstractingStatementProcessor(tokenDictionary, matchedFunc, files, new TokenizerVisitor(), methodMatcher);
                        processor.run();
                    }
                    test4 += clock.millis() - innerStart;

                    innerStart = clock.millis();
                    function.addFunctionTokens(matchedFunc);
                    test5 += clock.millis() - innerStart;
                }
            });
        } catch (UnsolvedSymbolException e) {
            if (e.getCause() == null) {
                // Откуда-то появились сторонние классы
                if (IStructure.findEntity(files, function, e.getName(), false, null) != null) {
                    throw e;
                }
            } else {
                if (e.getCause() instanceof UnsolvedSymbolException &&
                        IStructure.findEntity(files, function, ((UnsolvedSymbolException) e.getCause()).getName(),
                                false, null) != null) {
                    throw e;
                }
            }
        } catch (RuntimeException e) {
//            System.out.println(e.getCause() == null || e.getCause().getCause() == null || e.getCause().getCause().getCause() == null || e.getCause().getCause().getCause() instanceof UnsolvedSymbolException);
//            System.out.println(((UnsolvedSymbolException) e.getCause().getCause().getCause()).getName());
            if (!checkRuntimeException(e)) {
                throw e;
            }

            // TODO: rework - for multifile programs throw this expression wrapped by new custom class
        }
        test2 += clock.millis() - start;

//        start = clock.millis();
//        methodCallExpr.resolve().toAst();
//        test7 += clock.millis() - start;
    }

    private boolean checkRuntimeException(Throwable e) {
        if (e == null) {
            return false;
        }

        if (e instanceof UnsolvedSymbolException) {
            UnsolvedSymbolException use = (UnsolvedSymbolException) e;

            return IStructure.findEntity(files, function, use.getName(),false, null) == null;
        } else {
            return checkRuntimeException(e.getCause());
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

            ArrayList<mai.student.intermediateStates.Type> params = new ArrayList<>();
            for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : t.getTypeParametersMap()) {
                mai.student.intermediateStates.Type param = resolvedTypeToMyType(pair.b);
                if (param == null) {
                    return null;
                }

                params.add(param);
            }

            return new mai.student.intermediateStates.Type(t.getId(), params);
        }

        return null;
    }
}
