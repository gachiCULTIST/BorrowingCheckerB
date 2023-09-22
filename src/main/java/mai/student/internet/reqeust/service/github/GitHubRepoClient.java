package mai.student.internet.reqeust.service.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import mai.student.internet.reqeust.service.github.dto.RepoFileResponse;
import mai.student.utility.ConfigReader;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GitHubRepoClient {

    private static final String BASE_URL = ConfigReader.getProperty("github.host") + "repos/";
    private static final String VERSION = ConfigReader.getProperty("github.version");
    private static final String TOKEN = ConfigReader.getProperty("github.token");

    private final ObjectMapper mapper = new ObjectMapper();

    public RepoFileResponse getFile(String owner, String repo, Path path) {
        HttpGet get = new HttpGet(String.format(BASE_URL + "%s/%s/contents/%s", owner, repo, path.toString().replace("\\", "/")));

        get.setHeader(HttpHeaders.ACCEPT, ConfigReader.getProperty("github.accept"));
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
        get.setHeader("X-GitHub-Api-Version", VERSION);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream input = entity.getContent()) {
                    if (response.getCode() == HttpStatus.SC_OK) {
                        return mapper.readValue(input, RepoFileResponse.class);
                    }

                    String errorBody = CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
                    throw new ResponseException("Не успешный запрос, owner - " + owner + ", repo - " + repo
                            + ", path - " + path + ", code - " + response.getCode(), get.getUri(), response.getCode(),
                            errorBody);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException("Не успешный запрос, owner - " + owner + ", repo - " + repo
                            + ", path - " + path + ", code - " + response.getCode(), ex);
                }
            }

            throw new UnsupportedOperationException("Ответ пришел без тела, owner - " + owner + ", repo - " + repo
                    + ", path - " + path + ", code - " + response.getCode());
        } catch (IOException ex) {
            throw new RuntimeException("Во время отправки запроса возникла ошибка, owner - " + owner + ", repo - " + repo
                    + ", path - " + path, ex);
        }
    }

    public byte[] getFullRepo(String owner, String repo) {
        HttpGet get = new HttpGet(String.format(BASE_URL + "%s/%s/zipball", owner, repo));

        get.setHeader(HttpHeaders.ACCEPT, ConfigReader.getProperty("github.accept"));
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
        get.setHeader("X-GitHub-Api-Version", VERSION);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream input = entity.getContent()) {
                    if (response.getCode() == HttpStatus.SC_OK) {
                        return input.readAllBytes();
                    }

                    String errorBody = CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
                    throw new ResponseException("Не успешный запрос, owner - " + owner + ", repo - " + repo
                            + ", code - " + response.getCode(), get.getUri(), response.getCode(),
                            errorBody);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException("Не успешный запрос, owner - " + owner + ", repo - " + repo
                            + ", code - " + response.getCode(), ex);
                }
            }

            throw new UnsupportedOperationException("Ответ пришел без тела, owner - " + owner + ", repo - " + repo
                    + ", code - " + response.getCode());
        } catch (IOException ex) {
            throw new RuntimeException("Во время отправки запроса возникла ошибка, owner - " + owner + ", repo - " + repo, ex);
        }
    }
}
