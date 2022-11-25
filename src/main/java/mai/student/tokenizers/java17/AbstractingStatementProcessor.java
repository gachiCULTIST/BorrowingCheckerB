package mai.student.tokenizers.java17;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import mai.student.intermediateStates.DefinedFunction;
import mai.student.intermediateStates.FileRepresentative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: release
public class AbstractingStatementProcessor extends BasicStatementProcessor {

    protected static String IDENTIFIER = "*ident*";

    protected Map<CallableDeclaration<?>, DefinedFunction> methodMatcher;

    public AbstractingStatementProcessor(Map<String, Integer> tokenDictionary, DefinedFunction function, List<FileRepresentative> files, VoidVisitor<StatementProcessor> visitor, Map<CallableDeclaration<?>, DefinedFunction> methodMatcher) {
        super(tokenDictionary, function, files, visitor);

        this.methodMatcher = methodMatcher;
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


        // TODO: delete
        System.out.println(methodCallExpr);

            // method resolving
        methodCallExpr.resolve().toAst().ifPresent(method -> {
            if (methodMatcher.containsKey(method)) {
                DefinedFunction matchedFunc = methodMatcher.get(method);
                // TODO: rework type casting
                matchedFunc.actuateTypes((ArrayList<FileRepresentative>) files);

                if (!matchedFunc.isTokenized()) {
                    AbstractingStatementProcessor processor = new AbstractingStatementProcessor(tokenDictionary, matchedFunc, files, new TokenizerVisitor(), methodMatcher);
                    processor.run();
                }

                function.addFunctionTokens(matchedFunc);
            }
        });

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

}
