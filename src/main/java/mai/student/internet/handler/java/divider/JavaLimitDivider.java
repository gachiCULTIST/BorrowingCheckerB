package mai.student.internet.handler.java.divider;

import com.github.javaparser.ast.CompilationUnit;
import mai.student.internet.common.File;
import mai.student.internet.reqeust.service.github.dto.CodeSearchRequestBuilder;
import mai.student.internet.reqeust.service.github.dto.enums.QueryLanguages;
import mai.student.internet.reqeust.service.github.dto.enums.QueryParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaLimitDivider extends AbstractJavaDivider {

    private static final int LENGTH_LIMIT = CodeSearchRequestBuilder.QUERY_LENGTH_LIMIT -
            QueryParams.LANGUAGE.getName().length() - CodeSearchRequestBuilder.JAVA_LANG_EXTRA_SYMBOLS -
            QueryLanguages.JAVA.getName().length();
//private static final int LENGTH_LIMIT = 200;

    @Override
    public List<String> divide(File file) {
        List<String> result = new ArrayList<>();
        CompilationUnit tree = getFilteredTree(file);

        // Нарезание
        List<String> lines = Arrays.stream(tree.toString().split("\n"))
                .filter(s -> !s.matches("\\s*"))
                .map(String::strip)
                .map(s -> s.replace("\"", "\\\""))
                .collect(Collectors.toList());

        // Склеивание
        int currentLength = 0;
        StringBuilder queryString = new StringBuilder();
        for (String line : lines) {
            if (currentLength + line.length() + 1 > LENGTH_LIMIT) {
                if (queryString.length() == 0) {
                    do {
                        int diff = LENGTH_LIMIT - currentLength - line.length();
                        queryString.append(line, 0, diff);
                        result.add(queryString.toString());
                        queryString = new StringBuilder();
                        currentLength = 0;
                        line = line.substring(diff);
                    } while (line.length() < LENGTH_LIMIT);


                    queryString.append(line)
                            .append(" ");
                    currentLength = line.length() + 1;
                    continue;
                }

                result.add(queryString.toString());
                queryString = new StringBuilder();
                currentLength = 0;

                if (line.length() >= LENGTH_LIMIT) {
                    do {
                        int diff = LENGTH_LIMIT - currentLength - line.length();
                        queryString.append(line, 0, diff);
                        result.add(queryString.toString());
                        queryString = new StringBuilder();
                        line = line.substring(diff);
                    } while (line.length() < LENGTH_LIMIT);
                }

                queryString.append(line);
                currentLength = line.length();
                continue;
            }

            queryString.append(line)
                    .append(" ");
            currentLength += line.length() + 1;
        }

        if (currentLength > 0) {
            result.add(queryString.toString());
        }

        return result;
    }
}
