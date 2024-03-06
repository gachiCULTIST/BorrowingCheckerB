package mai.student.tokenizers.python3.ast.nodes.imports;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyImportFrom extends PyImports {

    private String module;
    private List<PyAlias> names;
    private int level;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
