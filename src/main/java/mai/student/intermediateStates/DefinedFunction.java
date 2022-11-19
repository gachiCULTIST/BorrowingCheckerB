package mai.student.intermediateStates;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.ArrayList;

// Класс для представления функций
public class DefinedFunction implements IStructure {

    private static final String TYPE_UNDEFINED = "null";

    private String funcName;
    private Type[] argTypes;

    // Границы для сужения зоны анализа
    @Deprecated
    private int startIndex;
    @Deprecated
    private int blockStart;
    @Deprecated
    private int endIndex;

    // Параметризирование
    private boolean isParametrized = false;
    private Type[] params;
    private Type returnValue;

    public ArrayList<DefinedClass> innerClasses;
    public ArrayList<VariableOrConst> variablesAndConsts;

    @Deprecated
    private boolean isRecurrent = false;

    public DefinedClass parent;

    // Представление токенов (только для методов, поскольку мы сводим все к монолиту)
    private boolean isTokenized = false;
    public ArrayList<Integer> tokens;

    // For Parser
    private BlockStmt body = null;

    // For Parser
    public DefinedFunction(String funcName, Type[] argTypes, Type[] params, Type returnValue, DefinedClass parent,
                           BlockStmt body) {
        this.funcName = funcName;
        this.argTypes = argTypes;
        this.startIndex = -1;
        this.blockStart = -1;
        this.endIndex = -1;
        this.returnValue = returnValue;
        this.parent = parent;

        innerClasses = new ArrayList<>();
        variablesAndConsts = new ArrayList<>();
        tokens = new ArrayList<>();

        if (params != null) {
            this.params = params;
            isParametrized = true;
        }

        if (body == null) {
            isTokenized = true;
        } else {
            this.body = body;
        }
    }

    @Deprecated
    public DefinedFunction(String funcName, Type[] argTypes, int startIndex, int blockStart, int endIndex, Type[] params,
                           Type returnValue, boolean isRecurrent, DefinedClass parent) {
        this.funcName = funcName;
        this.argTypes = argTypes;
        this.startIndex = startIndex;
        this.blockStart = blockStart;
        this.endIndex = endIndex;
        this.returnValue = returnValue;
        this.isRecurrent = isRecurrent;
        this.parent = parent;

        innerClasses = new ArrayList<>();
        variablesAndConsts = new ArrayList<>();
        tokens = new ArrayList<>();

        if (params != null) {
            this.params = params;
            isParametrized = true;
        }
    }

    @Override
    public String getName() {
        return funcName;
    }

    public BlockStmt getBody() {
        return body;
    }

    public boolean isTokenized() {
        return isTokenized;
    }

    public void tokenized() {
        isTokenized = true;
    }

    public void addToken(Integer token) {
        tokens.add(token);
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Function;
    }

    @Override
    public IStructure getParent() {
        return parent;
    }

    public Type[] getArgTypes() {
        return argTypes;
    }

    @Deprecated
    public int getStartIndex() {
        return startIndex;
    }

    @Deprecated
    public int getBlockStart() {
        return blockStart;
    }

    @Deprecated
    public int getEndIndex() {
        return endIndex;
    }

    public boolean isParametrized() {
        return isParametrized;
    }

    public Type[] getParams() {
        return params;
    }

    public Type getReturnValue() {
        return returnValue;
    }

    @Deprecated
    public boolean isRecurrent() {
        return isRecurrent;
    }

    public TypeCompatibility checkCompatibility(ArrayList<Type> args, ArrayList<FileRepresentative> files,
                                                IStructure searchOrigin) {
        boolean exactMatch = true;

        if (args.size() != this.argTypes.length) {
            return TypeCompatibility.NonCompatible;
        }

        for (int i = 0; i < this.argTypes.length; ++i) {
            if (args.get(i).getName().equals(TYPE_UNDEFINED)) {
                continue;
            }
            if (!this.argTypes[i].equals(args.get(i))) {
                exactMatch = false;
                break;
            }
        }

        for (int i = 0; i < this.argTypes.length; ++i) {
            if (args.get(i).getName().equals(TYPE_UNDEFINED)) {
                continue;
            }
            args.get(i).updateLinks(searchOrigin, files);
            this.argTypes[i].updateLinks(searchOrigin, files);
            if (!this.argTypes[i].equalsWithCompatibility(args.get(i), files)) {
                return TypeCompatibility.NonCompatible;
            }
        }

        return exactMatch ? TypeCompatibility.ExactMatch : TypeCompatibility.Compatible;
    }

    public static DefinedFunction getFunction(ArrayList<FileRepresentative> files, IStructure searchOrigin,
                                              String func_name, ArrayList<Type> args, ArrayList<Type> params) {
        ArrayList<DefinedFunction> allFuncs = IStructure.findAllFunctions(files, searchOrigin, func_name,
                false, null);
        DefinedFunction compatibleFunc = null;
        for (DefinedFunction func : allFuncs) {
            TypeCompatibility compatibility = func.checkCompatibility(args, files, searchOrigin);
            if (compatibility == TypeCompatibility.ExactMatch) {
                return func;
            } else if (compatibleFunc == null && compatibility == TypeCompatibility.Compatible) {
                compatibleFunc = func;
            }
        }

        if (!allFuncs.isEmpty() && compatibleFunc == null) {
            compatibleFunc = allFuncs.get(0);
        }

        return compatibleFunc;
    }
}
