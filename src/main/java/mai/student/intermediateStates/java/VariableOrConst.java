package mai.student.intermediateStates.java;

import com.github.javaparser.Position;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;

import java.util.List;
import java.util.Objects;

// Класс для представления переменных и констант
public class VariableOrConst implements IStructure<FileRepresentative> {

    private static final String TYPE_ARRAY = "Array";

    private final Type type;
    private Type realType = null;
    private final String identifier;
    public IStructure<FileRepresentative> parent;


    // For Parser
    private final Position position;
    private boolean isLinked = false;
    private ObjectCreationExpr replacer = null;

    public VariableOrConst(Type type, String identifier, IStructure<FileRepresentative> parent, Position position) {
        this.type = type;
        this.identifier = identifier;
        this.parent = parent;

        this.position = Objects.requireNonNullElseGet(position, () -> new Position(-1, -1));
    }

    public Type getType() {
        return type;
    }

    public Type getRealType() {
        return realType;
    }

    public void setRealType(Type realType) {
        this.realType = realType;

        if (realType != null) {
            if (realType.getName().equals(TYPE_ARRAY) || realType.linkToClass == null) {
                replacer = null;
            } else {
                replacer = new ObjectCreationExpr(null, realType.toClassOrInterfaceType(), new NodeList<>());
            }
        } else { // TODO: а нужен ли заменитель вообще для базового класса???
            if (type.getName().equals(TYPE_ARRAY) || type.linkToClass == null) {
                replacer = null;
            } else {
                replacer = new ObjectCreationExpr(null, type.toClassOrInterfaceType(), new NodeList<>());
            }
        }
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Variable;
    }

    @Override
    public IStructure<FileRepresentative> getParent() {
        return parent;
    }

    public ObjectCreationExpr getReplacer() {
        return replacer;
    }

    @Override
    public void actuateTypes(List<FileRepresentative> files) {
        if (isLinked) {
            return;
        }

        isLinked = true;

        // Обновление связей
        type.updateLink(parent, files);
        if (realType != null) {
            realType.updateLink(parent, files);
        }
        parent.actuateTypes(files);

        // Создание заменителя в выражении
        if (realType != null) {
            if (realType.getName().equals(TYPE_ARRAY) || realType.linkToClass == null) {
                replacer = null;
            } else {
                replacer = new ObjectCreationExpr(null, realType.toClassOrInterfaceType(), new NodeList<>());
            }
        } else { // TODO: а нужен ли заменитель вообще для базового класса???
            if (type.getName().equals(TYPE_ARRAY) || type.linkToClass == null) {
                replacer = null;
            } else {
                replacer = new ObjectCreationExpr(null, type.toClassOrInterfaceType(), new NodeList<>());
            }
        }
    }

    @Override
    public boolean isLinked() {
        return isLinked;
    }

    public Position getPosition() {
        return this.position;
    }

}
