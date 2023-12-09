package mai.student.internet.handler.java.filter;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class DeletePackageFilter extends ModifierVisitor<Void> {

    @Override
    public Visitable visit(PackageDeclaration n, Void arg) {
        return null;
    }
}
