package mai.student.tokenizers.python3.ast.nodes.roots;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.statements.PyStatement;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyModule extends PyRoot {

    private List<PyStatement> body;
    @JsonProperty("type_ignores")
    private List<String> typeIgnores;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
