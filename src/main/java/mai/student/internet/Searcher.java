package mai.student.internet;

import java.net.URL;
import java.util.List;

public interface Searcher {

    List<URL> search(List<String> target);
}
