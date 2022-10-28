package mai.student.intermediateStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Type {

    private static final String TYPE_BYTE = "byte";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_INTEGER = "int";
    private static final String TYPE_CHAR = "char";
    private static final String TYPE_SHORT = "short";
    private static final String TYPE_LONG = "long";
    // For Parser (temp maybe)
    private static final String TYPE_VOID = "void";
    private static final String TYPE_UNDEFINED = "null";
    private static final String TYPE_EXCEPTION = "Exception";
    private static final String TYPE_VAR = "var";
    private static final String TYPE_OBJECT = "Object";

    private static final Type[] byteAutoCast = {new Type(TYPE_SHORT), new Type(TYPE_CHAR), new Type(TYPE_INTEGER),
            new Type(TYPE_LONG), new Type(TYPE_FLOAT), new Type(TYPE_DOUBLE)};
    private static final Type[] shortAutoCast = {new Type(TYPE_CHAR), new Type(TYPE_INTEGER),
            new Type(TYPE_LONG), new Type(TYPE_FLOAT), new Type(TYPE_DOUBLE)};
    private static final Type[] charAutoCast = {new Type(TYPE_SHORT), new Type(TYPE_INTEGER),
            new Type(TYPE_LONG), new Type(TYPE_FLOAT), new Type(TYPE_DOUBLE)};
    private static final Type[] integerAutoCast = {new Type(TYPE_LONG), new Type(TYPE_FLOAT), new Type(TYPE_DOUBLE)};
    private static final Type[] longAutoCast = {new Type(TYPE_FLOAT), new Type(TYPE_DOUBLE)};
    private static final Type[] floatAutoCast = {new Type(TYPE_DOUBLE)};
    private String name;
    private ArrayList<Type> params;

    DefinedClass linkToClass = null;

    public Type(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public Type(String name, ArrayList<Type> params) {
        this.name = name;

        if (params != null) {
            this.params = params;
        } else {
            this.params = new ArrayList<>();
        }
    }

    public Type(String name, ArrayList<Type> params, DefinedClass link) {
        this.name = name;
        this.linkToClass = link;

        if (params != null) {
            this.params = params;
        } else {
            this.params = new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Type> getParams() {
        return params;
    }

    public DefinedClass getLinkToClass() {
        return linkToClass;
    }

    public void setLinkToClass(DefinedClass linkToClass) {
        this.linkToClass = linkToClass;
    }

    public Type clone() {
        ArrayList<Type> params = new ArrayList<>();
        for (Type p : this.params) {
            params.add(p.clone());
        }

        return new Type(new String(this.name), params, this.linkToClass);
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

    public boolean equalsWithCompatibility(Type t2, ArrayList<FileRepresentative> files) {
        if (t2 != null && this.params.isEmpty() && !t2.params.isEmpty() && this.name.equals(t2.name)) {
            return true;
        }

        if (t2 != null && t2.params.size() == this.params.size()) {
            boolean autoCast = false;
            switch (t2.getName()) {
                case TYPE_BYTE:
                    autoCast = isAutoCast(this, byteAutoCast);
                    break;
                case TYPE_SHORT:
                    autoCast = isAutoCast(this, shortAutoCast);
                    break;
                case TYPE_CHAR:
                    autoCast = isAutoCast(this, charAutoCast);
                    break;
                case TYPE_INTEGER:
                    autoCast = isAutoCast(this, integerAutoCast);
                    break;
                case TYPE_FLOAT:
                    autoCast = isAutoCast(this, floatAutoCast);
                    break;
                case TYPE_LONG:
                    autoCast = isAutoCast(this, longAutoCast);
                    break;
            }

            if (t2.name.equals(this.name) || autoCast || IStructure.findEntity(files, t2.linkToClass, this.name,
                    true, null) != null) {
                for (int i = 0; i < t2.params.size(); ++i) {
                    if (!t2.params.get(i).equalsWithCompatibility(this.params.get(i), files)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static boolean isAutoCast(Type type, Type[] casting) {
        for (Type cast : casting) {
            if (type.equals(cast)) {
                return true;
            }
        }
        return false;
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

    public void updateLinks(IStructure function, ArrayList<FileRepresentative> files) {
        IStructure link = IStructure.findEntity(files, function, this.name, false, null);
        if (link != null) {
            if (link.getStrucType() == StructureType.Function) {
                this.linkToClass = ((DefinedFunction) link).parent;
            } else {
                this.linkToClass = (DefinedClass) link;
            }
        } else {
            this.linkToClass = null;
        }
        for (Type p : this.params) {
            p.updateLinks(function, files);
        }
    }

    public static Type getTypeWithMapping(Type origin, HashMap<String, Type> params) {
        Type result = origin.clone();
        result.getParams().clear();

        if (params.containsKey(origin.getName())) {
            return getTypeWithMapping(params.get(origin.getName()), params);
        }

        for (int i = 0; i < origin.getParams().size(); ++i) {
            if (params.containsKey(origin.getParams().get(i).getName())) {
                result.getParams().add(getTypeWithMapping(params.get(origin.getParams().get(i).getName()), params));
            } else {
                result.getParams().add(origin.getParams().get(i).clone());
            }
        }

        return result;
    }

    // For Parser
    public static Type getVoidType() {
        return new Type(TYPE_VOID);
    }

    public static Type getUndefinedType() {
        return new Type(TYPE_UNDEFINED);
    }

    public static Type getExceptionType() {
        return new Type(TYPE_EXCEPTION);
    }

    public static Type getVarType() {
        return new Type(TYPE_VAR);
    }
    public static Type getObjectType() {
        return new Type(TYPE_OBJECT);
    }
}