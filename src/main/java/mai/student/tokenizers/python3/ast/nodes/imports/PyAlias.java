package mai.student.tokenizers.python3.ast.nodes.imports;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

@Getter
@Setter
public class PyAlias extends PyImports {

    private String name;
    private String asname;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
