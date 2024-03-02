package mai.student.tokenizers.python3.ast.nodes.definitions;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyNonlocal extends PyDefinition {

    private List<String> names;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
