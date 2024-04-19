package mai.student.tokenizers.python3.tokenization;

import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.python3.preprocessing.PyAnalysisVisitor;
import mai.student.tokenizers.python3.preprocessing.PyMainFinderVisitor;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class ReducingPythonTokenizer extends AbstractPythonTokenizer{

    public ReducingPythonTokenizer(Path source, CodeLanguage lang) {
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
        LOGGER.info("Preprocess started.");
        PyAnalysisVisitor analysisVisitor = new PyAnalysisVisitor();
        this.files.forEach(f -> f.getAst().accept(analysisVisitor, f));
        PyFileRepresentative main = new PyMainFinderVisitor(this.files).getResult();

        LOGGER.info("Tokenizing started.");
        result = new ArrayList<>();
        PyBasicTokenizerVisitor visitor = new PyInitFileTokenizerVisitor(tokenMapping, files, main, result);
        main.setTokenized(true);
        main.getAst().accept(visitor, null);

//         Запись результатов теста
//        printTree(result);
//        System.out.println();
    }
}
