package mai.student.intermediateStates.java;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Класс для представления классов сравниваемых программ и интерфейсов
public class DefinedClass implements IStructure<FileRepresentative> {

    private final String className;

    public List<DefinedClass> innerClasses;
    public List<DefinedFunction> functions;
    public List<VariableOrConst> variablesAndConsts;

    public List<Type> inheritanceList;

    private boolean isParametrized = false;

    private boolean isLinked = false;

    private Type[] params;

    public IStructure<FileRepresentative> parent;

    // For Parser
    public DefinedClass(String className, Type[] params, List<Type> inheritanceList, IStructure<FileRepresentative> parent) {
        this.className = className;
        this.inheritanceList = inheritanceList != null ? inheritanceList : new ArrayList<>();
        this.parent = parent;

        innerClasses = new ArrayList<>();
        functions = new ArrayList<>();
        variablesAndConsts = new ArrayList<>();

        if (params != null) {
            this.params = params;
            isParametrized = true;
        }
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Class;
    }

    @Override
    public IStructure<FileRepresentative> getParent() {
        return parent;
    }

    @Override
    public void actuateTypes(List<FileRepresentative> files) {
        if (isLinked) {
            return;
        }

        isLinked = true;

        innerClasses.forEach(i -> i.actuateTypes(files));
        functions.forEach(i -> i.actuateTypes(files));
        variablesAndConsts.forEach(i -> i.actuateTypes(files));
        inheritanceList.forEach(i -> i.updateLink(parent, files));
        Arrays.stream(params).forEach(i -> i.updateLink(parent, files));
        parent.actuateTypes(files);
    }

    @Override
    public boolean isLinked() {
        return isLinked;
    }

    public Type getType() {
        ArrayList<Type> params = new ArrayList<>();
        if (this.isParametrized) {
            for (Type p : this.params) {
                params.add(p.clone());
            }
        }
        return new Type(className, params, this, true);
    }

    public ClassOrInterfaceType getClassOrInterfaceType() {
        return new ClassOrInterfaceType(null, className);
    }

    public boolean isParametrized() {
        return isParametrized;
    }

    public Type[] getParams() {
        return params;
    }
}
