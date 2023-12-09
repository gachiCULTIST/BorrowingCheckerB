package mai.student.internet.reqeust.service.github.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestParams {
    QUERY("q"),
    SORT_TYPE("sort"),
    PAGE_SIZE("per_page"),
    PAGE("page");

    private final String name;
}
