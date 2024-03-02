package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyAttribute extends PyExpression {

    private PyNode value;
    private String attr;
    private Ctx ctx;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
