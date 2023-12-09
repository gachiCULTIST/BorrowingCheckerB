package mai.student;

import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.SimpleJavaTokenizer;
import mai.student.utility.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public class SimpleCodeComparer extends AbstractCodeComparer {

    @Override
    protected List<Integer> tokenizeOnSetUp(Path source, CodeLanguage lang) {
        if (Files.notExists(source) && Files.isDirectory(source)) {
            throw new UnsupportedOperationException("Path must leads to existing file.");
        }

        AbstractTokenizer tokenizer;
        if (lang == null) {
            lang = UtilityClass.getLanguage(source);
            if (lang == null) {
                throw new UnsupportedOperationException("Language of the project is not defined!");
            }
        }
        switch (lang) {
            case Java:
                tokenizer = new SimpleJavaTokenizer(source, lang);
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
