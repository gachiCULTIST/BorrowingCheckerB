package mai.student.tokenizers.python3.ast.nodes.literals;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyGenericListVisitor;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PySet extends PyLiteral {

    private List<PyNode> elts; // значения

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <T, K> List<T> accept(PyGenericListVisitor<T, K> v, K arg) {
        return v.visit(this, arg);
    }
}
