package mai.student.tokenizers.python3.ast.nodes.statements;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyRaise extends PyStatement {

    private PyNode exc;
    private PyNode cause;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
