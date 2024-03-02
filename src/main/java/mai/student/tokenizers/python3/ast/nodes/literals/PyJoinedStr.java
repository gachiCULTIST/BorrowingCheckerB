package mai.student.tokenizers.python3.ast.nodes.literals;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyJoinedStr extends PyLiteral {

    private List<PyLiteral> values;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
