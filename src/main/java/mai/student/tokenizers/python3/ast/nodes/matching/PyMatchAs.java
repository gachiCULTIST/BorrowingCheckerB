package mai.student.tokenizers.python3.ast.nodes.matching;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyMatchAs extends PyMatching {

    private PyMatching pattern; // if null -> _
    private String name;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
