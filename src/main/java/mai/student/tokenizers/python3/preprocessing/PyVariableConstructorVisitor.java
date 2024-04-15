package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import mai.student.intermediateStates.python.PyClassRepresentation;
import mai.student.intermediateStates.python.PyDecorator;
import mai.student.intermediateStates.python.PyVariableRepresentative;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyAttribute;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCall;
import mai.student.tokenizers.python3.ast.nodes.literals.PyConstant;
import mai.student.tokenizers.python3.ast.nodes.literals.PyList;
import mai.student.tokenizers.python3.ast.nodes.literals.PyTuple;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PyVariableConstructorVisitor extends AbstractPyVoidVisitor<Object> {


    private final List<PyVariableRepresentative> result = new ArrayList<>();
    private final List<PyVariableRepresentative> classVars = new ArrayList<>(); // для self.var =
    private final String funcFirstArg; // null -> no "self" field

    public PyVariableConstructorVisitor(PyNode assign, String funcFirstArg) {
        this.funcFirstArg = funcFirstArg;
        assign.accept(this, null);
    }

    @Override
    public void visit(PyTuple element, Object arg) {
        element.getElts().forEach(v -> v.accept(this, arg));
    }

    @Override
    public void visit(PyList element, Object arg) {
        element.getElts().forEach(v -> v.accept(this, arg));
    }

    @Override
    public void visit(PyName element, Object arg) {
        result.add(new PyVariableRepresentative(element.getId()));
    }

    // Пропуск
    @Override
    public void visit(PySubscript element, Object arg) {
    }

    @Override
    public void visit(PyAttribute element, Object arg) {
        if (funcFirstArg != null && element.getValue() instanceof PyName
                && ((PyName) element.getValue()).getId().equals(funcFirstArg)) {
            classVars.add(new PyVariableRepresentative(element.getAttr()));
        }
    }

    @Override
    public void visit(PyCall element, Object arg) {
    }
}
