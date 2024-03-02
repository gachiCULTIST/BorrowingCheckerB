package mai.student.tokenizers.python3.ast.nodes.imports;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyImport extends PyImports {

    private List<PyAlias> names;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
