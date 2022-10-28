package mai.student.intermediateStates;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Класс для представления классов сравниваемых прграмм и интерфейсов
public class DefinedClass implements IStructure {

    private String className;

    // Границы для сужения зоны анализа
    @Deprecated
    private int startIndex;
    @Deprecated
    private int endIndex;

    public ArrayList<DefinedClass> innerClasses;
    public ArrayList<DefinedFunction> functions;
    public ArrayList<VariableOrConst> variablesAndConsts;

    public ArrayList<Type> inheritanceList;
    @Deprecated
    public ArrayList<DefinedClass> linksToAncestors;

    // Параметризирование
    private boolean isParametrized = false;
    private Type[] params;

    public IStructure parent;

    // For Parser
    public DefinedClass(String className, Type[] params, ArrayList<Type> inheritanceList, IStructure parent) {
        this.className = className;
        this.startIndex = -1;
        this.endIndex = -1;
        this.inheritanceList = inheritanceList != null ? inheritanceList : new ArrayList<>();
        this.parent = parent;

        innerClasses = new ArrayList<>();
        functions = new ArrayList<>();
        variablesAndConsts = new ArrayList<>();
        linksToAncestors = new ArrayList<>();

        if (params != null) {
            this.params = params;
            isParametrized = true;
        }
    }

    @Deprecated
    public DefinedClass(String className, int startIndex, int endIndex, Type[] params, IStructure parent) {
        this.className = className;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.parent = parent;

        innerClasses = new ArrayList<>();
        functions = new ArrayList<>();
        variablesAndConsts = new ArrayList<>();
        inheritanceList = new ArrayList<>();
        linksToAncestors = new ArrayList<>();

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
    public IStructure getParent() {
        return parent;
    }

    public Type getType() {
        ArrayList<Type> params = new ArrayList<>();
        if (this.isParametrized) {
            for (Type p : this.params) {
                params.add(p.clone());
            }
        }
        return new Type(className, params);
    }
    @Deprecated
    public Type getType(ArrayList<Type> params){return new Type(className);}

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public boolean isParametrized() {
        return isParametrized;
    }

    public Type[] getParams() {
        return params;
    }
}
