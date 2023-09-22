package mai.student.internet.handler.java.divider;

import com.github.javaparser.ast.CompilationUnit;
import mai.student.internet.common.File;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaLineDivider extends AbstractJavaDivider {

    @Override
    public List<String> divide(File file) {
        List<String> result;

        CompilationUnit tree = getFilteredTree(file);

        // Нарезание
        result = Arrays.stream(tree.toString().split("\n"))
                .filter(s -> !s.matches("\\s*"))
                .map(String::strip)
                .collect(Collectors.toList());

        return screenQuotes(result);
    }

    private static List<String> screenQuotes(List<String> target) {
        return target.stream().map(s -> s.replace("\"", "\\\"")).collect(Collectors.toList());
    }
}
