package mai.student.tokenizers.python3.ast.nodes.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyWithItem extends PyControl {

    @JsonProperty("context_expr")
    private PyNode contextExpr;
    @JsonProperty("optional_vars")
    private PyNode optionalVars;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
