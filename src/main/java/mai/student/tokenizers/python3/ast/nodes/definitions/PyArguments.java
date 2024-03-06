package mai.student.tokenizers.python3.ast.nodes.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyArguments extends PyDefinition {

    private List<PyArg> posonlyargs;
    private List<PyArg> args;
    private PyArg vararg; // *args
    private List<PyArg> kwonlyargs;
    @JsonProperty("kw_defaults")
    private List<PyNode> kwDefaults;
    private PyArg kwarg; // **kwargs
    private List<PyNode> defaults; // fewer than args -> only last from args have defaults

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
