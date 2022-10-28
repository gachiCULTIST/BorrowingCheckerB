package mai.student.tokenizers.java17;

import mai.student.UtilityClass;
import mai.student.intermediateStates.*;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.lexing.Lexeme;
import mai.student.tokenizers.java17.lexing.LexemeType;
import mai.student.tokenizers.java17.lexing.Lexer;
import mai.student.tokenizers.java17.preprocessing.Preprocessor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mai.student.intermediateStates.IStructure.findEntity;
import static mai.student.intermediateStates.IStructure.mapParams;

public class JavaTokenizer extends AbstractTokenizer {

    private static Logger log = Logger.getLogger(JavaTokenizer.class.getName());

    private static final String DEFAULT_DICTIONARY = "tockenVocabularyJava17.txt";
    private static ClassLoader cl = JavaTokenizer.class.getClassLoader();

    // Заменитель строк, поскольку кавычки мешашуются при анализе
    private static final String IDENTIFIER_TOKEN_MAP = "*ident*";
    private static final String LITERAL_TOKEN_MAP = "*literal*";
    private static final String OPERATOR_NEW = "new";
    private static final String OPERATOR_TO = "to";
    private static final String OPERATOR_INSTANCE = "instanceof";
    private static final String OPERATOR_TRY = "try";
    private static final String TYPE_UNDEFINED = "null";
    private static final String TYPE_CHAR = "char";
    private static final String TYPE_ARRAY = "Array";

    public JavaTokenizer(ArrayList<Path> files, CodeLanguage lang) throws Exception {
        super(files, lang);

        try (Scanner scanner = new Scanner(cl.getResourceAsStream(DEFAULT_DICTIONARY))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public JavaTokenizer(ArrayList<Path> files, CodeLanguage lang, String dictionary) throws Exception {
        super(files, lang);

        try (Scanner scanner = new Scanner(cl.getResourceAsStream(dictionary))) {
            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                tokens.put(scanner.nextLine().strip(), id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    // TODO: tets timer
    public static long totalTime = 0;

    // Непосредственно функция будет возвращать массив токенов метода Main сравниваемой программы
    public void tokenize() {
        log.info("Preprocessing started.");

        // TODO: for test - delete
        Clock timer = Clock.systemDefaultZone();

        for (FileRepresentative file : files) {
            long time = timer.millis();
            new Preprocessor(file).preprocess();
            totalTime += timer.millis() - time;
//            System.out.println(file.code);
            UtilityClass.printInsideStructure(file, 0);
        }

        // TODO: новая версия препроцессора не может определить позиции элементов - поэтому пока без токенизации
//        DefinedFunction mainFunc = findMainFunction();
//        if (mainFunc == null) {
//            log.log(Level.SEVERE, "Can't find main method");
//            throw new UnsupportedOperationException("Can't find main method");
//        }
//
//        log.info("Tokenizing started.");
//        StringBuilder code = IStructure.getCode(mainFunc);
//        Lexer lexer = new Lexer(code, mainFunc, tokens);
//        tokenizeFunc(lexer, mainFunc, code, false);
//        result = mainFunc.tokens;

//         Запись рузельтатов теста
//        try (FileWriter saveResult = new FileWriter(files.get(0).getFilePath().getParent() + "/results/" /*"Статистика2022/results/"*/
//                + files.get(0).getFilePath().getFileName() + "_result.txt", false)) {
//            StringBuilder result = new StringBuilder();
//            for (int i : mainFunc.tokens) {
//                String eq = "";
//                for (Map.Entry var : tokens.entrySet()) {
//                    if ((int) var.getValue() == i) {
//                        eq = (String) var.getKey();
//                    }
//                }
//                result.append(i + eq + " ");
//            }
//
//            saveResult.write(result.toString());
//            saveResult.flush();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
    }

    private DefinedFunction findMainFunction() {
        for (FileRepresentative file : files) {
            for (DefinedClass innerClass : file.classes) {
                for (DefinedFunction func : innerClass.functions) {
                    if (func.getName().equals("main")) {
                        return func;
                    }
                }
            }
        }
        return null;
    }

    // Непосредственно процесс токенизации
    private ArrayList<Type> tokenizeFunc(Lexer lexer, DefinedFunction function, StringBuilder code, boolean isCall) {
        String curStruct = null; // Для определения разрешения имен функций
        IStructure structLink = null, prevStruct = null;

        ExpressionTypeSolver expression = null;
        if (isCall) {
            expression = new ExpressionTypeSolver();
        }
        ArrayList<Type> paramsTypes = new ArrayList<>();
        VariableOrConst initVar = null;
        DefinedClass typeCast = null;
        Lexeme prevLex = null;
        LinkedList<Object> expQueue = new LinkedList<>();

        boolean isDotOp = false, expressionIgnore = false, tryWithInit = false;

        int genericParamsLevel = -1;
        ArrayList<ArrayList<Type>> genericParams = new ArrayList<>();

        HashMap<String, Type> paramsMap = new HashMap<>();

        int bracketCounter = 1; // Определить конец вызова функции (или выбор элемента массива)

        Lexeme lexeme = lexer.getNextLexeme();
        while (lexeme.getType() != LexemeType.EoF) {

            // Определение типа лексемы и разделение по группам на основе схожести обработки
            switch (lexeme.getType()) {
                case Identifier:
                    if (genericParamsLevel != -1) { // Если идет указание параметров генерик, то пропускаем их поиск
                        genericParams.get(genericParamsLevel).add(new Type(lexeme.getContent()));
                        function.tokens.add(tokens.get(IDENTIFIER_TOKEN_MAP));
                    } else {

                        if (tokens.containsKey(lexeme.getContent()) && !OPERATOR_TO.equals(lexeme.getContent())) {
                            function.tokens.add(tokens.get(lexeme.getContent()));
                            curStruct = null;

                            if (expression != null && !expressionIgnore && !OPERATOR_NEW.equals(lexeme.getContent())) {
                                if (OPERATOR_INSTANCE.equals(lexeme.getContent())) {
                                    expQueue.addLast(lexeme);
                                } else {
                                    if (lexer.peekNextLexeme().getContent().equals("[")) {
                                        Type type = new Type(TYPE_ARRAY);
                                        type.getParams().add(new Type(lexeme.getContent()));

                                        structLink = new VariableOrConst(type,
                                                "", lexer.getPointer() - 1, function, this.files);
                                    } else {
                                        expQueue.addLast(new Lexeme(lexeme.getContent(), LexemeType.Cast));
                                        expQueue.addLast((new Type(lexeme.getContent())));
                                    }
                                }
                            }
                            if (OPERATOR_TRY.equals(lexeme.getContent()) &&
                                    "(".equals(lexer.peekNextLexeme().getContent())) {
                                tryWithInit = true;
                            }
                        } else {
                            boolean createParamsMap = false;
                            if (curStruct == null && structLink == null) {
                                createParamsMap = true;
                            }

                            curStruct = lexeme.getContent();
                            function.tokens.add(tokens.get(IDENTIFIER_TOKEN_MAP));
                            if (structLink != null && isDotOp) {
                                if (structLink != null && structLink.getStrucType() == StructureType.Variable) {
                                    VariableOrConst variableOrConst = (VariableOrConst) structLink;
                                    if (variableOrConst.linkToType == null) {

                                        IStructure link = findEntity(this.files, function,
                                                variableOrConst.getType().getName(), false, null);

                                        if (link != null) {
                                            if (link.getStrucType() == StructureType.Function) {
                                                variableOrConst.linkToType = ((DefinedFunction) link).parent;
                                            } else {
                                                variableOrConst.linkToType = (DefinedClass) link;
                                            }
                                        } else {
                                            variableOrConst.linkToType = null;
                                        }
                                    }

                                    if (variableOrConst.realType != null) {
                                        variableOrConst.realType.updateLinks(function, files);

                                        structLink = findEntity(this.files, variableOrConst.realType.getLinkToClass(),
                                                curStruct, true, null);

                                    } else {
                                        structLink = findEntity(this.files, variableOrConst.linkToType, curStruct,
                                                true, null);
                                    }
                                } else {
                                    structLink = findEntity(this.files, structLink, curStruct, true, null);
                                }
                            } else {
                                structLink = findEntity(this.files, function, curStruct, false, null);
                            }

                            if (structLink != null && createParamsMap) {
                                paramsMap = new HashMap<>();
                                if (structLink.getStrucType() == StructureType.Variable) {
                                    ((VariableOrConst) structLink).getType().updateLinks(function, files);
                                    mapParams(((VariableOrConst) structLink).getType(), paramsMap);
                                }
                            }

                            if (expression != null && !expressionIgnore && curStruct != null && !isDotOp && prevLex != null &&
                                    prevLex.getContent().equals("(") && lexer.peekNextLexeme().getContent().equals(")")) {
                                if (structLink == null) {
                                    expQueue.addLast(new Lexeme(curStruct, LexemeType.Cast));
                                    expQueue.addLast(new Type(curStruct));
                                } else if (structLink.getStrucType() == StructureType.Class) {
                                    typeCast = (DefinedClass) structLink;
                                    expQueue.addLast(new Lexeme(curStruct, LexemeType.Cast));
                                    expQueue.addLast(typeCast.getType(null));
                                }
                            } else if (expression != null && !expressionIgnore && structLink != null) {
                                switch (structLink.getStrucType()) {
                                    case Variable:
                                        expQueue.addLast(Type.getTypeWithMapping(((VariableOrConst) structLink).getType(), paramsMap));
                                        break;
                                    case Function:
                                        expQueue.addLast(Type.getTypeWithMapping(((DefinedFunction) structLink).getReturnValue(),
                                                paramsMap));
                                        break;
                                    case Class:
                                        expQueue.addLast(((DefinedClass) structLink).getType(null));
                                        break;
                                }
                            } else if (expression != null && !expressionIgnore) {
                                expQueue.addLast(new Type(TYPE_UNDEFINED));
                            }
                        }

                        isDotOp = false;
                    }
                    break;
                case Integer:
                case Long:
                case Float:
                case Double:
                case Bool:
                case Char:
                case String:
                    if (expression != null && !expressionIgnore) {
                        expQueue.addLast(new Type(lexeme.getType().getStr()));
                    }
                    function.tokens.add(tokens.get(LITERAL_TOKEN_MAP));

                    isDotOp = false;
                    structLink = null;
                    curStruct = null;
                    break;
                case Operator:

                    // Поскольку разделитель операторов не может отличить опреатор сдвига от двух символов больше
                    // делаем дополнительное условие
                    ArrayList<Lexeme> operators = new ArrayList<>();
                    if (genericParamsLevel != -1 && lexeme.getContent().startsWith(">>")) {
                        for (int i = 0; i < lexeme.getContent().length(); ++i) {
                            operators.add(new Lexeme(">", LexemeType.Operator));
                        }
                    } else {
                        operators.add(lexeme);
                    }


                    for (Lexeme op : operators) {
                        function.tokens.add(tokens.get(op.getContent()));

                        switch (op.getContent()) {
                            case "<":
                                if (isDotOp || genericParamsLevel != -1 || (curStruct != null && structLink == null) ||
                                        (structLink != null && structLink.getStrucType() == StructureType.Class)) {
                                    genericParams.add(new ArrayList<>());
                                    ++genericParamsLevel;
                                } else {
                                    if (expression != null && !expressionIgnore) {
                                        expQueue.addLast(op);
                                    }
                                    structLink = null;
                                    curStruct = null;
                                }
                                break;
                            case ">":
                                if (genericParamsLevel != -1) {

                                    if (genericParamsLevel > 0) {
                                        ArrayList<Type> level = genericParams.get(genericParamsLevel - 1);
                                        level.get(level.size() - 1).getParams().addAll(genericParams.get(genericParamsLevel));
                                        genericParams.remove(genericParamsLevel);
                                    }
                                    --genericParamsLevel;
                                } else {
                                    if (expression != null && !expressionIgnore) {
                                        expQueue.addLast(op);
                                    }
                                    structLink = null;
                                    curStruct = null;
                                    isDotOp = false;
                                }
                                break;
                            case ".":
                                if (expression != null && !expressionIgnore) {
                                    expQueue.pollLast();
                                }
                                prevStruct = structLink;
                                isDotOp = true;
                                break;
                            case "[":
                                ++bracketCounter;
                                if (genericParamsLevel == -1) {
                                    // Вызов обработкича параметров
                                    tokenizeFunc(lexer, function, code, true);
                                    --bracketCounter;

                                    Type t = null;
                                    if (structLink != null) {
                                        if (structLink.getStrucType() == StructureType.Class) {
                                            t = new Type(structLink.getName(), null, (DefinedClass) structLink);
                                            structLink = new VariableOrConst(t,
                                                    "", lexer.getPointer() - 1, function, this.files);
                                        } else {
                                            VariableOrConst tempVar = (VariableOrConst) structLink;
                                            if (tempVar.getType().getParams().isEmpty()) {
                                                t = new Type(TYPE_CHAR);
                                            } else {
                                                t = tempVar.getType().getParams().get(0);
                                            }
                                            structLink = new VariableOrConst(t,
                                                    "", lexer.getPointer() - 1, function, this.files);
                                        }
                                    } else {
                                        t = new Type(TYPE_UNDEFINED);
                                    }

                                    if (expression != null && !expressionIgnore) {
                                        expQueue.pollLast();
                                        expQueue.addLast(t);
                                    }


                                    isDotOp = false;
                                    curStruct = null;
                                } else {
                                    ArrayList<Type> level = genericParams.get(genericParamsLevel);
                                    Type arrType = Preprocessor.generateArray(1, level.get(level.size() - 1));
                                    level.remove(level.size() - 1);
                                    level.add(arrType);
                                }
                                break;
                            case "(":
                                ++bracketCounter;

                                if (structLink != null && structLink.getStrucType() == StructureType.Class) {
                                    structLink = findEntity(this.files, structLink, curStruct, true, null);
                                }
                                // Вызов обработкича параметров
                                if (structLink != null && structLink.getStrucType() == StructureType.Function && structLink != function) {
                                    ArrayList<Type> args = tokenizeFunc(lexer, function, code, true);
                                    --bracketCounter;

                                    ArrayList<Type> params = new ArrayList<>();

                                    DefinedFunction method = DefinedFunction.getFunction(files, structLink,
                                            structLink.getName(), args, params);

                                    if (method.tokens.isEmpty()) {
                                        StringBuilder c = IStructure.getCode(method);
                                        Lexer l = new Lexer(c, method, tokens);
                                        tokenizeFunc(l, method, c, false);
                                    }

                                    function.tokens.addAll(method.tokens);

                                    //mapParams(new Type(), paramsMap);
                                    structLink = new VariableOrConst(Type.getTypeWithMapping(method.getReturnValue(), paramsMap),
                                            "", lexer.getPointer() - 1, function, this.files);

                                    if (expression != null && !expressionIgnore) {
                                        expQueue.pollLast();
                                        expQueue.addLast(method.getReturnValue());
                                    }
                                } else if (prevLex != null && prevLex.getType() == LexemeType.Identifier &&
                                        !tokens.containsKey(prevLex.getContent())) {
                                    tokenizeFunc(lexer, function, code, true);
                                    --bracketCounter;
                                    if (expression != null && !expressionIgnore) {
                                        expQueue.pollLast();
                                        expQueue.addLast(new Type(TYPE_UNDEFINED));
                                    }
                                    structLink = null;
                                } else {
                                    if (expression != null && !expressionIgnore) {
                                        expQueue.addLast(op);
                                    }
                                    structLink = null;
                                }

                                isDotOp = false;
                                break;
                            case "]":
                            case ")":
                                --bracketCounter;
                                if (genericParamsLevel == -1) {
                                    if (expression != null && !expressionIgnore) {
                                        if ((isCall && bracketCounter == 0 || tryWithInit && bracketCounter == 1) &&
                                                !op.getContent().equals("]")) {
                                            expression.pushAll(expQueue);
                                            Type temp = expression.getExpressionType();
                                            if (temp != null && paramsTypes != null) {
                                                paramsTypes.add(temp);
                                            }

                                            if (tryWithInit) {
                                                expression = null;
                                                tryWithInit = false;
                                            }
                                        } else {
                                            expQueue.addLast(op);
                                        }
                                    }
                                }

                                isDotOp = false;
                                curStruct = null;
                                break;
                            case "=":
                                if (structLink != null && structLink.getStrucType() == StructureType.Variable &&
                                        ((VariableOrConst) structLink).linkToType != null) {
                                    initVar = (VariableOrConst) structLink;
                                    expression = new ExpressionTypeSolver();
                                }
                                isDotOp = false;
                                curStruct = null;
                                break;
                            case ";":
                                if (expression != null && !expressionIgnore) {
                                    expression.pushAll(expQueue);
                                    if (initVar != null) {
                                        initVar.realType = expression.getExpressionType();
                                    }

                                    expression = null;
                                    expQueue.clear();
                                    initVar = null;
                                }

                                isDotOp = false;
                                structLink = null;
                                curStruct = null;
                                break;
                            case ",":
                                if (genericParamsLevel == -1) {
                                    if (expression != null && !expressionIgnore) {
                                        expression.pushAll(expQueue);
                                        if (initVar != null) {
                                            initVar.realType = expression.getExpressionType();
                                        }
                                        if (isCall) {
                                            paramsTypes.add(expression.getExpressionType());
                                            expression = new ExpressionTypeSolver();
                                        } else {
                                            expression = null;
                                        }
                                        expQueue.clear();
                                        initVar = null;
                                    }

                                    isDotOp = false;
                                    structLink = null;
                                    curStruct = null;
                                }

                                break;
                            case "{":
                                if (genericParamsLevel == -1) {
                                    if (expression != null) {
                                        expressionIgnore = true;
                                    }

                                    isDotOp = false;
                                    structLink = null;
                                    curStruct = null;
                                }
                                break;
                            case "}":
                                if (genericParamsLevel == -1) {
                                    if (expression != null) {
                                        expressionIgnore = false;
                                    }

                                    isDotOp = false;
                                    structLink = null;
                                    curStruct = null;
                                }
                                break;
                            default:
                                // Указываем условие поскольку при использовании генерик могут попасться символы ,[]
                                // входящие в данну группу
                                if (genericParamsLevel == -1) {
                                    if (expression != null && !expressionIgnore) {
                                        expQueue.addLast(op);
                                    }

                                    isDotOp = false;
                                    structLink = null;
                                    curStruct = null;
                                }
                                break;

                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Undefined token: " + lexeme.getType());
            }
            if (bracketCounter == 0 && isCall) {
                return paramsTypes;
            }

            prevLex = lexeme;

            lexeme = lexer.getNextLexeme();
        }

        return null;
    }

    public static void setLoggerHandler(FileHandler fileHandler) {
        log.setUseParentHandlers(false);
        if (fileHandler == null) {
            return;
        }
        log.addHandler(fileHandler);
    }
}