package mai.student.internet.reqeust.service.github.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueryParams {
    LANGUAGE("language"),
    CONTENT("content");

    private final String name;
}
