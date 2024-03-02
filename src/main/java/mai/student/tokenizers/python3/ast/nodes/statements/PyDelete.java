package mai.student.tokenizers.python3.ast.nodes.statements;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyDelete extends PyStatement {

    private List<PyNode> targets;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
