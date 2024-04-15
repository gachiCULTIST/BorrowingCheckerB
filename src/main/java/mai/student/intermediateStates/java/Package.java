package mai.student.intermediateStates.java;

import com.github.javaparser.ast.expr.Name;

import java.util.Collection;

public class Package extends NameSequence {
    public Package(String sequence) {
        super(sequence);
    }

    public Package(Collection<String> names) {
        super(names);
    }

    public Package(String[] content) {
        super(content);
    }

    public Package(Name sequence) {
        super(sequence);
    }
}
