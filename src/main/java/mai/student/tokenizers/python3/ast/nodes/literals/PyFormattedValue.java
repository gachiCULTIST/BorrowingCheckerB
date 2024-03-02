package mai.student.tokenizers.python3.ast.nodes.literals;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyFormattedValue extends PyLiteral {

    private PyNode value;
    private LiteralFormat conversion;
    @JsonProperty("format_spec")
    private PyJoinedStr formatSpec;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
