package mai.student.intermediateStates;

import mai.student.intermediateStates.DefinedClass;
import mai.student.intermediateStates.IStructure;

import java.util.ArrayList;
import java.util.HashMap;

// Класс для представления переменных и констант
public class VariableOrConst implements IStructure {

    private Type type;
    public Type realType = null;
    public DefinedClass linkToType = null;
    public HashMap<Type, Type> params = new HashMap<>();
    private String identifier;

    // TODO: Пусть пока будет, a potom posmotrim (posle realizacii tokenizacii)
    public IStructure parent = null;

    // Начало для определения зоны действия
    @Deprecated
    private int startIndex;

    // For Parser
    // TODO: add link to declaration of all types (Maybe)

    public VariableOrConst(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
        this.startIndex = -1;
    }

    public VariableOrConst(Type type, String identifier, int startIndex) {
        this.type = type;
        this.identifier = identifier;
        this.startIndex = startIndex;
    }

    public VariableOrConst(Type type, String identifier, int startIndex, DefinedFunction function,
                           ArrayList<FileRepresentative> files) {
        this.type = type;
        this.identifier = identifier;
        this.startIndex = startIndex;

        IStructure link = IStructure.findEntity(files, function, this.type.getName(), false, null);
        if (link != null) {
            if (link.getStrucType() == StructureType.Class) {
                this.linkToType = (DefinedClass) link;
            } else {
                this.linkToType = ((DefinedFunction) link).parent;
            }
        } else {
            this.linkToType = null;
        }
    }

    public Type getType() {
        return type;
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

    @Override
    public  void setParent(IStructure parent) {
        this.parent = parent;
    }

    public int getStartIndex() {
        return startIndex;
    }
}
