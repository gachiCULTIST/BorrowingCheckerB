package mai.student.intermediateStates.python;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PyDecorator {

    private List<String> elements = new ArrayList<>();
    private int argsAmount;
}
