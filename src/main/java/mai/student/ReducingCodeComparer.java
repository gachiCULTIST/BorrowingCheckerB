package mai.student;

import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.ReducingJavaTokenizer;
import mai.student.utility.UtilityClass;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class ReducingCodeComparer extends AbstractCodeComparer {

    @Override
    protected List<Integer> tokenizeOnSetUp(Path source) {
        AbstractTokenizer tokenizer;
        CodeLanguage lang = UtilityClass.getLanguage(source);
        switch (lang) {
            case Java:
                tokenizer = new ReducingJavaTokenizer(source, lang);
                break;
            case C:
                LOGGER.log(Level.SEVERE, "Unsupported file extension: C/C++ is not supported yet!");
                throw new UnsupportedOperationException("C/C++ is not supported yet!");
            case Python:
                LOGGER.log(Level.SEVERE, "Python is not supported yet!");
                throw new UnsupportedOperationException("Python is not supported yet!");
            default:
                LOGGER.log(Level.SEVERE, "Unknown language. Hables Espanol?");
                throw new UnsupportedOperationException("Unknown language. Hables Espanol?");
        }

        tokenizer.tokenize();
        return tokenizer.getResult();
    }
}