package mai.student.tokenizers.java17.tokenization;

import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.util.Map;

public class NameAbstractingStatementProcessor extends BasicStatementProcessor {

    protected static String IDENTIFIER = "*ident*";

    public NameAbstractingStatementProcessor(Map<String, Integer> tokenDictionary, Visitable visitable, VoidVisitor<StatementProcessor> visitor) {
        super(tokenDictionary, visitable, visitor);
    }


    @Override
    protected void addToken(String lexeme) {
        System.out.println("TOKEN: " + lexeme);

        if (tokenDictionary.containsKey(lexeme)) {
            result.add(tokenDictionary.get(lexeme));
            return;
        }

        result.add(tokenDictionary.get(IDENTIFIER));
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
