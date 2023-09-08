package mai.student.internet.handler.java;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.internet.AbstractInternetBorrowingFinder;
import mai.student.internet.common.Divider;
import mai.student.internet.common.FileWithSourceRating;
import mai.student.tokenizers.CodeLanguage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaInternetBorrowingFinder extends AbstractInternetBorrowingFinder {

    private Divider divider;

    public JavaInternetBorrowingFinder(Path source, CodeLanguage lang) {
        super(source, lang, FileWithSourceRating::new);
        configureParser(source);
        // TODO: статический парсер (посмотреть выходы)
    }

    @Override
    public void start() {

    }

    @Override
    public double getResult() {
        return 0;
    }

    @Override
    public URL getSource() {
        return null;
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
}
