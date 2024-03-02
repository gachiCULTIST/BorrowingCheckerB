package mai.student.tokenizers.python3.ast.nodes.statements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeParameter;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyTypeAlias extends PyStatement {

    private PyName name;
    @JsonProperty("type_params")
    private List<PyTypeParameter> typeParams;
    private PyNode value;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
