package mai.student.internet.reqeust.service.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.io.CharStreams;
import mai.student.internet.reqeust.service.github.dto.RepoFileResponse;
import mai.student.tokenizers.java17.tokenization.UrlChineseException;
import mai.student.utility.ConfigReader;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.TruncatedChunkException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GitHubRepoClient {

    private static final String BASE_URL = ConfigReader.getProperty("github.host") + "repos/";
    private static final String VERSION = ConfigReader.getProperty("github.version");

    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public GitHubRepoClient(String token) {
        this.token = token;
    }

    public RepoFileResponse getFile(String owner, String repo, String path) {
        String url = String.format(BASE_URL + "%s/%s/contents/%s",
                URLEncoder.encode(owner, StandardCharsets.UTF_8),
                URLEncoder.encode(repo, StandardCharsets.UTF_8),
                URLEncoder.encode(path, StandardCharsets.UTF_8).replaceAll("%5C", "/").replaceAll("\\+", " "));
        HttpGet get = null;

        try {
            get = new HttpGet(url);
        } catch (IllegalArgumentException ex) {
            throw new UrlChineseException("Illegal url: " + url, ex);
        }

        get.setHeader(HttpHeaders.ACCEPT, ConfigReader.getProperty("github.accept"));
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        get.setHeader("X-GitHub-Api-Version", VERSION);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream input = entity.getContent()) {
                    if (response.getCode() == HttpStatus.SC_OK) {
                        try {
                            return mapper.readValue(input, RepoFileResponse.class);
                        } catch (MismatchedInputException ex) {
                            String body = CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
                            throw new ResponseException("Не успешный запрос, owner - " + owner + ", repo - " + repo
                                    + ", path - " + path + ", code - " + response.getCode(), get.getUri(), response.getCode(),
                                    body);
                        }
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

    public byte[] getFullRepo(String owner, String repo, boolean repeated) {
        HttpGet get = new HttpGet(String.format(BASE_URL + "%s/%s/zipball", owner, repo));

        get.setHeader(HttpHeaders.ACCEPT, ConfigReader.getProperty("github.accept"));
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
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
        } catch (TruncatedChunkException ex) {
            if (repeated) {
                System.out.println(ex.getMessage());
            }

            return getFullRepo(owner, repo, true); // если не удалось нормально скачать - проводится еше попытка
        } catch (IOException ex) {
            if (repeated) {
                throw new RuntimeException("Во время отправки запроса возникла ошибка, owner - " + owner + ", repo - " + repo, ex);
            }

            return getFullRepo(owner, repo, true); // если не удалось нормально скачать - проводится еше попытка
        }
    }
}
