package mai.student.tokenizers.python3.ast.nodes.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyKeyword;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeParameter;
import mai.student.tokenizers.python3.ast.visitors.PyGenericListVisitor;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyClassDef extends PyDefinition {

    private String name;
    private List<PyNode> bases;
    private List<PyKeyword> keywords;
    private List<PyNode> body;
    @JsonProperty("decorator_list")
    private List<PyNode> decoratorList;
    @JsonProperty("type_params")
    private List<PyTypeParameter> typeParams;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <T, K> List<T> accept(PyGenericListVisitor<T, K> v, K arg) {
        return v.visit(this, arg);
    }
}
