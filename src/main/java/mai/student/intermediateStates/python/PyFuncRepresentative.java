package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;
import mai.student.tokenizers.python3.ast.nodes.PyNode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class PyFuncRepresentative implements IStructure<PyFileRepresentative> {

    private List<PyDecorator> decorators = new ArrayList<>();
    private boolean async;
    private final String name;
    private List<String> argNames = new ArrayList<>();
    private List<PyType> argTypes = new ArrayList<>();

    // Параметризирование
    private boolean isParametrized = false;
    private List<PyType> params = new ArrayList<>();
    private PyType returnValue;

    public List<PyClassRepresentation> classes = new ArrayList<>();
    public List<PyFuncRepresentative> functions = new ArrayList<>();
    public List<PyVariableRepresentative> variables = new ArrayList<>();

    private IStructure<PyFileRepresentative> parent;

    private boolean isTokenized = false;
    private boolean isLinked = false;
    private List<Integer> tokens = new ArrayList<>();

    private PyNode selfNode = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Function;
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
