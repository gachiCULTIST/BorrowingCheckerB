package mai.student.tokenizers.python3.ast.nodes.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.visitors.PyVoidVisitor;

import java.util.List;

@Getter
@Setter
public class PyMatchClass extends PyMatching {

    private PyNode cls;
    private List<PyMatching> patterns;
    @JsonProperty("kwd_attrs")
    private List<String> kwdAttrs;
    @JsonProperty("kwd_patterns")
    private List<PyMatching> kwdPatterns;

    @Override
    public <A> void accept(PyVoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
}
