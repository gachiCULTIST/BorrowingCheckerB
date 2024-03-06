package mai.student.tokenizers.python3.ast.nodes.statements;

import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

public class PyPass extends PyStatement {
    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
