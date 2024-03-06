package mai.student.tokenizers.python3.ast.nodes.variables;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyName extends PyVariable {

    private String id;
    private Ctx ctx;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
