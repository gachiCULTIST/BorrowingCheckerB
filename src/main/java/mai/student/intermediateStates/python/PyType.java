package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.java.DefinedClass;
import mai.student.utility.PyEntitySearcher;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PyType {

    private String name;
    private List<PyType> params = new ArrayList<>();

    private boolean isLinked = false;
    private PyClassRepresentation linkToClass = null;

    public void actuateLink(List<PyFileRepresentative> files, IStructure<PyFileRepresentative> scope) {
        if (this.isLinked) {
            return;
        }
        this.isLinked = true;

        this.linkToClass = PyEntitySearcher.findClass(files, name, scope);
        params.forEach(r -> r.actuateLink(files, scope));
    }
}
