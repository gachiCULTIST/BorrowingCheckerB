package mai.student.tokenizers.python3.ast.nodes.control;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyWhile extends PyControl {

    private PyNode test;
    private List<PyNode> body;
    private List<PyNode> orelse;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
