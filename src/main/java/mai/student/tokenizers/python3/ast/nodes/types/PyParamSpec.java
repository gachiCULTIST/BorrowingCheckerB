package mai.student.tokenizers.python3.ast.nodes.types;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyParamSpec extends PyTypeParameter {

    private String name;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
