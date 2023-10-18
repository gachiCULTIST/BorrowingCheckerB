package mai.student.utility;

import mai.student.intermediateStates.*;
import mai.student.tokenizers.CodeLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilityClass {

    private static final String cExtension = ".c";
    private static final String cppExtension = ".cpp";
    private static final String javaExtension = ".java";
    private static final String pythonExtension = ".py";

    public static CodeLanguage getLanguage(Path source) {
        if (Files.isDirectory(source)) {
            try (Stream<Path> insides = Files.list(source)) {
                for (Path path : insides.collect(Collectors.toList())) {
                    CodeLanguage result = getLanguage(path);
                    if (result == null) {
                        continue;
                    }
                    return result;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Wrong source path: " + source, e);
            }
        } else {
            if (source.toString().endsWith(javaExtension)) {
                return CodeLanguage.Java;
            }

            if (source.toString().endsWith(cExtension) || source.toString().endsWith(cppExtension)) {
                return CodeLanguage.C;
            }

            if (source.toString().endsWith(pythonExtension)) {
                return CodeLanguage.Python;
            }
        }

        return null;
    }

    // Функция для проверка анализа программы
    public static void printInsideStructure(IStructure analysedStructure, int level) {
        if (analysedStructure != null && analysedStructure.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) analysedStructure;

            System.out.println(file.curPackage);
            System.out.println("Imports:");
            for (Import importEntity : file.imports) {
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