package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.operator.Operator;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyBinOp extends PyExpression {

    private Operator op;
    private PyNode left;
    private PyNode right;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
