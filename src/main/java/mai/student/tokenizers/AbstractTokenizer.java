package mai.student.tokenizers;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.intermediateStates.FileRepresentative;
import mai.student.tokenizers.java17.ReducingJavaTokenizer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class AbstractTokenizer {

    protected static final Logger LOGGER = Logger.getLogger(ReducingJavaTokenizer.class.getName());
    protected static final ClassLoader CLASS_LOADER = AbstractTokenizer.class.getClassLoader();
    protected static final String RESOURCE_MISSING_MESSAGE = "Missing token dictionary: ";
    protected static final String DEFAULT_DICTIONARY = "tockenVocabularyJava17.txt";

    protected CodeLanguage language;
    protected List<FileRepresentative> files;
    protected Map<String, Integer> tokenMapping;
    protected List<Integer> result;
    protected Path source;

    public AbstractTokenizer(Path source, CodeLanguage lang) {
        this.source = source;
        language = lang;

        this.files = new ArrayList<>();

        if (Files.isDirectory(source)) {
//            System.out.println(source);
            for (Path path : collectFiles(source)) {
                FileRepresentative file = new FileRepresentative(path);
                this.files.add(file);
            }
        } else {
            this.files.add(new FileRepresentative(source));
        }

        tokenMapping = new HashMap<>();
    }

    private List<Path> collectFiles(Path source) {
        try (Stream<Path> insides = Files.list(source)) {
            return insides.reduce(new ArrayList<>(),
                    (paths, path) -> {
                        if (Files.isDirectory(path)) {
                            paths.addAll(collectFiles(path));
                        } else {
                            if (path.toString().endsWith(language.getExtension())) {
                                paths.add(path);
                            }
                        }
                        return paths;
                    }, (paths1, paths2) -> {
                        paths1.addAll(paths2);
                        return paths1;
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("Wrong source path: " + source, e);
        }
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

    // Настройка парсера
    protected void configureParser(Path source) {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());

        if (Files.isDirectory(source)) {
            typeSolver.add(new JavaParserTypeSolver(source));
        }


        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false).
                setSymbolResolver(new JavaSymbolSolver(typeSolver)));
    }

    // Печать результата для дебага
    protected void printTree(List<Integer> tokens) {
        try (FileWriter saveResult = new FileWriter(files.get(0).getFilePath().getParent() + "/results/"
                + files.get(0).getFilePath().getFileName() + "_result.txt", false)) {
            StringBuilder result = new StringBuilder();
            for (int i : tokens) {
                String eq = "";
                for (Map.Entry<String, Integer> var : tokenMapping.entrySet()) {
                    if (var.getValue() == i) {
                        eq = var.getKey();
                    }
                }

                result.append(i).append(eq).append(" ");
                if (eq.equals("{") || eq.equals("}") || eq.equals(";")) {
                    result.append('\n');
                }
            }

            saveResult.write(result.toString());
            saveResult.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
