package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import mai.student.intermediateStates.python.PyType;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.literals.PyConstant;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.types.PyParamSpec;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVar;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVarTuple;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

import javax.lang.model.element.TypeParameterElement;

@Getter
public class PyTypeParameterConstructorVisitor extends AbstractPyVoidVisitor<Object> {

    private final PyType result = new PyType();

    public PyTypeParameterConstructorVisitor(PyNode type) {
        type.accept(this, null);
    }

    @Override
    public void visit(PyParamSpec element, Object arg) {
        result.setName(element.getName());
    }

    @Override
    public void visit(PyTypeVar element, Object arg) {
        result.setName(element.getName());
    }

    @Override
    public void visit(PyTypeVarTuple element, Object arg) {
        result.setName(element.getName());
    }

    @Override
    public void visit(PyName element, Object arg) {
        result.setName(element.getId());
    }

    @Override
    public void visit(PySubscript element, Object arg) {
        element.getValue().accept(this, arg);
        result.getParams().add(new PyTypeParameterConstructorVisitor(element.getSlice()).getResult());
    }
}
