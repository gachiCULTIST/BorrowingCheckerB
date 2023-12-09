package mai.student.internet.reqeust.service.github.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Только одно значение можно указать - по умолчанию стоит best match

@Getter
@RequiredArgsConstructor
public enum SortTypes {
    INDEXED("indexed");

    private final String name;
}
