package mai.student.tokenizers.java17.preprocessing;

import mai.student.UtilityClass;
import mai.student.intermediateStates.FileRepresentative;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cleaner {

    private static final String STRING_SUBSTITUTE = " *str* "; // токнизатор считывает операторы до пробела (поэтому тут стоят пробелы)
    private static final int ANNOTATION_USAGE_PARAMS = 4;
    private static final int ANNOTATION_DECLARATION = 1;
    private static final String UNDEFINED_PACKAGE = "undefined";
    private static final String REGEX_IMPORT = "import\\s+(static\\s+)?([\\w.*]+)\\s*;";
    private static final int IMP_STATIC = 1;
    private static final int IMP_PACKAGE = 2;

    private FileRepresentative file;

    Cleaner(FileRepresentative file) {
        this.file = file;
    }

    StringBuilder clean() {
        StringBuilder preresult = new StringBuilder();
        StringBuilder result = new StringBuilder();

        ArrayList<ErasingUnit> stringCommEdges = this.commAndStringDetection();

        // Удаление комментариев и замена строк
        int copyStart = 0;
        for (int i = 0; i < stringCommEdges.size(); i += 2) {
            preresult.append(file.code.substring(copyStart, stringCommEdges.get(i).index));

            if (stringCommEdges.get(i).lexeme.equals("\"")) {
                preresult.append(STRING_SUBSTITUTE);
            }

            copyStart = stringCommEdges.get(i + 1).index + 1;
        }

        if (copyStart < file.code.length()) {
            preresult.append(file.code.substring(copyStart));
        }

        ArrayList<Integer> otherEdges = annotDetection(preresult);
        otherEdges.addAll(packAndImpDetection(preresult));
        Collections.sort(otherEdges);

        copyStart = 0;
        for (int i = 0; i < otherEdges.size(); i += 2) {
            result.append(preresult.substring(copyStart, otherEdges.get(i)));

            copyStart = otherEdges.get(i + 1);
        }

        if (copyStart < preresult.length()) {
            result.append(preresult.substring(copyStart));
        }

        return result;
    }

    private ArrayList<ErasingUnit> commAndStringDetection() {


        // Обнаружение всех символов начала и конца комментариев и строк
        ArrayList<ErasingUnit> stringCommEdges = new ArrayList<>();

        // Поиск всех многострочных комментариев
        int startMulLineComm = 0;
        int endMulLineComm = 0;

        do {
            startMulLineComm = file.code.indexOf("/*", startMulLineComm + 2);
            if (startMulLineComm == -1) {
                break;
            }
            stringCommEdges.add(new ErasingUnit(startMulLineComm, "/*"));
        } while (true);

        do {
            endMulLineComm = file.code.indexOf("*/", endMulLineComm + 2);
            if (endMulLineComm == -1) {
                break;
            }
            stringCommEdges.add(new ErasingUnit(endMulLineComm + 1, "*/"));
        } while (true);

        // Поиск всех однострочных комментариев
        int startSinLineComm = 0;
        int endSinLineComm = 0;

        do {
            startSinLineComm = file.code.indexOf("//", startSinLineComm);
            if (startSinLineComm == -1) {
                break;
            }
            if (startSinLineComm != 0 && file.code.charAt(startSinLineComm - 1) == '*') {
                ++startSinLineComm;
                continue;
            }
            endSinLineComm = file.code.indexOf("\n", startSinLineComm + 2);
            if (endSinLineComm == -1) {
                endSinLineComm = file.code.length() - 1;
            }

            stringCommEdges.add(new ErasingUnit(startSinLineComm, "//"));
            stringCommEdges.add(new ErasingUnit(endSinLineComm, "\n"));
            startSinLineComm += 2;
        } while (true);


        // Поиск всех строк
        int quote = 0;
        Pattern pattern = Pattern.compile("(\\\\{2})*\"");
        Matcher matcher = pattern.matcher(file.code);

        while (matcher.find()) {
            if (file.code.charAt(matcher.start() - 1) == '\\') {
                continue;
            }
            stringCommEdges.add(new ErasingUnit(matcher.end() - 1, "\""));
        }

        //Составление списка значимых и незначимых элементов
        HashSet<Integer> insignSymbols = new HashSet<>();
        Collections.sort(stringCommEdges);

        // Поиск незначимых символов
        boolean quoteStarted = false;
        boolean mulStarted = false;
        boolean sinStarted = false;
        for (ErasingUnit i : stringCommEdges) {
            switch (i.lexeme) {
                case "\"":
                    if (mulStarted || sinStarted) {
                        insignSymbols.add(i.index);
                    } else {
                        if (quoteStarted) {
                            quoteStarted = false;
                        } else {
                            quoteStarted = true;
                        }
                    }
                    break;
                case "/*":
                    if (sinStarted || mulStarted || quoteStarted) {
                        insignSymbols.add(i.index);
                    } else {
                        mulStarted = true;
                    }
                    break;
                case "*/":
                    if (sinStarted || !mulStarted || quoteStarted) {
                        insignSymbols.add(i.index);
                    } else {
                        mulStarted = false;
                    }
                    break;
                case "//":
                    if (sinStarted || mulStarted || quoteStarted) {
                        insignSymbols.add(i.index);
                    } else {
                        sinStarted = true;
                    }
                    break;
                case "\n":
                    if (!sinStarted || mulStarted || quoteStarted) {
                        insignSymbols.add(i.index);
                    } else {
                        sinStarted = false;
                    }
                    break;
            }
        }
        for (int i = 0; i < stringCommEdges.size(); ++i) {
            if (insignSymbols.contains(stringCommEdges.get(i).index)) {
                stringCommEdges.remove(i);
                --i;
            }
        }

        return stringCommEdges;
    }

    // Обнаружение аннотаций
    private ArrayList<Integer> annotDetection(StringBuilder code) {
        Pattern pattern = Pattern.compile("((\\w+\\s+)?@interface\\s+[\\wа-яА-Я]+\\s*\\{)|(@\\w+(\\s*\\()?)");
        Matcher matcher = pattern.matcher(code);
        ArrayList<Integer> annotEdges = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(ANNOTATION_DECLARATION) != null) {
                annotEdges.add(matcher.start());
                annotEdges.add(UtilityClass.findLastFigure(code, matcher.end(), "{", "}") + 1);
            } else {
                annotEdges.add(matcher.start());

                if (matcher.group(ANNOTATION_USAGE_PARAMS) != null) {
                    annotEdges.add(UtilityClass.findLastFigure(code, matcher.end(), "(", ")") + 1);
                } else {
                    annotEdges.add(matcher.end());
                }
            }
        }

        return annotEdges;
    }

    // Обнаружение и запись списка импортов и указанного пакета
    private ArrayList<Integer> packAndImpDetection(StringBuilder code) {
        Pattern packPattern = Pattern.compile("package\\s+([\\w.]*)\\s*;");
        Matcher packMatcher = packPattern.matcher(code);
        ArrayList<Integer> packImpEdges = new ArrayList<>();

        if (packMatcher.find()) {
            file.curPackage = packMatcher.group(1);

            packImpEdges.add(packMatcher.start());
            packImpEdges.add(packMatcher.end());
        } else {
            file.curPackage = UNDEFINED_PACKAGE;
        }

        Pattern impPattern = Pattern.compile(REGEX_IMPORT);
        Matcher impMatcher = impPattern.matcher(code);
        int impStart = -1;
        int impEnd = 0;
        while (impMatcher.find()) {
            if (impStart == -1) {
                impStart = impMatcher.start();
            }
            impEnd = impMatcher.end();
            if (impMatcher.group(IMP_STATIC) == null) {
                file.imports.add(impMatcher.group(IMP_PACKAGE));
            } else {
                file.staticImports.add(impMatcher.group(IMP_PACKAGE));
            }
        }
        if (impStart != -1) {
            packImpEdges.add(impStart);
            packImpEdges.add(impEnd);
        }

        return packImpEdges;
    }
}