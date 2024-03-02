package mai.student.tokenizers.python3.ast.nodes.expressions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.operator.Operator;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyBoolOp extends PyExpression {

    private Operator op;
    private List<PyNode> values;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
