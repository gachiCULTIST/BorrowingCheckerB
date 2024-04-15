package mai.student.tokenizers.python3.ast.nodes.operator;

public class NotEq extends Operator {

    @Override
    public Instance getSelfOps() {
        return Instance.NOT_EQ;
    }
}
