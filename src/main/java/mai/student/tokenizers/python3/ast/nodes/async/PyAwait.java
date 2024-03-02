package mai.student.tokenizers.python3.ast.nodes.async;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyAwait extends PyAsync {

    private PyNode value;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
