package mai.student.tokenizers.python3.ast.nodes.variables;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyStarred extends PyVariable {

    private PyVariable value;
    private Ctx ctx;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
