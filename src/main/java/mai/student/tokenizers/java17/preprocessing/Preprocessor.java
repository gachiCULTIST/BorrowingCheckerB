package mai.student.tokenizers.java17.preprocessing;

import com.github.javaparser.*;
import com.github.javaparser.ast.body.CallableDeclaration;
import mai.student.intermediateStates.java.DefinedFunction;
import mai.student.intermediateStates.java.FileRepresentative;

import java.io.IOException;
import java.util.*;

public class Preprocessor {

    private final FileRepresentative file;
    private final Map<CallableDeclaration<?>, DefinedFunction> methodMatcher;

    public Preprocessor(FileRepresentative file, Map<CallableDeclaration<?>, DefinedFunction> methodMatcher) {
        this.file = file;
        this.methodMatcher = methodMatcher;
    }

    public void preprocess() {
        try {
            new AnalysisVisitor(methodMatcher).visit(StaticJavaParser.parse(file.getFilePath()), file);
        } catch (IOException e) {
            //TODO: пока буду пробрасывать такое исключение - надо заменить
            throw new RuntimeException(e.getMessage());
        }
    }
}