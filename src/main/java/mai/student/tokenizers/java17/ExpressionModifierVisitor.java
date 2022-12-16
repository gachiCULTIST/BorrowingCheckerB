package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mai.student.intermediateStates.*;

import java.util.ArrayList;
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
        IStructure variable = IStructure.findEntity(files, scope, nameExpr.getNameAsString(), false, null);

        if (variable != null && variable.getStrucType() == StructureType.Variable) {
            VariableOrConst var = (VariableOrConst) variable;
            // TODO: rework field and method parameters types
            var.actuateTypes((ArrayList<FileRepresentative>) files);

            if (var.getReplacer() != null) {
                nameExpr.replace(var.getReplacer());
            }
        }
    }
}
