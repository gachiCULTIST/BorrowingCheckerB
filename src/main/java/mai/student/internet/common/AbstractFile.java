package mai.student.internet.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public abstract class AbstractFile implements File{

    protected final Path filePath;
}
