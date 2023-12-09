package mai.student.internet.handler.java.filter;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.ModifierVisitor;

public class DeleteImportFilter extends ModifierVisitor<Void> {

    @Override
    public Node visit(ImportDeclaration n, Void arg) {
        return null;
    }
}
