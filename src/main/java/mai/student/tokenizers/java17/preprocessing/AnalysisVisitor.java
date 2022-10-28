package mai.student.tokenizers.java17.preprocessing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mai.student.intermediateStates.*;

import java.util.*;

// При обработке (всего) дерева необходимо обязательно вторым параметром передавать FileRepresentative
// Вторым аргументом выступает родительский элемент собственной модели
public class AnalysisVisitor extends VoidVisitorAdapter<IStructure> {

    private static final String TYPE_ARRAY = "Array";

    // Используется для отделения внутренних классов перечислений от классов представляющих констант
    private static final String ENUM_INNER_PREFIX = "?ENUM_";

    private final ArrayDeque<IStructure> children = new ArrayDeque<>();

    @Override
    public void visit(CompilationUnit unit, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;

        super.visit(unit, arg);

        processChildren(file);
    }

    @Override
    public void visit(PackageDeclaration declaration, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;
        file.curPackage = declaration.getNameAsString();

        super.visit(declaration, arg);
    }

    @Override
    public void visit(ImportDeclaration declaration, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;

        // TODO: сохраняю на всякий случай звездочку (не помню - она где-то обрабатывается?)
        if (declaration.isStatic()) {
            file.staticImports.add(declaration.getNameAsString() + (declaration.isAsterisk() ? "*" : ""));
        } else {
            file.imports.add(declaration.getNameAsString() + (declaration.isAsterisk() ? "*" : ""));
        }

        super.visit(declaration, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, IStructure arg) {
        // TODO: пока надо сохранять отдельно, что там есть - потом переделать
        ArrayDeque<IStructure> backup = children.clone();
        children.clear();

        // Обработка наследуемых классов и реализуемых интерфейсов
        ArrayList<Type> inheritanceList = new ArrayList<>();
        for (ClassOrInterfaceType inhClass : declaration.getExtendedTypes()) {
            inheritanceList.add(fromParserToMyType(inhClass));
        }

        for (ClassOrInterfaceType inhInterface : declaration.getImplementedTypes()) {
            inheritanceList.add(fromParserToMyType(inhInterface));
        }

        // Обработка параметров класса/интерфейса
        List<TypeParameter> parserParams = declaration.getTypeParameters();
        String[] params = new String[parserParams.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = parserParams.get(i).asString();
        }

        // Начальная инициализация
        DefinedClass result = new DefinedClass(declaration.getNameAsString(), params, inheritanceList);

        // Обработка внутренностей
        super.visit(declaration, result);
        processChildren(result);

        // Добавление конструктора по умолчанию, если ни одного не было объявлено
        boolean hasDefault = result.functions.stream().reduce(false,
                (f, i) -> f.equals(true) || i.getName().equals(result.getName()),
                (r1, r2) -> r1 || r2);
        if (!hasDefault) {
            DefinedFunction defaultConstructor = new DefinedFunction(result.getName(), new Type[0], new String[0],
                    result.getType());
            defaultConstructor.setParent(result);
            result.functions.add(defaultConstructor);
        }

        children.addAll(backup);
        children.offerFirst(result);

    }

    public void visit(EnumDeclaration declaration, IStructure arg) {
        // TODO: пока надо сохранять отдельно, что там есть - потом переделать
        ArrayDeque<IStructure> backup = children.clone();
        children.clear();

        // Обработка реализуемых интерфейсов
        ArrayList<Type> inheritanceList = new ArrayList<>();
        for (ClassOrInterfaceType inhInterface : declaration.getImplementedTypes()) {
            inheritanceList.add(fromParserToMyType(inhInterface));
        }

        // Начальная инициализация
        DefinedClass result = new DefinedClass(declaration.getNameAsString(), null, inheritanceList);

        // Обработка внутренностей
        super.visit(declaration, result);
        processChildren(result);

        children.addAll(backup);
        children.offerFirst(result);
    }

    public void visit(EnumConstantDeclaration declaration, IStructure arg) {
        // TODO: пока надо сохранять отдельно, что там есть - потом переделать
        ArrayDeque<IStructure> backup = children.clone();
        children.clear();

        // Указываем объявление перечисление как родительский класс
        ArrayList<Type> inheritanceList = new ArrayList<>() {{
            add(new Type(arg.getName(), null, (DefinedClass) arg));
        }};


        // Начальная инициализация
        DefinedClass resultClass = new DefinedClass(ENUM_INNER_PREFIX + declaration.getNameAsString(),
                null, inheritanceList);
        VariableOrConst resultVar = new VariableOrConst(resultClass.getType(), declaration.getNameAsString());

        // Обработка внутренностей
        super.visit(declaration, resultClass);
        processChildren(resultClass);

        children.addAll(backup);
        children.offerFirst(resultVar);
        children.offerFirst(resultClass);
    }

    @Override
    public void visit(VariableDeclarator declaration, IStructure arg) {
        VariableOrConst result = new VariableOrConst(fromParserToMyType(declaration.getType()),
                declaration.getNameAsString());

        // TODO: process init

        children.offerFirst(result);
    }

    @Override
    public void visit(ObjectCreationExpr expression, IStructure arg) {
        // skip anonymous class
    }

    @Override
    public void visit(MethodDeclaration declaration, IStructure arg) {

        ArrayDeque<IStructure> backup = children.clone();
        children.clear();

        Type returnValue = fromParserToMyType(declaration.getType());
        ArrayList<VariableOrConst> variablesAndConsts = new ArrayList<>();

        // Обработка параметров генерик
        NodeList<TypeParameter> paramsSource = declaration.getTypeParameters();

        // TODO: transfer type params from String[] to Type[]
        String[] params = new String[paramsSource.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(paramsSource.get(i)).toString();
        }

        // Обработка параметров функции
        NodeList<Parameter> argParamsSource = declaration.getParameters();
        Type[] argParams = new Type[argParamsSource.size()];
        for (int i = 0; i < argParams.length; ++i) {
            Type paramType = fromParserToMyType(argParamsSource.get(i).getType());
            variablesAndConsts.add(new VariableOrConst(paramType, argParamsSource.get(i).getNameAsString()));

            argParams[i] = paramType;
        }


        DefinedFunction result = new DefinedFunction(declaration.getNameAsString(), argParams, params, returnValue);
        result.variablesAndConsts.addAll(variablesAndConsts);

        // Обработка внутренностей
        if (declaration.getBody().isPresent()) {
            visit(declaration.getBody().get(), result);
        }
        processChildren(result);

        children.addAll(backup);
        children.offerFirst(result);

    }

    // TODO: copy (almost) of MethodDeclaration visitor
    @Override
    public void visit(ConstructorDeclaration declaration, IStructure arg) {
        ArrayDeque<IStructure> backup = children.clone();
        children.clear();

        // TODO: надо не забыть про генерики при инициализации
        Type returnValue = ((DefinedClass) arg).getType();
        ArrayList<VariableOrConst> variablesAndConsts = new ArrayList<>();

        // Обработка параметров генерик
        NodeList<TypeParameter> paramsSource = declaration.getTypeParameters();

        // TODO: transfer type params from String[] to Type[]
        String[] params = new String[paramsSource.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(paramsSource.get(i)).toString();
        }

        // Обработка параметров функции
        NodeList<Parameter> argParamsSource = declaration.getParameters();
        Type[] argParams = new Type[argParamsSource.size()];
        for (int i = 0; i < argParams.length; ++i) {
            Type paramType = fromParserToMyType(argParamsSource.get(i).getType());
            variablesAndConsts.add(new VariableOrConst(paramType, argParamsSource.get(i).getNameAsString()));

            argParams[i] = paramType;
        }


        DefinedFunction result = new DefinedFunction(declaration.getNameAsString(), argParams, params, returnValue);
        result.variablesAndConsts.addAll(variablesAndConsts);

        // Обработка внутренностей
        visit(declaration.getBody(), result);
        processChildren(result);

        children.addAll(backup);
        children.offerFirst(result);

    }

    // Преобразование типов JavaParser в собственные
    private static Type fromParserToMyType(com.github.javaparser.ast.type.Type type) {
        if (type.isPrimitiveType()) {
            return new Type(type.asPrimitiveType().asString());
        }

        // Ссылочные типы
        if (type.isArrayType()) {
            ArrayType t = type.asArrayType();
            return generateArray(t.getArrayLevel(), fromParserToMyType(t.getElementType()));
        }
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType t = type.asClassOrInterfaceType();
            ArrayList<Type> params = new ArrayList<>();

            Optional<NodeList<com.github.javaparser.ast.type.Type>> args = t.getTypeArguments();
            if (args.isPresent()) {
                for (com.github.javaparser.ast.type.Type arg : args.get()) {
                    params.add(fromParserToMyType(arg));
                }
            }

            return new Type(t.getNameAsString(), params);
        }
        if (type.isTypeParameter()) {
            return new Type(type.asTypeParameter().getNameAsString());
        }
        if (type.isVoidType()) {
            return Type.getVoidType();
        }
        if (type.isIntersectionType()) {
            // TODO: для типов вида Type1 & Type2 пока вот так - потом подумаю
            return Type.getUndefinedType();
        }
        if (type.isUnionType()) {
            // TODO: для типов вида Exception1 | Exception2 пока вот так - потом подумаю
            return Type.getExceptionType();
        }
        if (type.isVarType()) {
            return Type.getVarType();
        }

        // TODO: form normal exception type
        throw new UnsupportedOperationException("AnalysisVisitor.fromParserToMyType: unsupported type of type!");
    }

    // Обертывание типов в массивы
    private static Type generateArray(int dimensions, Type innerType) {
        if (dimensions == 0) {
            return innerType;
        }

        Type result = new Type(TYPE_ARRAY);
        Type curDimension = result;
        for (int i = 1; i < dimensions; ++i) {
            Type tempType = new Type(TYPE_ARRAY);
            curDimension.getParams().add(tempType);
            curDimension = tempType;
        }
        curDimension.getParams().add(innerType);
        return result;
    }

    // Единая обработка потомков для всех типов
    private void processChildren(IStructure target) {
        ArrayList<DefinedClass> classes = new ArrayList<>();
        ArrayList<DefinedFunction> functions = new ArrayList<>();
        ArrayList<VariableOrConst> vars = new ArrayList<>();

        // Children separation
        while (!children.isEmpty()) {
            IStructure child = children.pollFirst();
            child.setParent(target);
            switch (child.getStrucType()) {
                case Class:
                    classes.add((DefinedClass) child);
                    break;
                case Function:
                    functions.add((DefinedFunction) child);
                    break;
                case Variable:
                    vars.add((VariableOrConst) child);
                    break;
                default:
                    // TODO: form normal exception type
                    throw new UnsupportedOperationException("AnalysisVisitor.processChildren: unsupported child type!");
            }
        }

        // Adding children to target
        switch (target.getStrucType()) {
            case Class:
                DefinedClass cl = (DefinedClass) target;
                cl.innerClasses.addAll(classes);
                cl.functions.addAll(functions);
                cl.variablesAndConsts.addAll(vars);
                break;
            case Function:
                DefinedFunction fu = (DefinedFunction) target;
                fu.innerClasses.addAll(classes);
                fu.variablesAndConsts.addAll(vars);
                break;
            case File:
                FileRepresentative fi = (FileRepresentative) target;
                fi.classes.addAll(classes);
                break;
            default:
                // TODO: form normal exception type
                throw new UnsupportedOperationException("AnalysisVisitor.processChildren: unsupported target type!");
        }
    }
}