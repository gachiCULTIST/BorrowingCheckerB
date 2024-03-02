package mai.student.tokenizers.python3.ast.nodes.roots;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyExpression;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyExpressionContainer extends PyRoot {

    private PyExpression body;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
