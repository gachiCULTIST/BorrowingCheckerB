package mai.student.tokenizers.python3.ast.nodes.subscripting;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PySlice extends PySubscriptor {

    private PyNode lower;
    private PyNode upper;
    private PyNode step;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
