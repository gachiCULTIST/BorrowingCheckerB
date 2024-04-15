package mai.student.tokenizers.python3.ast.nodes.control;

import mai.student.tokenizers.python3.ast.visitors.PyGenericListVisitor;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

public class PyBreak extends PyControl {
    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <T, K> List<T> accept(PyGenericListVisitor<T, K> v, K arg) {
        return v.visit(this, arg);
    }
}
