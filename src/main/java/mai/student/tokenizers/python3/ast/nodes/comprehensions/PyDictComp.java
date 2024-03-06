package mai.student.tokenizers.python3.ast.nodes.comprehensions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyDictComp extends PyComprehension {

    private PyNode key;
    private PyNode value;
    private List<PyComp> generators;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
