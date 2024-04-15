package mai.student.intermediateStates.java;

import com.github.javaparser.ast.expr.Name;

import java.util.Collection;

public class Qualifier extends NameSequence{
    public Qualifier(String sequence) {
        super(sequence);
    }

    public Qualifier(Collection<String> names) {
        super(names);
    }

    public Qualifier(String[] content) {
        super(content);
    }

    public Qualifier(Name sequence) {
        super(sequence);
    }
}
