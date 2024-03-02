package mai.student.tokenizers.python3.ast.nodes.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class USub extends Operator {

    @JsonIgnore
    public static final Instance OP = Instance.U_SUB;
}
