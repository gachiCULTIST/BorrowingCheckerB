package mai.student.tokenizers.python3.ast.nodes.types;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyTypeVar extends PyTypeParameter {

    private String name;
    private PyNode bound; // if PyTuple - constraints

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
