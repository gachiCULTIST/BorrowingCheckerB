package mai.student.internet.reqeust.service.github;

import lombok.Getter;

import java.net.URI;

@Getter
public class ResponseException extends RuntimeException {

    private final String responseBody;
    private final int code;
    private final URI uri;

    public ResponseException(String message, URI uri, int code, String responseBody) {
        super(message + "\nBODY:\n" + responseBody);
        this.uri = uri;
        this.code = code;
        this.responseBody = responseBody;
    }
}
