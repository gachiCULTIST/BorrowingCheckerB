package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyAttribute extends PyExpression {

    private PyNode value;
    private String attr;
    private Ctx ctx;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
