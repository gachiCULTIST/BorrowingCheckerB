package mai.student.tokenizers.python3.ast.nodes.variables;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.ctx.Ctx;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyName extends PyVariable {

    private String id;
    private Ctx ctx;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
