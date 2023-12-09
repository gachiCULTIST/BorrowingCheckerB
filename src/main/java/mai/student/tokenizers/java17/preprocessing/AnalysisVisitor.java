package mai.student.tokenizers.java17.preprocessing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import mai.student.intermediateStates.*;
import mai.student.intermediateStates.Package;

import java.util.*;

// При обработке (всего) дерева необходимо обязательно вторым параметром передавать FileRepresentative
// Вторым аргументом выступает родительский элемент собственной модели
public class AnalysisVisitor extends GenericListVisitorAdapter<IStructure, IStructure> {

    private static final String TYPE_ARRAY = "Array";

    // Используется для отделения внутренних классов перечислений от классов представляющих констант
    private static final String ENUM_INNER_PREFIX = "?ENUM_";

    private final Map<CallableDeclaration<?>, DefinedFunction> methodMatcher;

    public AnalysisVisitor(Map<CallableDeclaration<?>, DefinedFunction> methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    @Override
    public List<IStructure> visit(CompilationUnit unit, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;

        List<IStructure> children = super.visit(unit, arg);

        processChildren(file, children);

        return new LinkedList<>();
    }

    @Override
    public List<IStructure> visit(PackageDeclaration declaration, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;
        file.curPackage = new Package(declaration.getName());

        return super.visit(declaration, arg);
    }

    @Override
    public List<IStructure> visit(ImportDeclaration declaration, IStructure arg) {
        FileRepresentative file = (FileRepresentative) arg;

        if (declaration.isStatic()) {
            file.staticImports.add(new Import(declaration.getName(), true, declaration.isAsterisk()));
        } else {
            file.imports.add(new Import(declaration.getName(), false, declaration.isAsterisk()));
        }

        return super.visit(declaration, arg);
    }

    @Override
    public List<IStructure> visit(RecordDeclaration recordDeclaration, IStructure arg) {
        // Обработка реализуемых интерфейсов
        ArrayList<Type> inheritanceList = new ArrayList<>();
        for (ClassOrInterfaceType inhInterface : recordDeclaration.getImplementedTypes()) {
            inheritanceList.add(fromParserToMyType(inhInterface));
        }

        // Обработка параметров записи
        List<TypeParameter> parserParams = recordDeclaration.getTypeParameters();
        Type[] params = new Type[parserParams.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(parserParams.get(i));
        }

        // Начальная инициализация
        DefinedClass result = new DefinedClass(recordDeclaration.getNameAsString(), params, inheritanceList, arg);

        // Обработка внутренностей
        List<IStructure> children = super.visit(recordDeclaration, result);
        processChildren(result, children);

        // Добавление конструктора по умолчанию, если ни одного не было объявлено
        boolean hasDefault = result.functions.stream().reduce(false,
                (f, i) -> f.equals(true) || i.getName().equals(result.getName()),
                (r1, r2) -> r1 || r2);
        if (!hasDefault) {
            DefinedFunction defaultConstructor = new DefinedFunction(result.getName(), new Type[0], new Type[0],
                    result.getType(), result, null, null);
            result.functions.add(defaultConstructor);
        }

        // Добавление параметры в виде переменных
        for (Parameter parameter : recordDeclaration.getParameters()) {
            VariableOrConst p = new VariableOrConst(fromParserToMyType(parameter.getType()),
                    parameter.getNameAsString(), result, parameter.getBegin().orElse(null));

            result.variablesAndConsts.add(p);

            // Добавление геттеров для параметров
            boolean hasSame = result.functions.stream().reduce(false,
                    (f, i) -> f.equals(true) || i.getName().equals(parameter.getNameAsString()),
                    (r1, r2) -> r1 || r2);
            if (!hasSame) {
                DefinedFunction getter = new DefinedFunction(parameter.getNameAsString(), new Type[0], new Type[0],
                        p.getType(), result, null, null);
                result.functions.add(getter);
            }
        }

        // Добавление канонического конструктора

            // Тело если есть компактный конструктор
        BlockStmt compactBody = recordDeclaration.getCompactConstructors().stream().reduce(null,
                (acc, i) -> i.getBody(),
                (r1, r2) -> r1 == null ? r2 : r1);

            // Определяем параметры для канонического конструктора
        Type[] recordParameters = recordDeclaration.getParameters().stream().map(Parameter::getType).
                map(AnalysisVisitor::fromParserToMyType).toArray(Type[]::new);

            // Проверяем есть ли канонический конструктор
        boolean hasCanonical = result.functions.stream().reduce(false,
                (f, i) -> {
                    if (!f && i.getName().equals(result.getName())) {
                        return Type.equalsTypeArrays(i.getArgTypes(), recordParameters);
                    }
                    return f;
                },
                (r1, r2) -> r1 || r2);

            // Добавляем канонический конструктор, если его нет
        if (!hasCanonical) {
            DefinedFunction defaultConstructor = new DefinedFunction(result.getName(), recordParameters, new Type[0],
                    result.getType(), result, compactBody, null);
            result.functions.add(defaultConstructor);
        }

        return new LinkedList<>(){{
            add(result);
        }};
    }

    @Override
    public List<IStructure> visit(ClassOrInterfaceDeclaration declaration, IStructure arg) {

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
        Type[] params = new Type[parserParams.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(parserParams.get(i));
        }

        // Начальная инициализация
        DefinedClass result = new DefinedClass(declaration.getNameAsString(), params, inheritanceList, arg);

        // Обработка внутренностей
        List<IStructure> children = super.visit(declaration, result);
        processChildren(result, children);

        // Добавление конструктора по умолчанию, если ни одного не было объявлено
        boolean hasDefault = result.functions.stream().reduce(false,
                (f, i) -> f.equals(true) || i.getName().equals(result.getName()),
                (r1, r2) -> r1 || r2);
        if (!hasDefault) {
            DefinedFunction defaultConstructor = new DefinedFunction(result.getName(), new Type[0], new Type[0],
                    result.getType(), result, null, null);
            result.functions.add(defaultConstructor);
        }

        return new LinkedList<>(){{
            add(result);
        }};
    }

    public List<IStructure> visit(EnumDeclaration declaration, IStructure arg) {

        // Обработка реализуемых интерфейсов
        ArrayList<Type> inheritanceList = new ArrayList<>();
        for (ClassOrInterfaceType inhInterface : declaration.getImplementedTypes()) {
            inheritanceList.add(fromParserToMyType(inhInterface));
        }

        // Начальная инициализация
        DefinedClass result = new DefinedClass(declaration.getNameAsString(), new Type[0], inheritanceList, arg);

        // Обработка внутренностей
        List<IStructure> children = super.visit(declaration, result);
        processChildren(result, children);

        return new LinkedList<>() {{
            add(result);
        }};
    }

    public List<IStructure> visit(EnumConstantDeclaration declaration, IStructure arg) {

        // Указываем объявление перечисление как родительский класс
        ArrayList<Type> inheritanceList = new ArrayList<>() {{
            add(new Type(arg.getName(), null, (DefinedClass) arg));
        }};


        // Начальная инициализация
        DefinedClass resultClass = new DefinedClass(ENUM_INNER_PREFIX + declaration.getNameAsString(),
                new Type[0], inheritanceList, arg);
        VariableOrConst resultVar = new VariableOrConst(resultClass.getType(), declaration.getNameAsString(), arg,
                declaration.getBegin().orElse(null));

        // Обработка внутренностей
        List<IStructure> children = super.visit(declaration, resultClass);
        processChildren(resultClass, children);

        return new LinkedList<>() {{
            add(resultClass);
            add(resultVar);
        }};
    }

    @Override
    public List<IStructure> visit(VariableDeclarator declaration, IStructure arg) {
        VariableOrConst result = new VariableOrConst(fromParserToMyType(declaration.getType()),
                declaration.getNameAsString(), arg, declaration.getBegin().orElse(null));

        return new LinkedList<>() {{
            add(result);
        }};
    }

    @Override
    public List<IStructure> visit(ObjectCreationExpr expression, IStructure arg) {
        // skip anonymous class
        return new LinkedList<>();
    }

    @Override
    public List<IStructure> visit(MethodDeclaration declaration, IStructure arg) {
        Type returnValue = fromParserToMyType(declaration.getType());
        ArrayList<VariableOrConst> variablesAndConsts = new ArrayList<>();

        // Обработка параметров генерик
        NodeList<TypeParameter> paramsSource = declaration.getTypeParameters();

        Type[] params = new Type[paramsSource.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(paramsSource.get(i));
        }

        // Объявление массива типов параметров
        NodeList<Parameter> argParamsSource = declaration.getParameters();
        Type[] argParams = new Type[argParamsSource.size()];

        // Объявление целевого метода
        DefinedFunction result = new DefinedFunction(declaration.getNameAsString(), argParams, params, returnValue,
                (DefinedClass) arg, declaration.getBody().orElse(null), declaration);
        methodMatcher.put(declaration, result);

        // Обработка параметров функции
        for (int i = 0; i < argParams.length; ++i) {
            Type paramType = fromParserToMyType(argParamsSource.get(i).getType());
            variablesAndConsts.add(new VariableOrConst(paramType, argParamsSource.get(i).getNameAsString(), result,
                    argParamsSource.get(i).getBegin().orElse(null)));

            argParams[i] = paramType;
        }

        result.variablesAndConsts.addAll(variablesAndConsts);

        // Обработка внутренностей
        List<IStructure> children = new LinkedList<>();
        if (declaration.getBody().isPresent()) {
            children.addAll(visit(declaration.getBody().get(), result));
        }
        processChildren(result, children);

        return new LinkedList<>() {{
            add(result);
        }};
    }

    @Override
    public List<IStructure> visit(ConstructorDeclaration declaration, IStructure arg) {

        Type returnValue = ((DefinedClass) arg).getType();
        ArrayList<VariableOrConst> variablesAndConsts = new ArrayList<>();

        // Обработка параметров генерик
        NodeList<TypeParameter> paramsSource = declaration.getTypeParameters();

        Type[] params = new Type[paramsSource.size()];
        for (int i = 0; i < params.length; ++i) {
            params[i] = fromParserToMyType(paramsSource.get(i));
        }

        // Объявление массива типов параметров
        NodeList<Parameter> argParamsSource = declaration.getParameters();
        Type[] argParams = new Type[argParamsSource.size()];

        // Объявление целевого конструктора
        DefinedFunction result = new DefinedFunction(declaration.getNameAsString(), argParams, params, returnValue,
                (DefinedClass) arg, declaration.getBody(), declaration);
        methodMatcher.put(declaration, result);

        // Обработка параметров функции
        for (int i = 0; i < argParams.length; ++i) {
            Type paramType = fromParserToMyType(argParamsSource.get(i).getType());
            variablesAndConsts.add(new VariableOrConst(paramType, argParamsSource.get(i).getNameAsString(), result,
                    argParamsSource.get(i).getBegin().orElse(null)));

            argParams[i] = paramType;
        }

        result.variablesAndConsts.addAll(variablesAndConsts);

        // Обработка внутренностей
        List<IStructure> children = visit(declaration.getBody(), result);
        processChildren(result, children);

        return new LinkedList<>() {{
            add(result);
        }};
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

            Qualifier qualifier = null;
            if (t.getScope().isPresent()) {
                qualifier = new Qualifier(processScope(t.getScope().get()));
            }

            ArrayList<Type> params = new ArrayList<>();

            Optional<NodeList<com.github.javaparser.ast.type.Type>> args = t.getTypeArguments();
            if (args.isPresent()) {
                for (com.github.javaparser.ast.type.Type arg : args.get()) {
                    params.add(fromParserToMyType(arg));
                }
            }

            return new Type(t.getNameAsString(), params, qualifier);
        }
        if (type.isTypeParameter()) {
            return new Type(type.asTypeParameter().getNameAsString(), true);
        }
        if (type.isVoidType()) {
            return Type.getVoidType();
        }
        if (type.isIntersectionType()) {
            // для типов вида Type1 & Type2 пока вот так
            return Type.getUndefinedType();
        }
        if (type.isUnionType()) {
            // для типов вида Exception1 | Exception2 пока вот так
            return Type.getExceptionType();
        }
        if (type.isVarType()) {
            return Type.getVarType();
        }
        if (type.isWildcardType()) {
            // для ? пока вот так
            return Type.getObjectType();
        }

        throw new UnsupportedOperationException("AnalysisVisitor.fromParserToMyType: unsupported type of type!");
    }

    // Обработка квалифаеров
    private static Collection<String> processScope(ClassOrInterfaceType scope) {
        if (scope == null) {
            return new ArrayList<>();
        }

        Collection<String> result = processScope(scope.getScope().orElse(null));
        result.add(scope.getNameAsString());
        return result;
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
    private void processChildren(IStructure target, List<IStructure> children) {
        ArrayList<DefinedClass> classes = new ArrayList<>();
        ArrayList<DefinedFunction> functions = new ArrayList<>();
        ArrayList<VariableOrConst> vars = new ArrayList<>();

        // Children separation
        for (IStructure child : children) {
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
                throw new UnsupportedOperationException("AnalysisVisitor.processChildren: unsupported target type!");
        }
    }
}