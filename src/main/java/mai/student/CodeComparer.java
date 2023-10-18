package mai.student;

import mai.student.tokenizers.CodeLanguage;

import java.nio.file.Path;

public interface CodeComparer {

    void setFirstProgram(Path source);

    void setFirstProgram(Path source, CodeLanguage lang);

    void setSecondProgram(Path source);

    void setSecondProgram(Path source, CodeLanguage lang);

    void compare();

    double getResult();
}
