package mai.student.intermediateStates.java;

import com.github.javaparser.ast.expr.Name;

import java.util.Collection;

public class Import extends NameSequence {

    private final boolean isStatic;

    private final boolean isOverall;

    public Import(String sequence, boolean isStatic, boolean isOverall) {
        super(sequence);

        this.isStatic = isStatic;
        this.isOverall = isOverall;
    }

    public Import(Collection<String> names, boolean isStatic, boolean isOverall) {
        super(names);

        this.isStatic = isStatic;
        this.isOverall = isOverall;
    }

    public Import(Name sequence, boolean isStatic, boolean isOverall) {
        super(sequence);

        this.isStatic = isStatic;
        this.isOverall = isOverall;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isOverall() {
        return isOverall;
    }

    @Override
    public String toString() {
        return isOverall ? super.toString() + ".*" : super.toString();
    }

    public Qualifier toQualifier() {
        if (isOverall) {
            return new Qualifier(getContent());
        }

        // without last entity
        String[] newContent = new String[getLength() - 1];
        System.arraycopy(getContent(), 0, newContent, 0, newContent.length);
        return new Qualifier(newContent);
    }
}
