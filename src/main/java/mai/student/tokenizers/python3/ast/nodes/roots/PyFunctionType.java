package mai.student.tokenizers.python3.ast.nodes.roots;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyExpression;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyFunctionType extends PyRoot {

    @JsonProperty("argtypes")
    private List<PyExpression> argTypes;
    @JsonProperty("returns")
    private PyExpression returnType;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
