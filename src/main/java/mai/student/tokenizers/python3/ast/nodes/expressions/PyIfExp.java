package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyIfExp extends PyExpression {

    private PyNode body;
    private PyNode test;
    private PyNode orelse;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
