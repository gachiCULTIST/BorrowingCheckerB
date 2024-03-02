package mai.student.tokenizers.python3.ast.nodes.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Not extends Operator {

    @JsonIgnore
    public static final Instance OP = Instance.NOT;
}
