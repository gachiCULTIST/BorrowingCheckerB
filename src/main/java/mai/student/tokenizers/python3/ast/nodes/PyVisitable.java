package mai.student.tokenizers.python3.ast.nodes;

import mai.student.tokenizers.python3.ast.visitors.PyGenericListVisitor;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

public interface PyVisitable {

    <A> void accept(PyVoidVisitor<A> v, A arg);

    <T, K> List<T> accept(PyGenericListVisitor<T, K> v, K arg);
}
