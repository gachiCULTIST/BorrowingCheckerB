package mai.student.tokenizers.python3.ast;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import mai.student.tokenizers.python3.ast.nodes.roots.PyInteractive;
import mai.student.tokenizers.python3.ast.nodes.roots.PyModule;
import mai.student.tokenizers.python3.ast.nodes.roots.PyRoot;

import java.nio.file.Path;

@Getter
public class PyAst {

    private Path path;
    private PyRoot ast;
}
