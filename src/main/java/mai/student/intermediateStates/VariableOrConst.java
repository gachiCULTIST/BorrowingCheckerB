package mai.student.intermediateStates;

import com.github.javaparser.Position;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.HashMap;

// Класс для представления переменных и констант
public class VariableOrConst implements IStructure {

    private static final String TYPE_ARRAY = "Array";

    private Type type;
    private Type realType = null;

    @Deprecated
    public DefinedClass linkToType = null;

    // TODO: check usage after tokenizer release
    public HashMap<Type, Type> params = new HashMap<>();
    private String identifier;

    // TODO: Пусть пока будет, a potom posmotrim (posle realizacii tokenizacii)
    public IStructure parent = null;

    // Начало для определения зоны действия
    @Deprecated
    private int startIndex;

    // For Parser
    private Position position;

    private boolean isLinked = false;

    private ObjectCreationExpr replacer = null;

    public VariableOrConst(Type type, String identifier, IStructure parent, Position position) {
        this.type = type;
        this.identifier = identifier;
        this.startIndex = -1;
        this.parent = parent;

        if (position == null) {
            this.position = new Position(-1, -1);
        } else {
            this.position = position;
        }
    }

    public VariableOrConst(Type type, String identifier, int startIndex) {
        this.type = type;
        this.identifier = identifier;
        this.startIndex = startIndex;
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
    public IStructure getParent() {
        return parent;
    }

    public ObjectCreationExpr getReplacer() {
        return replacer;
    }

    @Override
    public void actuateTypes(ArrayList<FileRepresentative> files) {
        if (isLinked) {
            return;
        }

        isLinked = true;

        // Обновление связей
        type.updateLink(parent, files);
        if (realType != null) {
            realType.updateLink(parent, files);
        }
        params.forEach((key, value) -> {
            key.updateLink(parent, files);
            value.updateLink(parent, files);
        });
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

    @Deprecated
    public int getStartIndex() {
        return startIndex;
    }
}
