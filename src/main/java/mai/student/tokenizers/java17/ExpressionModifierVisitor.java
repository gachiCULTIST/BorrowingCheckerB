package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mai.student.intermediateStates.*;
import mai.student.utility.EntitySearchers;

import java.util.List;

public class ExpressionModifierVisitor extends VoidVisitorAdapter<Void> {

    private final List<FileRepresentative> files;

    private final IStructure scope;

    public ExpressionModifierVisitor(List<FileRepresentative> files, IStructure scope) {
        this.files = files;
        this.scope = scope;
    }

    @Override
    public void visit(NameExpr nameExpr, Void arg) {
        VariableOrConst variable = EntitySearchers.findVariable(files, scope, nameExpr.getNameAsString(), false);

        if (variable != null) {
            variable.actuateTypes(files);

            if (variable.getReplacer() != null) {
                nameExpr.replace(variable.getReplacer());
            }
        }
    }

    @Override
    public void visit(FieldAccessExpr fieldAccessExpr, Void arg) {}
}
