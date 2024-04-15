package mai.student.tokenizers.python3.ast.nodes.async;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyArguments;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeParameter;
import mai.student.tokenizers.python3.ast.visitors.PyGenericListVisitor;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyAsyncFunctionDef extends PyAsync {

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
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <T, K> List<T> accept(PyGenericListVisitor<T, K> v, K arg) {
        return v.visit(this, arg);
    }
}
