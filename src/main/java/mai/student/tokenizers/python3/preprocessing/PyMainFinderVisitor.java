package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.tokenizers.NoStartPointException;
import mai.student.tokenizers.python3.ast.nodes.control.PyIf;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyBoolOp;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCompare;
import mai.student.tokenizers.python3.ast.nodes.literals.PyConstant;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

import java.util.List;

import static mai.student.tokenizers.python3.ast.nodes.operator.Operator.Instance.EQ;

@Getter
public class PyMainFinderVisitor extends AbstractPyVoidVisitor<Object> {

    private static final String NAME_VAR = "__name__";
    private static final String MAIN = "__main__";

    private final PyFileRepresentative result;
    private boolean found = false;

    public PyMainFinderVisitor(List<PyFileRepresentative> files) {
        if (files == null || files.isEmpty()) {
            throw new NoStartPointException("Нет точки входа");
        }

        if (files.size() == 1) {
            result = files.get(0);
            return;
        }

        for (PyFileRepresentative f : files) {
            f.getAst().accept(this, null);

            if (found) {
                result = f;
                return;
            }
        }

        result = files.get(0);
    }

    @Override
    public void visit(PyIf element, Object arg) {
        if (element.getTest() instanceof PyCompare) {
            PyCompare test = (PyCompare) element.getTest();

            if (test.getOps().size() == 1 && test.getOps().get(0).getSelfOps() == EQ && test.getLeft() instanceof PyName
            && ((PyName) test.getLeft()).getId().equals(NAME_VAR) && test.getComparators().get(0) instanceof PyConstant
            && ((PyConstant) test.getComparators().get(0)).getValue().equals(MAIN)) {
                found = true;
            }
        }
    }
}
