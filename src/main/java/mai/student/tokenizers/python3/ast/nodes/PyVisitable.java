package mai.student.tokenizers.python3.ast.nodes;

import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

public interface PyVisitable {

    <A> void accept(VoidVisitor<A> v, A arg);
}
