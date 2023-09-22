package mai.student;

import java.nio.file.Path;

public interface CodeComparer {

    void setFirstProgram(Path source);

    void setSecondProgram(Path source);

    void compare();

    double getResult();
}
