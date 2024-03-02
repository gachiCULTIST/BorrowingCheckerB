package mai.student.tokenizers.python3.ast.nodes.control;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyIf extends PyControl {

    private PyNode test;
    private List<PyNode> body;
    private List<PyNode> orelse;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
