package mai.student.tokenizers.python3.ast.nodes.statements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyAssign extends PyStatement {

    private List<PyNode> targets;
    private PyNode value;
    @JsonProperty("type_comment")
    private String typeComment;


    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
