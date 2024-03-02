package mai.student.tokenizers.python3.ast.nodes.types;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyTypeVarTuple extends PyTypeParameter {

    private String name;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
