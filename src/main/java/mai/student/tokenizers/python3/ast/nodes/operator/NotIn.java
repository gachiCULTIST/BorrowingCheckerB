package mai.student.tokenizers.python3.ast.nodes.operator;

public class NotIn extends Operator {

    @Override
    public Instance getSelfOps() {
        return Instance.NOT_IN;
    }
}
