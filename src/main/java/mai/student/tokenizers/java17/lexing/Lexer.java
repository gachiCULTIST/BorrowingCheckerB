package mai.student.tokenizers.java17.lexing;

import mai.student.intermediateStates.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    // Идентификатор лексемы в регулярке
    private static final int LEXEME_IDENTIFIER = 3;
    private static final int LEXEME_INTEGER = 14;
    private static final int LEXEME_LONG = 17;
    private static final int LEXEME_DOUBLE = 4;
    private static final int LEXEME_FLOAT = 13;
    private static final int LEXEME_CHAR = 19;
    private static final int LEXEME_BOOL = 2;
    private static final int LEXEME_STRSUB = 18;
    private static final int LEXEME_OPERATOR = 23;

    private StringBuilder code;
    private DefinedFunction function;
    private HashMap<String, Integer> tokens;
    private LinkedList<Lexeme> stash = new LinkedList<>();

    private int pointer = -1;
    private Pattern sentencePattern;
    private Matcher sentenceMatcher;
    private Pattern lexemePattern;

    public int getPointer() {
        return pointer;
    }

    public Lexer(StringBuilder code, DefinedFunction function, HashMap<String, Integer> tokens) {
        this.code = code;
        this.function = function;
        this.tokens = tokens;

        pointer = function.getBlockStart();

        sentencePattern = Pattern.compile("([^;{}]*)([;{}])");
        sentenceMatcher = sentencePattern.matcher(code);

        // При обработки логических литералов захватывает идущий за ними символ (учитывается в указателе)
        lexemePattern = Pattern.compile("((true|false)[^\\w])|([\\w$&&[^\\d]]{1}[\\w$]*)|((((([\\d_]*\\.[\\d_]+)" +
                "|([\\d_]+\\.[\\d_]*)|([\\d_]+))[eE][+-][\\d_]+)|([\\d_]*\\.[\\d_]+)|([\\d_]+\\.[\\d_]*))([fF])?)" +
                "|((0x[\\d_a-fA-F]+|(0b)?[\\d_]+)([lL])?)|(\\*str\\*)|('[\\w\\W&&[^']]*(([\\\\]{2})" +
                "|([\\w\\W&&[^\\\\]]))')|([\\W&&[^\\s']]+)");
    }

    public Lexeme getNextLexeme() {
        if (stash.isEmpty()) {
            if (extractLexeme()) {
                return getNextLexeme();
            }

            return new Lexeme(null, LexemeType.EoF);
        } else {
            return stash.pollFirst();
        }
    }

    public Lexeme peekNextLexeme() {
        if (stash.isEmpty()) {
            if (extractLexeme()) {
                return peekNextLexeme();
            }

            return new Lexeme(null, LexemeType.EoF);
        } else {
            return stash.peekFirst();
        }
    }

    // Обрабатываем по выражению
    private boolean extractLexeme() {
        if (sentenceMatcher.find(pointer)) {

            if (sentenceMatcher.end() < function.getEndIndex()) {
                Matcher lexemeMatcher = lexemePattern.matcher(sentenceMatcher.group(0));

                int innerPointer = 0;

                while (lexemeMatcher.find(innerPointer)) {
                    // Определение типа лексемы и разделение по группам на основе схожести обработки
                    if (lexemeMatcher.group(LEXEME_IDENTIFIER) != null) {
                        stash.addLast(new Lexeme(lexemeMatcher.group(LEXEME_IDENTIFIER), LexemeType.Identifier));
                        innerPointer = lexemeMatcher.end();
                    } else if (lexemeMatcher.group(LEXEME_INTEGER) != null){
                        if (lexemeMatcher.group(LEXEME_LONG) != null) {
                            stash.addLast(new Lexeme(null, LexemeType.Long));
                        } else {
                            stash.addLast(new Lexeme(null, LexemeType.Integer));
                        }
                        innerPointer = lexemeMatcher.end();
                    } else if (lexemeMatcher.group(LEXEME_DOUBLE) != null) {
                        if (lexemeMatcher.group(LEXEME_FLOAT) != null) {
                            stash.addLast(new Lexeme(null, LexemeType.Float));
                        } else {
                            stash.addLast(new Lexeme(null, LexemeType.Double));
                        }
                        innerPointer = lexemeMatcher.end();
                    } else if (lexemeMatcher.group(LEXEME_CHAR) != null) {
                        stash.addLast(new Lexeme(null, LexemeType.Char));
                        innerPointer = lexemeMatcher.end();
                    } else if (lexemeMatcher.group(LEXEME_STRSUB) != null) {
                        stash.addLast(new Lexeme(null, LexemeType.String));
                        innerPointer = lexemeMatcher.end();
                    } else if (lexemeMatcher.group(LEXEME_BOOL) != null) {
                        stash.addLast(new Lexeme(null, LexemeType.Bool));
                        innerPointer = lexemeMatcher.end() - 1;
                    } else if (lexemeMatcher.group(LEXEME_OPERATOR) != null) {
                        String operator = lexemeMatcher.group(LEXEME_OPERATOR);
                        ArrayList<String> operators = separateOperators(operator);

                        for (String op : operators) {
                            stash.addLast(new Lexeme(op, LexemeType.Operator));
                        }
                        innerPointer = lexemeMatcher.end();
                    }
                }
                //stash.addLast(new Lexeme(sentenceMatcher.group(2), LexemeType.Operator));
            }

            pointer = sentenceMatcher.end();
        } else {
            return false;
        }

        return true;
    }

    // Поскольку операторы могут состоять из нескольких символов считываем сразу последовательноть операторов и потом
    // отделяем их друг от друга при помощи этой функции
    private ArrayList<String> separateOperators(String op) {
        ArrayList<String> result = new ArrayList<>();
        int length = op.length();

        boolean notSeparated = true;
        do {
            if (tokens.containsKey(op.substring(0, length))) {
                notSeparated = false;
            } else {
                --length;
            }
        } while (notSeparated);

        result.add(op.substring(0, length));
        if (length != op.length()) {
            result.addAll(separateOperators(op.substring(length)));
        }

        return result;
    }
}
