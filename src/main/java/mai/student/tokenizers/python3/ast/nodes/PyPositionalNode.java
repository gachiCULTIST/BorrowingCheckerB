package mai.student.tokenizers.python3.ast.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PyPositionalNode extends PyNode {

    private int lineno;
    @JsonProperty("col_offset")
    private int colOffset;
    @JsonProperty("end_lineno")
    private int endLineno;
    @JsonProperty("end_col_offset")
    private int endColOffset;
}
