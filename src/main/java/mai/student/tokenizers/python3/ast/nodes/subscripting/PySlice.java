package mai.student.tokenizers.python3.ast.nodes.subscripting;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PySlice extends PySubscriptor {

    private PyNode lower;
    private PyNode upper;
    private PyNode step;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
