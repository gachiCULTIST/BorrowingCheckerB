package mai.student.tokenizers.python3.ast.nodes.statements;

import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.VoidVisitor;

@Getter
@Setter
public class PyAssert extends PyStatement {

    private PyNode test;
    private PyNode msg;

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {

    }
}
