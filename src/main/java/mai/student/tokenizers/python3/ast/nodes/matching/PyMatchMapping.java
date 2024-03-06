package mai.student.tokenizers.python3.ast.nodes.matching;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyMatchMapping extends PyMatching {

    private List<PyNode> keys;
    private List<PyMatching> patterns;
    private String rest;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
