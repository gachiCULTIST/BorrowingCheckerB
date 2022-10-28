package mai.student;

import mai.student.intermediateStates.*;
import mai.student.tokenizers.CodeLanguage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityClass {

    public static CodeLanguage getLanguage(String file) {
        String extension = file.substring(file.lastIndexOf('.') + 1);

        switch (extension) {
            case "java":
                return CodeLanguage.Java;
            case "c":
            case "cpp":
                return CodeLanguage.C;
            case "py":
                return CodeLanguage.Python;
        }

        throw new UnsupportedOperationException("Not supported file type: " + extension);
    }

    // Поиск индекса закрывающей скобки (вид которой устанавливается в соответствующем поле)
    public static int findLastFigure(StringBuilder code, int start, String openChar, String closeChar) {
        int end = start - 1;
        int openedFig = 1;
        do {
            end = code.indexOf(closeChar, end + 1);
            while (end != -1 && end > 0 && code.charAt(end - 1) == '\'' && end < code.length() - 1 &&
                    code.charAt(end + 1) == '\'') {
                end = code.indexOf(closeChar, end + 1);
            }
            if (end == -1) {
                throw new RuntimeException("Can't find closing char: " + closeChar);
            }

            while (true) {
                start = code.indexOf(openChar, start + 1);
                while (start != -1 && start > 0 && code.charAt(start - 1) == '\'' && start < code.length() - 1 &&
                        code.charAt(start + 1) == '\'') {
                    start = code.indexOf(openChar, start + 1);
                }
                if (start >= end || start == -1) {
                    break;
                }
                ++openedFig;
            }

            start = end;
            --openedFig;
        } while (openedFig != 0);
        return end;
    }

    // Функция для представления строки в виде массива из значений указанной группы указанного регулярного выражения
    public static ArrayList<String> stringToList(String str, String regex, int groupNum) {
        ArrayList<String> result = new ArrayList<>();

        if (str == null) {
            return result;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            result.add(matcher.group(groupNum));
        }
        return result;
    }

    public static ArrayList<Integer> countSquareBrackets(String str, String regex, int[] bracketGroups) {
        ArrayList<Integer> result = new ArrayList<>();

        if (str == null) {
            return result;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            int counter = 0;

            for (int brackets : bracketGroups) {
                if (matcher.group(brackets) == null) {
                    continue;
                }
                String bracketGroup = matcher.group(brackets);

                int pointer = -1;
                while (pointer < bracketGroup.length()) {
                    pointer = bracketGroup.indexOf('[', pointer + 1);
                    if (pointer == -1) {
                        break;
                    }
                    ++counter;
                }
            }

            result.add(counter);
        }
        return result;
    }

    // Функция для проверка анализа программы
    public static void printInsideStructure(IStructure analysedStructure, int level) {
        if (analysedStructure != null && analysedStructure.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) analysedStructure;

            System.out.println(file.curPackage);
            System.out.println("Imports:");
            for (String importEntity : file.imports) {
                System.out.println(importEntity);
            }

            for (DefinedClass tempClass : file.classes) {
                printInsideStructure(tempClass, 1);
            }
        } else if (analysedStructure != null && analysedStructure.getStrucType() == StructureType.Class) {
            DefinedClass defClass = (DefinedClass) analysedStructure;

            System.out.println("\t".repeat(level) + defClass.getName() + " | parent:" + defClass.getParent().getName());

            for (VariableOrConst var : defClass.variablesAndConsts) {
                System.out.println("\t".repeat(level + 1) + var.getType() + " " + var.getName() + " " +
                        var.getPosition().toString() + " | parent: " + var.getParent().getName());
            }

            for (DefinedFunction func : defClass.functions) {
                printInsideStructure(func, level + 1);
            }

            for (DefinedClass tempClass : defClass.innerClasses) {
                printInsideStructure(tempClass, level + 1);
            }
        } else {
            DefinedFunction defFunc = (DefinedFunction) analysedStructure;

            ArrayList<String> funcArgTypes = new ArrayList<>();
            for (Type type : defFunc.getArgTypes()) {
                funcArgTypes.add(type.toString());
            }
            System.out.println("\t".repeat(level) + defFunc.getReturnValue() + " " + defFunc.getName() + " (" +
                    String.join(", ", funcArgTypes) + ") | parent: " + defFunc.getParent().getName());

            for (VariableOrConst var : defFunc.variablesAndConsts) {
                System.out.println("\t".repeat(level + 1) + var.getType() + " " + var.getName() + " " +
                        var.getPosition().toString() + " | parent: " + var.getParent().getName());
            }

            for (DefinedClass tempClass : defFunc.innerClasses) {
                printInsideStructure(tempClass, level + 1);
            }
        }
    }
}