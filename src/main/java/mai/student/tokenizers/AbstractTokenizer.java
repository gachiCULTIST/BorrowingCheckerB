package mai.student.tokenizers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public abstract class AbstractTokenizer<T> {

    protected static final Logger LOGGER = Logger.getLogger(AbstractTokenizer.class.getName());
    protected static final ClassLoader CLASS_LOADER = AbstractTokenizer.class.getClassLoader();
    protected static final String RESOURCE_MISSING_MESSAGE = "Missing token dictionary: ";

    protected CodeLanguage language;
    protected List<T> files;
    protected Map<String, Integer> tokenMapping;
    protected List<Integer> result;
    protected Path source;

    public AbstractTokenizer(Path source, CodeLanguage lang) {
        this.source = source;
        language = lang;
    }

    public List<Integer> getResult() {
        return result;
    }

    public CodeLanguage getLanguage() {
        return language;
    }

    public abstract void tokenize();

    public static void setLoggerHandler(FileHandler fileHandler) {
        LOGGER.setUseParentHandlers(false);
        if (fileHandler == null) {
            return;
        }
        LOGGER.addHandler(fileHandler);
    }
}
