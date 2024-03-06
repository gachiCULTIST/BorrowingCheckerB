package mai.student.tokenizers.python3.ast.nodes.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyArg extends PyDefinition {

    private String arg;
    private PyNode annotation;
    @JsonProperty("type_comment")
    private String typeComment;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
