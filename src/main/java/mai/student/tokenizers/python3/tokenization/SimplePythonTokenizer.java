package mai.student.tokenizers.python3.tokenization;

import mai.student.tokenizers.CodeLanguage;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class SimplePythonTokenizer extends AbstractPythonTokenizer {

    public SimplePythonTokenizer(Path source, CodeLanguage lang) {
        super(source, lang);

        InputStream resourceToLoad = CLASS_LOADER.getResourceAsStream(DEFAULT_DICTIONARY);
        if (resourceToLoad == null) {
            LOGGER.severe(RESOURCE_MISSING_MESSAGE + DEFAULT_DICTIONARY);
            throw new RuntimeException(RESOURCE_MISSING_MESSAGE + DEFAULT_DICTIONARY);
        }

        try (Scanner scanner = new Scanner(resourceToLoad)) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokenMapping.put(scanner.nextLine().strip(), id);
            }
        }
    }

    @Override
    public void tokenize() {
        LOGGER.info("Tokenizing started.");
        result = new ArrayList<>();
        PyBasicTokenizerVisitor visitor = new PyNameAbstractingTokenizerVisitor(tokenMapping, result);
        this.files.get(0).getAst().accept(visitor, null);

//         Запись результатов теста
        printTree(result);
    }
}
