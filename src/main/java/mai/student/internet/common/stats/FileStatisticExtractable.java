package mai.student.internet.common.stats;

import java.net.URL;

public interface FileStatisticExtractable extends Extractable {

    String extractFileName();
    URL extractFileUrl();
}
