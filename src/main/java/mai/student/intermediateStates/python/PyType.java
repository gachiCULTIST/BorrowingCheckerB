package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mai.student.intermediateStates.java.DefinedClass;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PyType {

    private String name;
    private List<PyType> params = new ArrayList<>();

    private boolean isLinkSet = false;
    private DefinedClass linkToClass = null;
}
