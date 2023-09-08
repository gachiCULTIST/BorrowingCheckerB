package mai.student.internet.reqeust.service.github;

import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException {

    private final String responseBody;

    public ResponseException(String message, String responseBody) {
        super(message + "\nBODY:\n" + responseBody);
        this.responseBody = responseBody;
    }
}
