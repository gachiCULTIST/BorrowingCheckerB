package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import mai.student.intermediateStates.python.PyDecorator;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyAttribute;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCall;
import mai.student.tokenizers.python3.ast.nodes.literals.PyConstant;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

@Getter
public class PyDecoratorConstructorVisitor extends AbstractPyVoidVisitor<Object> {

    private final PyDecorator result = new PyDecorator();

    public PyDecoratorConstructorVisitor(PyNode decorator) {
        decorator.accept(this, null);
    }

    @Override
    public void visit(PyCall element, Object arg) {
        element.getFunc().accept(this, arg);

        int argsAmount = 0;
        if (element.getArgs() != null) {
            argsAmount += element.getArgs().size();
        }
        if (element.getKeywords() != null) {
            argsAmount += element.getKeywords().size();
        }

        result.setArgsAmount(argsAmount);
    }

    @Override
    public void visit(PyConstant element, Object arg) {
        result.getElements().add(element.getValue());
    }

    @Override
    public void visit(PyName element, Object arg) {
        result.getElements().add(element.getId());
    }

    @Override
    public void visit(PyAttribute element, Object arg) {
        result.getElements().add(element.getAttr());
        element.getValue().accept(this, arg);
    }
}
