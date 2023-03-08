package mai.student.intermediateStates;

import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.Collection;

// Contains package, import and qualifier information
public abstract class NameSequence {
    private final String[] content;

    public NameSequence(String sequence) {
        content = sequence.split("\\.");
    }

    public NameSequence(Collection<String> names) {
        content = new String[names.size()];

        int i = 0;
        for (String name : names) {
            content[i] = name;
            ++i;
        }
    }

    public NameSequence(String[] content) {
        this.content = new String[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);
    }

    public NameSequence(Name sequence) {
        content = fromParserName(sequence, 0);
    }

    // Convert JavaParser Name to String[]
    // Second argument should be 0
    private String[] fromParserName(Name sequence, int length) {
        String[] result;

        if (sequence.getQualifier().isEmpty()) {
            result = new String[length + 1];
            result[0] = sequence.getIdentifier();
            return result;
        }

        result = fromParserName(sequence.getQualifier().get(), length + 1);
        result[result.length - length - 1] = sequence.getIdentifier();
        return result;
    }

    public String[] getContent() {
        return content;
    }

    public int getLength() { return content.length; }

    public boolean startsWith(NameSequence included) {
        if (included == null || included.getLength() > getLength()) {
            return false;
        }

        for (int i = 0; i < included.getLength(); ++i) {
            if (!included.content[i].equals(content[i])) {
                return false;
            }
        }

        return true;
    }

    public ClassOrInterfaceType toParserForm() {
        ClassOrInterfaceType result = null, previous = null;
        for (String s : content) {
            ClassOrInterfaceType current = new ClassOrInterfaceType(previous, s);
            result = current;
            previous = current;
        }

        return result;
    }

    public boolean endsWith(String name) {
        return content[content.length - 1].equals(name);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(content[0]);

        for (int i = 1; i < content.length; ++i) {
            result.append('.');
            result.append(content[i]);
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NameSequence)) {
            return false;
        }

        NameSequence sequence = (NameSequence) obj;
        if (sequence.content.length != this.content.length) {
            return false;
        }

        for (int i = 0; i < this.content.length; ++i) {
            if (!this.content[i].equals(sequence.content[i])) {
                return false;
            }
        }
        return true;
    }
}
