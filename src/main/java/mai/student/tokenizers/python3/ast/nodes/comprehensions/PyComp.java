package mai.student.tokenizers.python3.ast.nodes.comprehensions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyComp extends PyComprehension {

    private PyNode target;
    private PyNode iter;
    private List<PyNode> ifs;
    @JsonProperty("is_async")
    private int isAsync;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
