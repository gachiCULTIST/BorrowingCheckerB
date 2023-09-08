package mai.student.internet.reqeust.service.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import mai.student.internet.reqeust.service.github.dto.CodeSearchResponse;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GitHubClient {

    private static final String URL = "https://api.github.com/search/code";
    private static final String VERSION = "2022-11-28";

    // TODO: token to config;
    private static final String TOKEN = "change";

//    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CodeSearchResponse get(List<NameValuePair> request) {
        HttpGet get = new HttpGet(URL);

        try {
            URI uri = new URIBuilder(get.getUri()).addParameters(request)
                    .build();
            get.setUri(uri);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Что-то пошло не так", ex);
        }

        ObjectMapper mapper = new ObjectMapper();

        get.setHeader(HttpHeaders.ACCEPT, "application/vnd.github+json");
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
        get.setHeader("X-GitHub-Api-Version", VERSION);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream input = entity.getContent()) {
                    if (response.getCode() == HttpStatus.SC_OK) {
                        return mapper.readValue(input, CodeSearchResponse.class);
                    }

                    String errorBody = CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
                    throw new ResponseException("Не успешный запрос, code: " + response.getCode(), errorBody);
                }
            }

            throw new UnsupportedOperationException("Ответ пришел без тела, код: " + response.getCode());
        } catch (IOException ex) {
            throw new RuntimeException("Во время отправки запроса возникла ошибка.", ex);
        }
    }
}
