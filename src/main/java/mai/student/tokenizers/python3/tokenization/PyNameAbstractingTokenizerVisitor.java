package mai.student.tokenizers.python3.tokenization;

import java.util.List;
import java.util.Map;

public class PyNameAbstractingTokenizerVisitor extends PyBasicTokenizerVisitor {

    public PyNameAbstractingTokenizerVisitor(Map<String, Integer> tokenDictionary, List<Integer> result) {
        super(tokenDictionary, result);
    }

    @Override
    protected void addToken(String lexeme) {
        if (!tokenDictionary.containsKey(lexeme)) {
            result.add(tokenDictionary.get(IDENTIFIER));
        } else {
            result.add(tokenDictionary.get(lexeme));
        }
    }
}
