package mai.student.internet;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.URL;
import java.nio.file.Path;

@Getter
@Setter
@Accessors(chain = true)
public class InternetSearchResult {

    private Path target;
    private URL source;
    private double originality;
}
