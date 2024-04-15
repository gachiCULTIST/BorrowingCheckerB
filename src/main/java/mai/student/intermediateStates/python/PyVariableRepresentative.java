package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class PyVariableRepresentative implements IStructure<PyFileRepresentative> {

    private final String name;
    private PyType type = null;
    private PyType realType = null;
    private IStructure<PyFileRepresentative> parent;

    private boolean isLinked = false;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Variable;
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
