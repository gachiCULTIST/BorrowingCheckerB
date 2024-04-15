package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import mai.student.intermediateStates.python.PyType;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyAttribute;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCall;
import mai.student.tokenizers.python3.ast.nodes.literals.PyConstant;
import mai.student.tokenizers.python3.ast.nodes.literals.PyTuple;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PyClassBaseConstructorVisitor extends AbstractPyVoidVisitor<Object> {

    private final PyType result = new PyType();

    public PyClassBaseConstructorVisitor(PyNode base) {
        base.accept(this, null);
    }

    @Override
    public void visit(PyCall element, Object arg) {
        element.getFunc().accept(this, arg);
    }

    @Override
    public void visit(PyConstant element, Object arg) {
        result.setName(element.getValue());
    }

    @Override
    public void visit(PyName element, Object arg) {
        result.setName(element.getId());
    }

    @Override
    public void visit(PySubscript element, Object arg) {
        element.getValue().accept(this, arg);

        List<PyType> params = new ArrayList<>();
        if (element.getSlice() instanceof PyTuple) {
            ((PyTuple) element.getSlice()).getElts().forEach(b -> params.add(
                    new PyClassBaseConstructorVisitor(b).getResult()
            ));
        } else {
            params.add(new PyClassBaseConstructorVisitor(element.getSlice()).getResult());
        }
        result.setParams(params);
    }

    @Override
    public void visit(PyAttribute element, Object arg) {
        result.setName(element.getAttr());
    }
}
