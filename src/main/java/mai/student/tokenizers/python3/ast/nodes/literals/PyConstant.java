package mai.student.tokenizers.python3.ast.nodes.literals;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyConstant extends PyLiteral {

    private String value;
    private String kind; // хз что это
    private String n; // хз что это
    private String s; // хз что это

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
