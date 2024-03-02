package mai.student.tokenizers.python3.ast.nodes.roots;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.statements.PyStatement;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyInteractive extends PyRoot {

    private List<PyStatement> body;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
