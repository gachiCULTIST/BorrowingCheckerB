package mai.student.tokenizers.java17.preprocessing;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.intermediateStates.*;

import java.io.IOException;
import java.util.*;

public class Preprocessor {

    private static final String TYPE_ARRAY = "Array";

    private final FileRepresentative file;

    private final Map<CallableDeclaration<?>, DefinedFunction> methodMatcher;

    public Preprocessor(FileRepresentative file, Map<CallableDeclaration<?>, DefinedFunction> methodMatcher) {
        this.file = file;
        this.methodMatcher = methodMatcher;

        // Настройка парсера
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        // TODO: договориться о принципе получения директории с исходниками
        //  тестирование с данной функций обрабатывает все файлы в директории
        //  (для прогонки по всем работам ведет к проблемам производительности)
//        typeSolver.add(new JavaParserTypeSolver(file.getFilePath().getParent()));

        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false).
                setSymbolResolver(new JavaSymbolSolver(typeSolver)).
                setLanguageLevel(ParserConfiguration.LanguageLevel.CURRENT));
    }

    public void preprocess() {

        try {
            new AnalysisVisitor(methodMatcher).visit(StaticJavaParser.parse(file.getFilePath()), file);
        } catch (IOException e) {
            //TODO: пока буду пробрасывать такое исключение - надо заменить
            throw new RuntimeException(e.getMessage());
        }

        // TODO: set new implementation
//        Cleaner cleaner = new Cleaner(file);
//        file.code = cleaner.clean();
//
//        structAnalyze(file.code, file, 0, file.code.length() - 1);

    }

    public static Type generateArray(int dimensions, Type innerType) {
        if (dimensions == 0) {
            return innerType;
        }

        Type result = new Type(TYPE_ARRAY);
        Type curDimension = result;
        for (int i = 1; i < dimensions; ++i) {
            Type tempType = new Type(TYPE_ARRAY);
            curDimension.getParams().add(tempType);
            curDimension = tempType;
        }
        curDimension.getParams().add(innerType);
        return result;
    }
}