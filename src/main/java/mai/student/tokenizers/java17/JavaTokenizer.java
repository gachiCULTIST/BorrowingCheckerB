package mai.student.tokenizers.java17;

import com.github.javaparser.ast.body.CallableDeclaration;
import mai.student.UtilityClass;
import mai.student.intermediateStates.*;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.preprocessing.Preprocessor;

import java.io.FileWriter;
import java.nio.file.Path;
import java.rmi.UnexpectedException;
import java.time.Clock;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaTokenizer extends AbstractTokenizer {

    private static final Logger log = Logger.getLogger(JavaTokenizer.class.getName());

    private static final String DEFAULT_DICTIONARY = "tockenVocabularyJava17.txt";
    private static final ClassLoader cl = JavaTokenizer.class.getClassLoader();

    public JavaTokenizer(ArrayList<Path> files, CodeLanguage lang) throws Exception {
        super(files, lang);

        if (cl == null) {
            throw new UnexpectedException("JavaTokenizer: classloader not found?");
        }

        try (Scanner scanner = new Scanner(cl.getResourceAsStream(DEFAULT_DICTIONARY))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public JavaTokenizer(ArrayList<Path> files, CodeLanguage lang, String dictionary) throws Exception {
        super(files, lang);

        if (cl == null) {
            throw new UnexpectedException("JavaTokenizer: classloader not found?");
        }

        try (Scanner scanner = new Scanner(cl.getResourceAsStream(dictionary))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    // TODO: tets timer
    public static long totalTime = 0;

    // Непосредственно функция будет возвращать массив токенов метода Main сравниваемой программы
    public void tokenize() {
        log.info("Preprocessing started.");

        // TODO: for test - delete
        Clock timer = Clock.systemDefaultZone();

        Map<CallableDeclaration<?>, DefinedFunction> methodMatcher = new HashMap<>();
        for (FileRepresentative file : files) {
            long time = timer.millis();
            new Preprocessor(file, methodMatcher).preprocess();
            totalTime += timer.millis() - time;
//            System.out.println(file.code);
            UtilityClass.printInsideStructure(file, 0);
        }

        DefinedFunction mainFunc = findMainFunction();
        if (mainFunc == null) {
            log.log(Level.SEVERE, "Can't find main method");
            throw new UnsupportedOperationException("Can't find main method");
        }

        log.info("Tokenizing started.");
        BasicStatementProcessor processor = new AbstractingStatementProcessor(tokens, mainFunc, files, new TokenizerVisitor(), methodMatcher);
        processor.run();
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