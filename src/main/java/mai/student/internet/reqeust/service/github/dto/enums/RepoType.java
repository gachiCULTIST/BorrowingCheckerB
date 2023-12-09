package mai.student.internet.reqeust.service.github.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepoType {
    @JsonProperty("file")
    FILE("file");

    private final String name;
}
