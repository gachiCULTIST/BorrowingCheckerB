package mai.student.tokenizers.python3.preprocessing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PyDecorators {
    STATIC("staticmethod");

    private final String name;
}
