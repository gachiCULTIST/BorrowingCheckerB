package mai.student.tokenizers.python3.ast.nodes.literals;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LiteralFormat {
    NO_FORMATTING(-1, null),
    STRING(115, "!s"),
    REPR(114, "!r"),
    ASCII(97, "!a");

    private final int value;
    private final String representation;

    @JsonValue
    public int getValue() {
        return this.value;
    }
}
