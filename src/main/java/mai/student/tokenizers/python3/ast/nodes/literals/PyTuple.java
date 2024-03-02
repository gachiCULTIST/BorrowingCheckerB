package mai.student.tokenizers.python3.ast.nodes.literals;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyTuple extends PyLiteral {

    private List<PyNode> elts; // значения
    private Ctx ctx;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
