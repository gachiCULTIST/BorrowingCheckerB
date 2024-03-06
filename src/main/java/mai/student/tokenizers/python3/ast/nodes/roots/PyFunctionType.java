package mai.student.tokenizers.python3.ast.nodes.roots;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyFunctionType extends PyRoot {

    @JsonProperty("argtypes")
    private List<PyNode> argTypes;
    @JsonProperty("returns")
    private PyNode returnType;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
