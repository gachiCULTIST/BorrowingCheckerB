package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public class PyClassRepresentation implements IStructure<PyFileRepresentative> {

    private List<PyDecorator> decorators = new ArrayList<>();
    private final String name;

    public List<PyClassRepresentation> classes = new ArrayList<>();
    public List<PyFuncRepresentative> functions = new ArrayList<>();
    public List<PyVariableRepresentative> variables = new ArrayList<>();

    private List<PyType> inheritanceList = new ArrayList<>();

    private boolean isParametrized = false;
    private List<PyType> params = new ArrayList<>();

    private boolean isLinked = false;

    private IStructure<PyFileRepresentative> parent;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Class;
    }

    @Override
    public IStructure<PyFileRepresentative> getParent() {
        return parent;
    }

    @Override
    public void actuateTypes(List<PyFileRepresentative> files) {
        IStructure.super.actuateTypes(files);
    }

    @Override
    public boolean isLinked() {
        return IStructure.super.isLinked();
    }
}
