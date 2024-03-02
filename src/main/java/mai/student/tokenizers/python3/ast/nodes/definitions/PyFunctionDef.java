package mai.student.tokenizers.python3.ast.nodes.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeParameter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyFunctionDef extends PyDefinition {

    private String name;
    private PyArguments args;
    private List<PyNode> body;
    @JsonProperty("decorator_list")
    private List<PyNode> decoratorList;
    private PyNode returns;
    @JsonProperty("type_params")
    private List<PyTypeParameter> typeParams;
    @JsonProperty("type_comment")
    private String typeComment;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
