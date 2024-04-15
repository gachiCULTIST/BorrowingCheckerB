package mai.student.intermediateStates.python;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PyImport {

    private Path module;
    private String entity; // == null -> not static
    private String alias;
    private boolean isStatic;
}
