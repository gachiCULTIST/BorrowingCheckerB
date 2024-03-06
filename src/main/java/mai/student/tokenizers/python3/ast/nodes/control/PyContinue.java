package mai.student.tokenizers.python3.ast.nodes.control;

import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

public class PyContinue extends PyControl {
    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
