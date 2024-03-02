package mai.student.tokenizers.python3.ast.nodes.control;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyExceptHandler extends PyControl {

    private PyNode type;
    private String name;
    private List<PyNode> body;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
