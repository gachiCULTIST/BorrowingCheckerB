package mai.student.tokenizers.java17;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import mai.student.internet.handler.java.filter.DeleteImportFilter;
import mai.student.internet.handler.java.filter.DeletePackageFilter;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.tokenization.BasicStatementProcessor;
import mai.student.tokenizers.java17.tokenization.NameAbstractingStatementProcessor;
import mai.student.tokenizers.java17.tokenization.TokenizerVisitor;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;

public class SimpleJavaTokenizer extends AbstractTokenizer {


    public SimpleJavaTokenizer(Path source, CodeLanguage lang) {
        super(source, lang);
        configureParser(source);

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

    public SimpleJavaTokenizer(Path source, CodeLanguage lang, String dictionary) {
        super(source, lang);
        configureParser(source);

        try (Scanner scanner = new Scanner(new FileReader(dictionary))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokenMapping.put(scanner.nextLine().strip(), id);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(RESOURCE_MISSING_MESSAGE + dictionary);
        }
    }

    @Override
    public void tokenize() {
        LOGGER.info("Preprocessing started.");
        CompilationUnit tree;
        try {
            tree = StaticJavaParser.parse(files.get(0).getFilePath());
        } catch (IOException e) {
            //TODO: пока буду пробрасывать такое исключение - надо заменить
            throw new RuntimeException(e.getMessage());
        }
        tree.accept(new DeletePackageFilter(), null);
        tree.accept(new DeleteImportFilter(), null);

        LOGGER.info("Tokenizing started.");
        BasicStatementProcessor processor = new NameAbstractingStatementProcessor(tokenMapping, tree, new TokenizerVisitor());
        result = processor.run();

//         Запись результатов теста
        printTree(result);
    }
}
