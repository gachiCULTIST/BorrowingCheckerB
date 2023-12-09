package mai.student.internet;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public interface InternetBorrowingFinder {

    void setProgram(Path source);

    List<InternetSearchResult> start();
}
