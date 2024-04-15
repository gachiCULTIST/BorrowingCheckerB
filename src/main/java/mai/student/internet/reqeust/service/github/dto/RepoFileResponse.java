package mai.student.internet.reqeust.service.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import mai.student.internet.reqeust.service.github.dto.enums.RepoType;

import java.net.URL;

@Getter
public class RepoFileResponse {

    private RepoType type;
    private String encoding;
    private long size;
    private String name;
    private String path;
    private String content;
    private String sha;
    private URL url;
    @JsonProperty("git_url")
    private URL gitUrl;
    @JsonProperty("html_url")
    private URL htmlUrl;
    @JsonProperty("download_url")
    private URL downloadUrl;
    @JsonProperty("_links")
    private Link links;

    @Getter
    private static class Link {

        private URL git;
        private URL self;
        private URL html;
    }
}
