package mai.student.internet;

import java.net.URL;
import java.nio.file.Path;

public interface InternetBorrowingFinder {

    void setProgram(Path source);

    // TODO result class
    void start();

    double getResult();

    URL getSource();
}
