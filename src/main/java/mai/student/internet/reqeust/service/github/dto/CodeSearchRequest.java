package mai.student.internet.reqeust.service.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.internet.reqeust.service.github.dto.enums.SortTypes;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class CodeSearchRequest {

    private final String query;
    private SortTypes sortType;
    private Integer pageSize;
    private Integer page;

    @Getter
    @RequiredArgsConstructor
    public enum Fields {
        QUERY("q"),
        SORT_TYPE("sort"),
        PAGE_SIZE("per_page"),
        PAGE("page");

        private final String name;
    }
}
