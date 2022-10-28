package mai.student.intermediateStates;

import java.util.ArrayList;
import java.util.HashMap;

public interface IStructure {
    String getName();

    StructureType getStrucType();

    default IStructure getParent() {
        return null;
    }

    // Поиск сущности
    // Параметр isPartOfInheritanceTree за рамки поиска, если он истинен то поиск не выходит за рамки дерева населодования
    // (сам посебе предотвращает зацыкиванывания поиска в нескольких файлах со связанными классами)
    static IStructure findEntity(ArrayList<FileRepresentative> files, IStructure searchOrigin, String entityName,
                                 boolean isPartOfInheritanceTree, HashMap<String, Type> params) {
        IStructure result = null;

        if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.Function) {
            DefinedFunction func = (DefinedFunction) searchOrigin;

            for (VariableOrConst var : func.variablesAndConsts) {
                if (var.getName().equals(entityName)) {
                    return var;
                }
            }

            for (DefinedClass innerClass : func.innerClasses) {
                if (innerClass.getName().equals(entityName)) {
                    return innerClass;
                }
            }

            return findEntity(files, func.parent, entityName, false, params);
        } else if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.Class) {
            DefinedClass definedClass = (DefinedClass) searchOrigin;

            for (VariableOrConst var : definedClass.variablesAndConsts) {
                if (var.getName().equals(entityName)) {
                    return var;
                }
            }

            for (DefinedFunction func : definedClass.functions) {
                if (func.getName().equals(entityName)) {
                    return func;
                }
            }

            for (DefinedClass innerClass : definedClass.innerClasses) {
                if (innerClass.getName().equals(entityName)) {
                    return innerClass;
                }
            }

            if (!definedClass.inheritanceList.isEmpty()) {
                if (definedClass.linksToAncestors.isEmpty()) {
                    for (Type inhClass : definedClass.inheritanceList) {
                        IStructure linkToClass = findEntity(files, definedClass.parent, inhClass.getName(),
                                false, null);
                        if (linkToClass != null) {
                            definedClass.linksToAncestors.add((DefinedClass) linkToClass);
                        }
                    }
                }

                for (Type linkedClass : definedClass.inheritanceList) {
                    if (linkedClass.linkToClass == null) {
                        continue;
                    }

                    if (params != null) {
                        mapParams(linkedClass, params);
                    }

                    IStructure foundEntity = findEntity(files, linkedClass.linkToClass, entityName,
                            true, params);
                    if (foundEntity != null) {
                        return foundEntity;
                    }
                }
            }

            if (isPartOfInheritanceTree) {
                return null;
            } else {
                return findEntity(files, definedClass.parent, entityName, false, params);
            }
        } else if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) searchOrigin;

            for (DefinedClass innerClass : file.classes) {
                if (innerClass.getName().equals(entityName)) {
                    return innerClass;
                }
            }

            // check static imports
            String staticImportedPack = null;
            for (String importEntity : file.staticImports) {
                if (importEntity.endsWith("." + entityName)) {
                    staticImportedPack = importEntity;
                }
            }

            if (staticImportedPack != null) {
                for (FileRepresentative f : files) {
                    if (staticImportedPack.startsWith(f.curPackage)) {
                        for (DefinedClass innerClass : f.classes) {
                            if (staticImportedPack.contains("." + innerClass.getName() + ".")) {
                                for (VariableOrConst var : innerClass.variablesAndConsts) {
                                    if (!staticImportedPack.endsWith(var.getName())) {
                                        continue;
                                    }
                                    return var;
                                }

                                for (DefinedFunction func : innerClass.functions) {
                                    if (!staticImportedPack.endsWith(func.getName())) {
                                        continue;
                                    }
                                    return func;
                                }

                                for (DefinedClass cl : innerClass.innerClasses) {
                                    if (!staticImportedPack.endsWith(cl.getName())) {
                                        continue;
                                    }
                                    return cl;
                                }
                            }
                        }
                    }
                }
            }

            // check current package
            for (FileRepresentative f : files) {
                if (f.curPackage.equals(file.curPackage)) {
                    for (DefinedClass innerClass : f.classes) {
                        if (innerClass.getName().equals(entityName)) {
                            return innerClass;
                        }
                    }
                }
            }

            // check imported packages
            String importedPack = null;
            for (String importEntity : file.imports) {
                if (importEntity.endsWith("." + entityName)) {
                    importedPack = importEntity;
                }
            }

            if (importedPack != null) {
                for (FileRepresentative f : files) {
                    if (importedPack.startsWith(f.curPackage)) {
                        for (DefinedClass innerClass : f.classes) {
                            if (innerClass.getName().equals(entityName)) {
                                return innerClass;
                            }
                        }
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    // Функции поиска кода соответствующей структукы (в случае обработки функций из разных файлов это необходимо)
    static StringBuilder getCode(IStructure structure) {
        if (structure != null && structure.getStrucType() == StructureType.File) {
            return ((FileRepresentative) structure).code;
        } else {
            if (structure != null && structure.getStrucType() == StructureType.Class) {
                return getCode(((DefinedClass) structure).parent);
            } else {
                return getCode(((DefinedFunction) structure).parent);
            }
        }
    }

    static ArrayList<DefinedFunction> findAllFunctions(ArrayList<FileRepresentative> files, IStructure searchOrigin,
                                                       String entityName, boolean isPartOfInheritanceTree,
                                                       HashMap<String, Type> params) {

        ArrayList<DefinedFunction> result = new ArrayList<>();

        if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.Function) {
            DefinedFunction func = (DefinedFunction) searchOrigin;

            return findAllFunctions(files, func.parent, entityName, false, params);
        } else if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.Class) {
            DefinedClass definedClass = (DefinedClass) searchOrigin;

            for (DefinedFunction func : definedClass.functions) {
                if (func.getName().equals(entityName)) {
                    result.add(func);
                }
            }

            if (!definedClass.inheritanceList.isEmpty()) {
                if (definedClass.linksToAncestors.isEmpty()) {
                    for (Type inhClass : definedClass.inheritanceList) {
                        IStructure linkToClass = findEntity(files, definedClass.parent, inhClass.getName(),
                                false, null);
                        if (linkToClass != null) {
                            definedClass.linksToAncestors.add((DefinedClass) linkToClass);
                        }
                    }
                }

                for (Type linkedClass : definedClass.inheritanceList) {
                    if (linkedClass.linkToClass == null) {
                        continue;
                    }

                    if (params != null) {
                        mapParams(linkedClass, params);
                    }

                    ArrayList<DefinedFunction> temp = findAllFunctions(files, linkedClass.linkToClass, entityName,
                            true, params);
                    if (temp != null) {
                        result.addAll(temp);
                    }
                }
            }

            if (isPartOfInheritanceTree) {
                return null;
            } else {
                ArrayList<DefinedFunction> subResult = findAllFunctions(files, definedClass.parent, entityName,
                        false, params);
                if (subResult != null) {
                    result.addAll(subResult);
                }
            }
            return result;
        } else if (searchOrigin != null && searchOrigin.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) searchOrigin;

            // check static imports
            String staticImportedPack = null;
            for (String importEntity : file.staticImports) {
                if (importEntity.endsWith("." + entityName)) {
                    staticImportedPack = importEntity;
                }
            }

            if (staticImportedPack != null) {
                for (FileRepresentative f : files) {
                    if (staticImportedPack.startsWith(f.curPackage)) {
                        for (DefinedClass innerClass : f.classes) {
                            if (staticImportedPack.contains("." + innerClass.getName() + ".")) {
                                for (DefinedFunction func : innerClass.functions) {
                                    if (!staticImportedPack.endsWith(func.getName())) {
                                        continue;
                                    }
                                    result.add(func);
                                }
                            }
                        }
                    }
                }
            }

            return result.isEmpty() ? null : result;
        } else {
            return null;
        }
    }

    // TODO: transfer HashMap<String, Type> to HashMap<Type, Type>
    public static void mapParams(Type type, HashMap<String, Type> params) {
        if (type.linkToClass == null) {
            return;
        }

        Type[] paramList = type.linkToClass.getParams();
        for (int i = 0; paramList != null && i < paramList.length; ++i) {
            if (params.containsKey(type.getParams().get(i).getName())) {
                continue;
            }

            params.put(paramList[i].toString(), type.getParams().get(i));
        }
    }
}