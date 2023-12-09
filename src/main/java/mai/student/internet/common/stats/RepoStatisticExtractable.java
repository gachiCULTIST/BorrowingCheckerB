package mai.student.internet.common.stats;

import java.net.URL;

public interface RepoStatisticExtractable extends Extractable {

    String extractRepoName();
    URL extractRepoUrl();
}
