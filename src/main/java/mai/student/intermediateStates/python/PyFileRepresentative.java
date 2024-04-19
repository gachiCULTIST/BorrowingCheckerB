package mai.student.intermediateStates.python;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;
import mai.student.tokenizers.python3.ast.nodes.PyNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PyFileRepresentative implements IStructure<PyFileRepresentative> {

    private Path path; // equals module
    private PyNode ast;

    @JsonIgnore
    private List<PyImport> imports = new ArrayList<>();
    @JsonIgnore
    public List<PyClassRepresentation> classes = new ArrayList<>();
    @JsonIgnore
    public List<PyFuncRepresentative> functions = new ArrayList<>();
    @JsonIgnore
    public List<PyVariableRepresentative> variables = new ArrayList<>();
    @JsonIgnore
    private List<Integer> initCode = new ArrayList<>();
    @JsonIgnore
    private boolean isTokenized = false;

    @Override
    public String getName() {
        return path.toString();
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.File;
    }

    @Override
    public boolean isLinked() {
        return IStructure.super.isLinked();
    }
}
