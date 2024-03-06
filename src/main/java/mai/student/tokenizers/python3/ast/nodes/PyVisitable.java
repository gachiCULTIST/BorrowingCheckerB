package mai.student.tokenizers.python3.ast.nodes;

import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

public interface PyVisitable {

    <A> void accept(PyVoidVisitor<A> v, A arg);
}
