package mai.student.tokenizers.java17.preprocessing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.intermediateStates.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mai.student.UtilityClass.*;

public class Preprocessor {

    // Заменитель строк, поскольку кавычки мешашуются при анализе
    private static final String REGEX_PARAM = "([\\wа-яА-ЯёЁ]+)";
    private static final int PARAMS_GROUP = 1;
    private static final String REGEX_VARIABLE = "([\\wа-яА-ЯёЁ]+\\s+)*(([\\wа-яА-ЯёЁ]+)\\s*(<" +
            "([\\wа-яА-ЯёЁ<>\\[\\],\\s]+)>|\\s+|((\\[\\s*\\]\\s*)+))\\s*((\\[\\s*\\]\\s*)+)?)\\s*([\\wа-яА-ЯёЁ]+)\\s*" +
            "((\\[\\s*\\]\\s*)+)?\\s*(;|=|\\)|,|:)";
    private static final int VAR_TYPE = 2;
    private static final int VAR_ID = 10;
    private static final int VAR_BRACKETS = 11;
    private static final int VAR_OPERATOR = 13;
    private static final String REGEX_VAR_LIST = ",\\s*([\\wа-яА-ЯёЁ]+)\\s*((\\[\\s*\\]\\s*)+)?\\s*(;|=|,)";
    private static final int VAR_LIST_ID = 1;
    private static final int VAR_LIST_BRACKETS = 2;
    private static final int VAR_LIST_OPERATOR = 4;
    private static final int FUNC_PARAMS = 6;
    private static final int FUNC_RETURN_VALUE1 = 1;
    private static final int FUNC_RETURN_VALUE2 = 13;
    private static final int FUNC_RETURN_VALUE_WITH_GEN = 7;
    private static final int FUNC_NAME = 19;
    private static final int FUNC_ARGS = 20;
    private static final int FUNC_PROTOTYPE = 38;
    private static final int STRUCT_ISCLASS = 23;
    private static final int CLASS_NAME = 26;
    private static final int CLASS_PARAMS = 27;
    private static final int CLASS_EXTENDS1 = 31;
    private static final int CLASS_EXTENDS2 = 35;
    private static final int CLASS_IMPLEMENTS = 33;
    private static final String TYPE_ARRAY = "Array";

    private static final String REG_TYPE = "([\\wа-яА-ЯёЁ]+)\\s*(<([\\wа-яА-ЯёЁ<>\\s,\\[\\]]+)>\\s*)?((\\[\\s*\\]\\s*)+)?";
    private static final int REG_TYPE_NAME = 1;
    private static final int REG_TYPE_PARAMS = 3;
    private static final int REG_TYPE_ARRAY = 4;
    private static final String REGEX_ARG_TYPE = "(final\\s+)?(" + REG_TYPE + ")([\\wа-яА-ЯёЁ]+)\\s*((\\[\\s*\\]\\s*)+)?";
    private static final int ARG_TYPE_GROUP = 2;
    private static final int ARG_ID_GROUP = 8;
    private static final int ARG_BRACKETS1 = 6;
    private static final int ARG_BRACKETS2 = 9;
    private static final String TYPE_UNDEFINED = "null";
    private static final String TYPE_VAR = "var";
    private static final String OPERATOR_NEW = "new";

    private FileRepresentative file;

    // For Parser
    private final JavaParser parser = new JavaParser();

    public Preprocessor(FileRepresentative file) {
        this.file = file;

        // Настройка парсера
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        // TODO: договориться о принципе получения директории с исходниками
        typeSolver.add(new JavaParserTypeSolver(file.getFilePath().getParent()));

        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false).
                setSymbolResolver(new JavaSymbolSolver(typeSolver)));
    }

    public void preprocess() {

        try {
            new AnalysisVisitor().visit(StaticJavaParser.parse(file.getFilePath()), file);
        } catch (IOException e) {
            //TODO: пока буду пробрасывать такое исключение - надо заменить
            throw new RuntimeException(e.getMessage());
        }

        // TODO: set new implementation
//        Cleaner cleaner = new Cleaner(file);
//        file.code = cleaner.clean();
//
//        structAnalyze(file.code, file, 0, file.code.length() - 1);

    }

    private void structAnalyze(StringBuilder code, IStructure analysedStruct, int indexStart, int endIndex) {
        Pattern pattern = Pattern.compile("([\\wа-яА-ЯёЁ]+\\s+)*(((((<[\\wа-яА-ЯёЁ,<>&\\s]+>)\\s*" +
                "(([\\wа-яА-ЯёЁ]+)\\s*(<([\\wа-яА-ЯёЁ<>\\s,\\[\\]]+)>\\s*)?((\\[\\s*\\]\\s*)+)?)\\s+)|" +
                "(([\\wа-яА-ЯёЁ]+)\\s*(<([\\wа-яА-ЯёЁ<>\\s,\\[\\]]+)>\\s*)?((\\[\\s*\\]\\s*)+)?\\s+))?" +
                "([\\wа-я-А-ЯёЁ]+)\\s*\\(([\\wа-я-А-ЯёЁ,<>\\[\\]\\s]*)\\)(\\s+throws\\s+[\\wа-я-А-ЯёЁ\\s,\\.]+)?)" +
                "|(((class)|(interface))\\s+([\\w\\dа-я-А-ЯёЁ]+)\\s*(<[\\wа-я-А-ЯёЁ,<>&\\s]+>)?\\s*" +
                "(((extends\\s+([\\wа-я-А-ЯёЁ<>,\\s\\[\\]]+)\\s*)?implements\\s+" +
                "(([\\wа-я-А-ЯёЁ<>,\\s\\[\\]]+\\s*,?\\s*)+))|(extends\\s+([\\wа-я-А-ЯёЁ<>,\\s\\[\\]]+)\\s*))?))" +
                "\\s*((\\{)|(;))");
        Matcher matcher = pattern.matcher(code);

        ArrayList<DefinedClass> classes = new ArrayList<>();
        ArrayList<DefinedFunction> functions = null;
        if (analysedStruct != null && analysedStruct.getStrucType() == StructureType.Class) {
            functions = new ArrayList<>();
        }

        int pointer = indexStart;
        boolean isFound = true;
        while (isFound) {
//            System.out.println(177);
            isFound = matcher.find(pointer);
            if (isFound) {
//                System.out.println(matcher.group(0)+"\n"+1);
                pointer = matcher.end();
            }

            if (isFound && pointer < endIndex) {
//                System.out.println(3);
                int start = pointer;

                if (matcher.group(STRUCT_ISCLASS) == null) {
//                    System.out.println("Preprocessor: " + matcher.group(0));

                    // Костыыыыыыль
                    // Инициализация поля класса при его объявлении через new определяется как функция
                    if (matcher.group(FUNC_RETURN_VALUE1) != null &&
                            matcher.group(FUNC_RETURN_VALUE1).strip().equals(OPERATOR_NEW)) {
                        continue;
                    }

                    if (matcher.group(FUNC_PROTOTYPE) == null) {
                        pointer = findLastFigure(code, start, "{", "}");
                    } else {
                        --pointer;
                    }

                    // Получение параметров функции
                    String[] params = null;
                    if (matcher.group(FUNC_PARAMS) != null) {
                        params = separateParams(matcher.group(FUNC_PARAMS));
                    }

                    // Получение типов аргументов функции
                    ArrayList<Type> argTypes = new ArrayList<>();
                    ArrayList<VariableOrConst> args = new ArrayList<>();
//                    System.out.println(6);
                    if (matcher.group(FUNC_ARGS) != null) {
                        ArrayList<String> tempArgs = separateArgsAndImplementation(matcher.group(FUNC_ARGS));

                        for (String arg : tempArgs) {
                            Pattern argPattern = Pattern.compile(REGEX_ARG_TYPE);
                            Matcher argMatcher = argPattern.matcher(arg);
                            if (argMatcher.find()) {

                                Type argType = getTypeFromString(argMatcher.group(ARG_TYPE_GROUP));
                                String argID = argMatcher.group(ARG_ID_GROUP);

                                argType = generateArray(countSquareBrackets(arg, REGEX_ARG_TYPE,
                                        new int[]{ARG_BRACKETS2}).get(0), argType);
                                argTypes.add(argType.clone());

                                VariableOrConst argVar = new VariableOrConst(argType, argID, start - 1);
                                args.add(argVar);
                            }
                        }
                    }
                    // Является функция рекурсивной
                    boolean isRecurrent = false;
                    Pattern checkRec = Pattern.compile("[^\\w_]" + matcher.group(FUNC_NAME) + "\\s*\\(");
                    Matcher matchRec = checkRec.matcher(code);
//                    System.out.println(1);
                    if (matchRec.find(start) && matchRec.end() < pointer) {
//                        System.out.println(1);
                        isRecurrent = true;
                    }

                    // Обработка возвращаемого значения в случае конструкторов
                    String returnValue;
                    if (analysedStruct != null && analysedStruct.getStrucType() == StructureType.Class) {
                        if (matcher.group(FUNC_RETURN_VALUE2) != null) {
                            returnValue = matcher.group(FUNC_RETURN_VALUE2).strip();
                        } else if (matcher.group(FUNC_RETURN_VALUE_WITH_GEN) != null) {
                            if (((DefinedClass) analysedStruct).getName().equals(matcher.group(FUNC_NAME))) {
                                returnValue = ((DefinedClass) analysedStruct).getName();
                            } else {
                                returnValue = matcher.group(FUNC_RETURN_VALUE_WITH_GEN).strip();
                            }
                        } else if (((DefinedClass) analysedStruct).getName().equals(matcher.group(FUNC_NAME))) {
                            returnValue = ((DefinedClass) analysedStruct).getName();
                        } else if (matcher.group(FUNC_RETURN_VALUE1) != null) {
                            returnValue = matcher.group(FUNC_RETURN_VALUE1).strip();
                        }else {
                            continue;
                        }
                    } else {
                        continue;
                    }

                    Type[] types = new Type[argTypes.size()];
                    types = argTypes.toArray(types);
                    DefinedFunction tempFunc = new DefinedFunction(matcher.group(FUNC_NAME), types, matcher.start(),
                            start - 1, pointer, params, getTypeFromString(returnValue), isRecurrent, (DefinedClass) analysedStruct);
                    tempFunc.variablesAndConsts.addAll(args);

                    functions.add(tempFunc);

                    // Конструктор по умолчанию
                    structAnalyze(code, tempFunc, tempFunc.getBlockStart(), tempFunc.getEndIndex());
                } else {
//                    System.out.println(5);
                    pointer = findLastFigure(code, start, "{", "}");

                    // Получение параметров класса
                    String[] params = null;
                    if (matcher.group(CLASS_PARAMS) != null) {
                        params = separateParams(matcher.group(CLASS_PARAMS));
                    }

                    DefinedClass tempClass = new DefinedClass(matcher.group(CLASS_NAME), start - 1, pointer,
                            params, analysedStruct);

                    // Получение списка наследования и реализации
                    if (matcher.group(CLASS_EXTENDS1) != null) {
                        tempClass.inheritanceList.add(getTypeFromString(matcher.group(CLASS_EXTENDS1)));
                    } if (matcher.group(CLASS_EXTENDS2) != null) {
                        tempClass.inheritanceList.add(getTypeFromString(matcher.group(CLASS_EXTENDS2)));
                    }
                    if (matcher.group(CLASS_IMPLEMENTS) != null) {
                        ArrayList<Type> inheritanceList = new ArrayList<>();
                        for (String inhType : separateArgsAndImplementation(matcher.group(CLASS_IMPLEMENTS).strip())) {
                            inheritanceList.add(getTypeFromString(inhType));
                        }
                        tempClass.inheritanceList.addAll(inheritanceList);
                    }

                    classes.add(tempClass);
                    structAnalyze(code, tempClass, tempClass.getStartIndex(), tempClass.getEndIndex());
                }
            }
        }

        if (analysedStruct != null && analysedStruct.getStrucType() == StructureType.File) {
            ((FileRepresentative) analysedStruct).classes = classes;
        } else {
            Pattern varSearch = Pattern.compile(REGEX_VARIABLE);
            Matcher varSearcher = varSearch.matcher(code);

            pointer = indexStart;
            ArrayList<VariableOrConst> vars = new ArrayList<>();
//            System.out.println(11);
            while (varSearcher.find(pointer)) {
//                System.out.println(11);
                boolean isDeeper = false;
                int start = pointer = varSearcher.end() - 1;

                if (pointer > endIndex) {
                    break;
                }

                for (DefinedClass tmClass : classes) {
                    if (start >= tmClass.getStartIndex() && start <= tmClass.getEndIndex()) {
                        isDeeper = true;
                        break;
                    }
                }
                if (functions != null && !isDeeper) {
                    for (DefinedFunction tmFunc : functions) {
                        if (start >= tmFunc.getStartIndex() && start <= tmFunc.getEndIndex()) {
                            isDeeper = true;
                            break;
                        }
                    }
                }

                if (isDeeper) {
                    continue;
                } else {
                    if (varSearcher.group(VAR_TYPE).equals("return")) {
                        continue;
                    }

                    // Формирование типа
                    Type varType, commonType;
                    int varDimensions;
                    if (varSearcher.group(VAR_TYPE).strip().equals(TYPE_VAR)) {
                        varType = new Type(TYPE_UNDEFINED);
                        commonType = varType.clone();
                    } else {
                        varType = getTypeFromString(varSearcher.group(VAR_TYPE).strip());
                        commonType = varType.clone();
                        varDimensions = countSquareBrackets(varSearcher.group(0), REGEX_VARIABLE,
                                new int[]{VAR_BRACKETS}).get(0);

                        if (varDimensions != 0) {
                            varType = generateArray(varDimensions, varType);
                        }
                    }

                    VariableOrConst tempVar = new VariableOrConst(varType, varSearcher.group(VAR_ID), start);
                    vars.add(tempVar);

                    // Чтение если идет список переменных
                    String oper = varSearcher.group(VAR_OPERATOR);
                    if (oper.equals("=") || oper.equals(",")) {
                        Pattern varList = Pattern.compile(REGEX_VAR_LIST);
                        Matcher varListFinder = varList.matcher(code);

                        int declareEnd = code.indexOf(";", pointer);
                        ++pointer;
                        ArrayList<Integer> bracketIndexes = new ArrayList<>();

                        // Вызовы методов в выражении
                        int leftIndex = code.indexOf("(", pointer);
                        while (leftIndex != -1 && leftIndex < declareEnd) {
                            while (leftIndex != -1 && leftIndex < declareEnd && code.charAt(leftIndex - 1) == '\'' &&
                                    code.charAt(leftIndex + 1) == '\'') {
                                leftIndex = code.indexOf("(", leftIndex + 1);
                            }

                            if (leftIndex > declareEnd || leftIndex == -1) {
                                break;
                            }

                            bracketIndexes.add(leftIndex);
                            leftIndex = findLastFigure(code, leftIndex + 1, "(", ")");
                            while (leftIndex != -1 && leftIndex < declareEnd && code.charAt(leftIndex - 1) == '\'' &&
                                    code.charAt(leftIndex + 1) == '\'') {
                                leftIndex = findLastFigure(code, leftIndex + 1, "(", ")");
                            }
                            bracketIndexes.add(leftIndex);

                            leftIndex = code.indexOf("(", leftIndex + 1);
                        }

                        // Генерики в выражении
                        ArrayList<Integer> genericImplication = new ArrayList<>();
                        Pattern genImpPattern = Pattern.compile("\\s*[\\d\\w]+[\\d\\w\\[\\]<>,\\s]*>");
                        Matcher genImpFinder = genImpPattern.matcher(code);
                        int genPointer = pointer;

//                        System.out.println(12);
                        while (genImpFinder.find(genPointer) && genImpFinder.end() <= declareEnd) {
//                            System.out.println(12);
                            genPointer = genImpFinder.end();
                            genericImplication.add(genImpFinder.start());
                            genericImplication.add(genImpFinder.end());
                        }

                        --pointer;
//                        System.out.println(13);
                        while (varListFinder.find(pointer) && varListFinder.end() - 1 <= declareEnd) {
//                            System.out.println(13);
                            pointer = varListFinder.end() - 1;

                            boolean skip = false;
                            for (int i = 0; i < bracketIndexes.size(); i += 2) {
                                if (bracketIndexes.get(i) < varListFinder.start() && varListFinder.end() <=
                                        bracketIndexes.get(i + 1)) {
                                    skip = true;
                                }
                            }

                            if (skip) {
                                continue;
                            }

                            for (int i = 0; i < genericImplication.size(); i += 2) {
                                if (genericImplication.get(i) < varListFinder.start() && varListFinder.end() <
                                        genericImplication.get(i + 1)) {
                                    skip = true;
                                }
                            }

                            if (skip) {
                                continue;
                            }

                            varDimensions = countSquareBrackets(varListFinder.group(0), REGEX_VAR_LIST,
                                    new int[] {VAR_LIST_BRACKETS}).get(0);
                            varType = commonType.clone();

                            if (varDimensions != 0) {
                                varType = generateArray(varDimensions, varType);
                            }

                            VariableOrConst tVar = new VariableOrConst(varType, varListFinder.group(VAR_LIST_ID),
                                    varListFinder.end() - 1);
                            vars.add(tVar);
                        }

                        pointer = declareEnd + 1;
                    }
                }
            }

            if (analysedStruct != null && analysedStruct.getStrucType() == StructureType.Class) {
                DefinedClass analysedClass = (DefinedClass) analysedStruct;
                analysedClass.innerClasses.addAll(classes);
                analysedClass.functions.addAll(functions);

                // Конструктор по умолчанию
                analysedClass.functions.add(new DefinedFunction(analysedClass.getName(), new Type[0], analysedClass.getEndIndex(),
                        analysedClass.getEndIndex(), analysedClass.getEndIndex(), null,
                        new Type(analysedClass.getName()), false, analysedClass));

                analysedClass.variablesAndConsts.addAll(vars);

            } else {
                DefinedFunction analysedFunc = (DefinedFunction) analysedStruct;
                analysedFunc.innerClasses.addAll(classes);
                analysedFunc.variablesAndConsts.addAll(vars);

            }
        }
    }

    private static ArrayList<String> separateArgsAndImplementation(String str) {
        ArrayList<String> result = new ArrayList<>();

        int leftBorder = 0;
        int isGeneric = 0;

        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '<':
                    ++isGeneric;
                    break;
                case '>':
                    --isGeneric;
                    break;
                case ',':
                    if (isGeneric != 0) {
                        continue;
                    }

                    result.add(str.substring(leftBorder, i).strip());
                    leftBorder = i + 1;
                    break;
            }
        }

        result.add(str.substring(leftBorder).strip());
        return result;
    }
    private static String[] separateParams(String str) {
        ArrayList<String> result = new ArrayList<>();

        int leftBorder = 0;
        int isGeneric = 0;

        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '<':
                    ++isGeneric;
                    break;
                case '>':
                    --isGeneric;
                    break;
                case ',':
                    if (isGeneric != 0) {
                        continue;
                    }

                    result.add(extractParam(str.substring(leftBorder, i).strip()));
                    leftBorder = i + 1;
                    break;
            }
        }

        result.add(extractParam(str.substring(leftBorder).strip()));

        String[] r = new String[result.size()];
        result.toArray(r);
        return r;
    }

    private static String extractParam(String str) {
        Pattern pattern = Pattern.compile(REGEX_PARAM);
        Matcher matcher = pattern.matcher(str);

//        System.out.println(14);
        if (!matcher.find()) {
            throw new RuntimeException("Param extraction error!");
        }
//        System.out.println(14);

        return matcher.group(PARAMS_GROUP);
    }

    private static ArrayList<Type> getTypeList(String str) {
        int leftBorder = 0;
        int isGeneric = 0;
        ArrayList<Type> result = new ArrayList<>();

        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '<':
                    ++isGeneric;
                    break;
                case '>':
                    --isGeneric;
                    break;
                case ',':
                    if (isGeneric != 0) {
                        continue;
                    }

                    result.add(getTypeFromString(str.substring(leftBorder, i).strip()));
                    leftBorder = i + 1;
                    break;
            }
        }

        result.add(getTypeFromString(str.substring(leftBorder).strip()));
        return result;
    }

    private static Type getTypeFromString(String str) {
        Type result;
        ArrayList<Type> params = null;
        int genericStarts = -1;

        Pattern pattern = Pattern.compile(REG_TYPE);

//        System.out.println(15);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            throw new RuntimeException("Cant analyze type: " + str);
        }
//        System.out.println(25);

        // Get params
        if (matcher.group(REG_TYPE_PARAMS) != null) {
            params = getTypeList(matcher.group(REG_TYPE_PARAMS).strip());
        }

        // Generate innerType for Array types
        result = new Type(matcher.group(REG_TYPE_NAME), params);

        // Check Array brackets
        if (matcher.group(REG_TYPE_ARRAY) == null) {
            return result;
        } else {
            return generateArray(countSquareBrackets(str, REG_TYPE, new int[] {REG_TYPE_ARRAY}).get(0), result);
        }
    }

    public static Type generateArray(int dimensions, Type innerType) {
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
}