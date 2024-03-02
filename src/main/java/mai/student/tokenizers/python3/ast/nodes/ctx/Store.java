package mai.student.tokenizers.python3.ast.nodes.ctx;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Store extends Ctx{

    @JsonIgnore
    public static final Type CTX = Type.STORE;
}
