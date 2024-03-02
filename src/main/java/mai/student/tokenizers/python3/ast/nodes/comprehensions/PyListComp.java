package mai.student.tokenizers.python3.ast.nodes.comprehensions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyListComp extends PyComprehension {

    private PyNode elt;
    private List<PyComp> generators;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
