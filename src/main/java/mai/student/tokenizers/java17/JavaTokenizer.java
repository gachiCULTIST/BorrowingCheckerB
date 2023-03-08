package mai.student.tokenizers.java17;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.intermediateStates.*;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.preprocessing.Preprocessor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaTokenizer extends AbstractTokenizer {

    private static final Logger log = Logger.getLogger(JavaTokenizer.class.getName());
    private static final String resourceMissingMessage = "Missing token dictionary: ";
    private static final String DEFAULT_DICTIONARY = "tockenVocabularyJava17.txt";
    private static final ClassLoader cl = JavaTokenizer.class.getClassLoader();

    public JavaTokenizer(Path source, CodeLanguage lang) {
        super(source, lang);
        configureParser(source);

        InputStream resourceToLoad = cl.getResourceAsStream(DEFAULT_DICTIONARY);
        if (resourceToLoad == null) {
            log.severe(resourceMissingMessage + DEFAULT_DICTIONARY);
            throw new RuntimeException(resourceMissingMessage + DEFAULT_DICTIONARY);
        }

        try (Scanner scanner = new Scanner(resourceToLoad)) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        }
    }

    public JavaTokenizer(Path source, CodeLanguage lang, String dictionary) {
        super(source, lang);
        configureParser(source);

        try (Scanner scanner = new Scanner(new FileReader(dictionary))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(resourceMissingMessage + dictionary);
        }
    }

    // TODO: tets timer
    public static long totalPreprocessingTime = 0;
    public static long totalTokenizingTime = 0;


    // Непосредственно функция будет возвращать массив токенов метода Main сравниваемой программы
    public void tokenize() {
        log.info("Preprocessing started.");

        // TODO: for test - delete
        Clock timer = Clock.systemDefaultZone();

        Map<CallableDeclaration<?>, DefinedFunction> methodMatcher = new HashMap<>();
        for (FileRepresentative file : files) {
            System.out.println("File: " + file.getFilePath().toString());
            long time = timer.millis();
            new Preprocessor(file, methodMatcher).preprocess();
            totalPreprocessingTime += timer.millis() - time;
//            System.out.println(file.code);

//            UtilityClass.printInsideStructure(file, 0);
        }

        DefinedFunction mainFunc = findMainFunction();
        if (mainFunc == null) {
            log.log(Level.SEVERE, "Can't find main method");
            throw new UnsupportedOperationException("Can't find main method");
        }

        log.info("Tokenizing started.");
        long start = timer.millis();
        BasicStatementProcessor processor = new AbstractingStatementProcessor(tokens, mainFunc, files, new TokenizerVisitor(), methodMatcher);
        processor.run();
        totalTokenizingTime += timer.millis() - start;
        result = mainFunc.tokens;


//         Запись результатов теста
//        try (FileWriter saveResult = new FileWriter(files.get(0).getFilePath().getParent() + "/results/"
//                + files.get(0).getFilePath().getFileName() + "_result.txt", false)) {
//            StringBuilder result = new StringBuilder();
//            for (int i : mainFunc.tokens) {
//                String eq = "";
//                for (Map.Entry<String, Integer> var : tokens.entrySet()) {
//                    if (var.getValue() == i) {
//                        eq = var.getKey();
//                    }
//                }
//
//                result.append(i).append(eq).append(" ");
//                if (eq.equals("{") || eq.equals("}") || eq.equals(";")) {
//                    result.append('\n');
//                }
//            }
//
//            saveResult.write(result.toString());
//            saveResult.flush();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
    }

    // Настройка парсера
    private void configureParser(Path source) {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());

        if (Files.isDirectory(source)) {
            typeSolver.add(new JavaParserTypeSolver(source));
        }


        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false).
                setSymbolResolver(new JavaSymbolSolver(typeSolver)));
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

    public static void setLoggerHandler(FileHandler fileHandler) {
        log.setUseParentHandlers(false);
        if (fileHandler == null) {
            return;
        }
        log.addHandler(fileHandler);
    }
}