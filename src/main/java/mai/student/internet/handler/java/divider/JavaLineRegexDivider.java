package mai.student.internet.handler.java.divider;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mai.student.internet.common.File;
import mai.student.internet.handler.java.filter.AbstractNameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
public class JavaLineRegexDivider extends AbstractJavaDivider {
    private ModifierVisitor<Void> finalizer = new AbstractNameFilter();

    @Override
    public List<String> divide(File file) {
        List<String> result;

        CompilationUnit tree = getFilteredTree(file);
        tree.accept(finalizer, null);

        //Экранирование спецсимволов
        String stringed = tree.toString();
        List<RegexPoint> regexes = locateRegex(stringed);
        StringBuilder semiResult = screen(stringed, regexes);

        // Нарезание
        result = Arrays.stream(unscreenNames(semiResult, regexes).split("\n"))
                .filter(s -> !s.matches("\\s*"))
                .map(String::strip)
                .collect(Collectors.toList());

        return result;
    }

    private static List<RegexPoint> locateRegex(String target) {
        List<RegexPoint> result = new ArrayList<>();

        int pointer = 0;
        do {
            pointer = target.indexOf(AbstractNameFilter.SCREENED_REPLACER, pointer);
            if (pointer == -1) {
                return result;
            }

            result.add(new RegexPoint(pointer, pointer + AbstractNameFilter.SCREENED_REPLACER.length()));
            pointer++;
        } while (true);
    }

    private static StringBuilder screen(String target, List<RegexPoint> regexes) {
        int offset = 0;
        int pointer = 0;
        //  [ ] \ / ^ $ . | ? * + ( ) { }
        Pattern pattern = Pattern.compile("[\\[\\]{}\\\\/^$.|?*+()]");
        StringBuilder result = new StringBuilder(target);
        Matcher matcher = pattern.matcher(target);

        for (RegexPoint reg : regexes) {
            while (true) {
                try {
                    if (matcher.start() >= reg.start) {
                        if (matcher.start() < reg.end) {
                            pointer = reg.end;
                            matcher.find(pointer);
                        }
                        break;
                    }
                } catch (IllegalStateException ex) {
                    if (!matcher.find(pointer)) {
                        break;
                    }

                    if (matcher.start() >= reg.start) {
                        if (matcher.start() < reg.end) {
                            pointer = reg.end;
                            matcher.find(pointer);
                        }
                        break;
                    }
                }

                result.insert(matcher.start() + offset, "\\");
                offset++;
                pointer = matcher.end();

                if (!matcher.find(pointer)) {
                    break;
                }

                if (matcher.start() >= reg.start) {
                    if (matcher.start() < reg.end) {
                        pointer = reg.end;
                        matcher.find(pointer);
                    }
                    break;
                }
            }

            pointer = reg.end;
            reg.start += offset;
            reg.end += offset;
        }

        // Проверить конец
        while (matcher.find(pointer)) {
            result.insert(matcher.start() + offset, "\\");
            offset++;
            pointer = matcher.end();
            if (pointer >= target.length()) {
                break;
            }
        }

        return result;
    }

    private static String unscreenNames(StringBuilder target, List<RegexPoint> regexes) {
        StringBuilder result = new StringBuilder(target);
        int offset = 0;

        for (RegexPoint reg : regexes) {
            result.replace(reg.start - offset, reg.end - offset, AbstractNameFilter.SIMPLE_NAME_PATTERN);
            offset += AbstractNameFilter.SCREENED_REPLACER.length() - AbstractNameFilter.SIMPLE_NAME_PATTERN.length();
        }

        return result.toString();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RegexPoint {

        private int start;
        private int end;
    }
}
