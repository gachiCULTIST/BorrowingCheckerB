package mai.student.intermediateStates;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import mai.student.utility.EntitySearchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Type {

    // For Parser (temp maybe)
    private static final String TYPE_VOID = "void";
    private static final String TYPE_UNDEFINED = "null";
    private static final String TYPE_EXCEPTION = "Exception";
    private static final String TYPE_VAR = "var";
    private static final String TYPE_OBJECT = "Object";
    private final String name;
    private final Optional<Qualifier> qualifier;
    private final List<Type> params;

    private boolean isLinkSet = false;
    DefinedClass linkToClass = null;

    public Type(String name) {
        this.name = name;
        this.params = new ArrayList<>();
        this.qualifier = Optional.empty();
    }

    public Type(String name, boolean isLinkSet) {
        this.name = name;
        this.params = new ArrayList<>();
        this.isLinkSet = isLinkSet;
        this.qualifier = Optional.empty();
    }

    public Type(String name, ArrayList<Type> params, Qualifier qualifier) {
        this.name = name;
        this.params = Objects.requireNonNullElseGet(params, ArrayList::new);
        this.qualifier = Optional.ofNullable(qualifier);
    }

    public Type(String name, ArrayList<Type> params, DefinedClass link) {
        this.name = name;
        this.linkToClass = link;

        this.params = Objects.requireNonNullElseGet(params, ArrayList::new);
        this.qualifier = Optional.empty();
    }

    public Type(String name, ArrayList<Type> params, DefinedClass link, boolean isLinkSet) {
        this.name = name;
        this.linkToClass = link;
        this.isLinkSet = isLinkSet;

        this.params = Objects.requireNonNullElseGet(params, ArrayList::new);
        this.qualifier = Optional.empty();
    }

    public Optional<Qualifier> getQualifier() {
        return qualifier;
    }

    public String getName() {
        return name;
    }

    public List<Type> getParams() {
        return params;
    }

    public DefinedClass getLinkToClass() {
        return linkToClass;
    }

    public void setLinkToClass(DefinedClass linkToClass) {
        this.linkToClass = linkToClass;
    }

    public boolean isLinkSet() {
        return isLinkSet;
    }

    public Type clone() {
        ArrayList<Type> params = new ArrayList<>();
        for (Type p : this.params) {
            params.add(p.clone());
        }

        return new Type(this.name, params, this.linkToClass, this.isLinkSet);
    }

    public boolean equals(Type t2) {
        if (t2 != null && t2.name.equals(this.name) && t2.params.size() == this.params.size()) {
            for (int i = 0; i < t2.params.size(); ++i) {
                if (!t2.params.get(i).equals(this.params.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    // Сравнение массивов параметров
    public static boolean equalsTypeArrays(Type[] arg1, Type[] arg2) {
        if (arg1.length != arg2.length) {
            return false;
        }

        for (int i = 0; i < arg1.length; ++i) {
            if (!arg1[i].equals(arg2[i])) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        if (this.params.isEmpty()) {
            return this.name;
        }

        StringBuilder result = new StringBuilder(this.name);
        result.append("<");
        for (int i = 0; i < this.params.size(); ++i) {
            result.append(this.params.get(i));
            if (i + 1 < this.params.size()) {
                result.append(", ");
            }
        }
        result.append(">");

        return result.toString();
    }

    public ClassOrInterfaceType toClassOrInterfaceType() {
        ClassOrInterfaceType scope = null;
        if (qualifier.isPresent()) {
            scope = qualifier.get().toParserForm();
        }

        NodeList<com.github.javaparser.ast.type.Type> typeArgs = new NodeList<>();
        for (Type t : params) {
            typeArgs.add(t.toClassOrInterfaceType());
        }

        return new ClassOrInterfaceType(scope, new SimpleName(name), typeArgs.isEmpty() ? null : typeArgs);
    }

    public void updateLink(IStructure scope, List<FileRepresentative> files) {
        System.out.println("update: " + scope.getName());

        this.isLinkSet = true;
        this.linkToClass = EntitySearchers.findClass(files, scope, this);

        System.out.println("update 0");

        for (Type p : this.params) {
            System.out.println("update 1");
            p.updateLink(scope, files);
        }
    }

    // For Parser
    public static Type getVoidType() {
        return new Type(TYPE_VOID, true);
    }

    public static Type getUndefinedType() {
        return new Type(TYPE_UNDEFINED, true);
    }

    public static Type getExceptionType() {
        return new Type(TYPE_EXCEPTION, true);
    }

    public static Type getVarType() {
        return new Type(TYPE_VAR, true);
    }
    public static Type getObjectType() {
        return new Type(TYPE_OBJECT, true);
    }
}