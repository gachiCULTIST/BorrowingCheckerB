package mai.student.intermediateStates;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Класс для представления функций
public class DefinedFunction implements IStructure {

    private final String funcName;
    private final Type[] argTypes;

    // Параметризирование
    private boolean isParametrized = false;
    private Type[] params;
    private final Type returnValue;

    public List<DefinedClass> innerClasses;
    public List<VariableOrConst> variablesAndConsts;

    public DefinedClass parent;

    // Представление токенов (только для методов, поскольку мы сводим все к монолиту)
    private boolean isTokenized = false;

    private boolean isLinked = false;

    public List<Integer> tokens;

    // For Parser
    private BlockStmt body = null;

    private final CallableDeclaration<?> declaration;

    // For Parser
    public DefinedFunction(String funcName, Type[] argTypes, Type[] params, Type returnValue, DefinedClass parent,
                           BlockStmt body, CallableDeclaration<?> declaration) {
        this.funcName = funcName;
        this.argTypes = argTypes;
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

        this.declaration = declaration;
    }

    @Override
    public String getName() {
        return funcName;
    }

    public BlockStmt getBody() {
        return body;
    }

    public CallableDeclaration<?> getDeclaration() {
        return declaration;
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

    public void addFunctionTokens(DefinedFunction function) {
        tokens.addAll(function.tokens);
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.Function;
    }

    @Override
    public IStructure getParent() {
        return parent;
    }

    @Override
    public void actuateTypes(List<FileRepresentative> files) {
        if (isLinked) {
            return;
        }

        isLinked = true;

        Arrays.stream(argTypes).forEach(i -> i.updateLink(parent, files));
        Arrays.stream(params).forEach(i -> i.updateLink(parent, files));
        returnValue.updateLink(parent, files);
        innerClasses.forEach(i -> i.actuateTypes(files));
        variablesAndConsts.forEach(i -> i.actuateTypes(files));
        parent.actuateTypes(files);
    }

    @Override
    public boolean isLinked() {
        return isLinked;
    }

    public Type[] getArgTypes() {
        return argTypes;
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

}
