package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PySpecificFunction {
    INIT("__init__");

    private final String name;
}
