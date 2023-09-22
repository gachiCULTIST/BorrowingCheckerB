package mai.student.tokenizers.java17;

import com.github.javaparser.ast.body.CallableDeclaration;
import mai.student.intermediateStates.*;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.preprocessing.Preprocessor;
import mai.student.tokenizers.java17.tokenization.FullAbstractingStatementProcessor;
import mai.student.tokenizers.java17.tokenization.BasicStatementProcessor;
import mai.student.tokenizers.java17.tokenization.TokenizerVisitor;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Clock;
import java.util.*;
import java.util.logging.Level;

public class ReducingJavaTokenizer extends AbstractTokenizer {

    public ReducingJavaTokenizer(Path source, CodeLanguage lang) {
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

    public ReducingJavaTokenizer(Path source, CodeLanguage lang, String dictionary) {
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

    // TODO: tets timer
    public static long totalPreprocessingTime = 0;
    public static long totalTokenizingTime = 0;


    // Непосредственно функция будет возвращать массив токенов метода Main сравниваемой программы
    public void tokenize() {
        LOGGER.info("Preprocessing started.");

        // TODO: for test - delete
        Clock timer = Clock.systemDefaultZone();

        Map<CallableDeclaration<?>, DefinedFunction> methodMatcher = new HashMap<>();
        for (FileRepresentative file : files) {
//            System.out.println("File: " + file.getFilePath().toString());
            long time = timer.millis();
            new Preprocessor(file, methodMatcher).preprocess();
            totalPreprocessingTime += timer.millis() - time;
//            System.out.println(file.code);

//            UtilityClass.printInsideStructure(file, 0);
        }

        DefinedFunction mainFunc = findMainFunction();
        if (mainFunc == null) {
            LOGGER.log(Level.SEVERE, "Can't find main method");
            throw new UnsupportedOperationException("Can't find main method");
        }

        LOGGER.info("Tokenizing started.");
        long start = timer.millis();
        BasicStatementProcessor processor = new FullAbstractingStatementProcessor(tokenMapping, mainFunc, files, new TokenizerVisitor(), methodMatcher);
        mainFunc.addTokens(processor.run());
        totalTokenizingTime += timer.millis() - start;
        result = mainFunc.tokens;


//         Запись результатов теста
        printTree(mainFunc.tokens);
    }

    private DefinedFunction findMainFunction() {
        for (FileRepresentative file : files) {
            for (DefinedClass innerClass : file.classes) {
                for (DefinedFunction func : innerClass.functions) {
                    if (func.getName().equals("main")) {
                        return func;
                    }
                }
            }
        }
        return null;
    }
}