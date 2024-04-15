package mai.student.tokenizers.python3.ast.nodes.operator;

public class MatMult extends Operator {

    @Override
    public Instance getSelfOps() {
        return Instance.MAT_MULT;
    }
}
