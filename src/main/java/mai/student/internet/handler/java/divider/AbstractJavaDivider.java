package mai.student.internet.handler.java.divider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import lombok.Getter;
import lombok.Setter;
import mai.student.internet.common.Divider;
import mai.student.internet.common.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractJavaDivider implements Divider {

    protected List<ModifierVisitor<?>> filterChain = new ArrayList<>();

    public void addFilter(ModifierVisitor<?> filter) {
        filterChain.add(filter);
    }

    protected CompilationUnit getFilteredTree(File file) {
        CompilationUnit tree;
        try {
            tree = StaticJavaParser.parse(file.getFilePath());
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка парсинга файла: " + file.getFilePath(), ex);
        }

        // Фильтрыыыыыы
        filterChain.forEach(f -> tree.accept(f, null));

        return tree;
    }
}
