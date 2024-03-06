package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyCall extends PyExpression {

    private PyNode func;
    private List<PyNode> args;
    private List<PyKeyword> keywords;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
