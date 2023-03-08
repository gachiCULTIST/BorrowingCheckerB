package mai.student.utility;

import mai.student.intermediateStates.*;
import java.util.List;


public class EntitySearchers {

    // For all classes
    public static DefinedClass findClass(List<FileRepresentative> files, IStructure searchOrigin, String target) {
        return findClass(files, searchOrigin, new Type(target));
    }

    // For all classes
    public static DefinedClass findClass(List<FileRepresentative> files, IStructure searchOrigin, Type target) {
        if (files == null || target == null) {
            return null;
        }

        // Qualified
        if (target.getQualifier().isPresent()) {
            return findQualifiedClass(files, target);
        }

        // Non-qualified
        if (searchOrigin == null) {
            return null;
        }
        return findNonQualifiedClass(files, searchOrigin, target);
    }

    // For qualified class
    public static DefinedClass findQualifiedClass(List<FileRepresentative> files, Type target) {
        if (files == null || target == null || target.getQualifier().isEmpty()) {
            return null;
        }

        Qualifier qualifier = target.getQualifier().get();

        for (FileRepresentative file : files) {
            if (!qualifier.startsWith(file.curPackage)) {
                continue;
            }

            // For non nested class
            if (qualifier.equals(file.curPackage)) {
                for (DefinedClass innerClass : file.classes) {
                    if (innerClass.getName().equals(target.getName())) {
                        return innerClass;
                    }
                }

                continue;
            }


            // For nested classes
            DefinedClass result = null;
            int i = file.curPackage.getLength();

            // Get first entity
            while (i < qualifier.getLength()) {
                for (DefinedClass innerClass : file.classes) {
                    if (innerClass.getName().equals(qualifier.getContent()[i])) {
                        result = innerClass;
                        ++i;
                        break;
                    }
                }
            }
            if (result == null) {
                continue;
            }

            // Moving pointer to the end
            for (; i < qualifier.getLength(); ++i) {
                boolean changed = false; // observe result shifting

                for (DefinedClass innerClass : result.innerClasses) {
                    if (innerClass.getName().equals(qualifier.getContent()[i])) {
                        result = innerClass;
                        changed = true;
                        break;
                    }
                }

                if (!changed) {
                    result = null;
                    break;
                }
            }
            if (result == null) {
                continue;
            }

            // Get result from qualifier
            for (DefinedClass innerClass : result.innerClasses) {
                if (innerClass.getName().equals(target.getName())) {
                    return innerClass;
                }
            }
        }

        return null;
    }

    // For non-qualified class
    public static DefinedClass findNonQualifiedClass(List<FileRepresentative> files, IStructure searchOrigin,
                                                     Type target) {

        if (files == null || searchOrigin == null || target == null) {
            return null;
        }

        // Scope - function
        if (searchOrigin.getStrucType() == StructureType.Function) {
            DefinedFunction func = (DefinedFunction) searchOrigin;

            for (DefinedClass innerClass : func.innerClasses) {
                if (innerClass.getName().equals(target.getName())) {
                    return innerClass;
                }
            }

            return findNonQualifiedClass(files, func.getParent(), target);
        }

        // Scope - class
        if (searchOrigin.getStrucType() == StructureType.Class) {
            DefinedClass definedClass = (DefinedClass) searchOrigin;

            for (DefinedClass innerClass : definedClass.innerClasses) {
                if (innerClass.getName().equals(target.getName())) {
                    return innerClass;
                }
            }

            return findNonQualifiedClass(files, definedClass.getParent(), target);
        }

        // Scope - file
        if (searchOrigin.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) searchOrigin;

            for (DefinedClass innerClass : file.classes) {
                if (innerClass.getName().equals(target.getName())) {
                    return innerClass;
                }
            }

            // From other files
            DefinedClass result;

            //  Check static imports
            for (Import staticImport : file.staticImports) {
                if (staticImport.isOverall() || staticImport.endsWith(target.getName())) {
                    result = findQualifiedClass(files, new Type(target.getName(), null, staticImport.toQualifier()));

                    if (result != null) {
                        return result;
                    }
                }
            }

            //  Check non-static imports
            for (Import nonStaticImport : file.imports) {
                if (nonStaticImport.isOverall() || nonStaticImport.endsWith(target.getName())) {
                    result = findQualifiedClass(files, new Type(target.getName(), null, nonStaticImport.toQualifier()));

                    if (result != null) {
                        return result;
                    }
                }
            }

            // Check current package
            for (FileRepresentative f : files) {
                if (f.curPackage != null && !f.equals(file) && f.curPackage.equals(file.curPackage)) {
                    for (DefinedClass innerClass : f.classes) {
                        if (innerClass.getName().equals(target.getName())) {
                            return innerClass;
                        }
                    }
                }
            }

            return null;
        }

        return null;
    }

    // For all vars and constants
    // Параметр isPartOfInheritanceTree за рамки поиска, если он истинен то поиск не выходит за рамки дерева наследования
    // (сам по себе предотвращает зацикливания поиска в нескольких файлах со связанными классами)
    public static VariableOrConst findVariable(List<FileRepresentative> files, IStructure searchOrigin, String target,
                                               boolean isPartOfInheritanceTree) {

        if (files == null || searchOrigin == null || target == null) {
            return null;
        }


        // Scope - function
        if (searchOrigin.getStrucType() == StructureType.Function) {
            DefinedFunction func = (DefinedFunction) searchOrigin;

            for (VariableOrConst var : func.variablesAndConsts) {
                if (var.getName().equals(target)) {
                    return var;
                }
            }

            return findVariable(files, func.parent, target, false);
        }

        // Scope - class
        if (searchOrigin.getStrucType() == StructureType.Class) {
            DefinedClass definedClass = (DefinedClass) searchOrigin;

            for (VariableOrConst var : definedClass.variablesAndConsts) {
                if (var.getName().equals(target)) {
                    return var;
                }
            }

            // Get vars from inheritance tree
            if (!definedClass.inheritanceList.isEmpty()) {
                for (Type linkedClass : definedClass.inheritanceList) {
                    if (linkedClass.getLinkToClass() == null) {
                        continue;
                    }

                    VariableOrConst var = findVariable(files, linkedClass.getLinkToClass(), target, true);
                    if (var != null) {
                        return var;
                    }
                }
            }

            // Walking up over inheritance tree, we aren't step away for not getting looped.
            if (isPartOfInheritanceTree) {
                return null;
            }

            return findVariable(files, definedClass.getParent(), target, false);
        }

        // Scope - file
        if (searchOrigin.getStrucType() == StructureType.File) {
            FileRepresentative file = (FileRepresentative) searchOrigin;

            // From other files
            VariableOrConst result;

            //  Check static imports
            for (Import staticImport : file.staticImports) {
                if (staticImport.isOverall() || staticImport.endsWith(target)) {
                    result = findQualifiedVariable(files, target, staticImport.toQualifier());

                    if (result != null) {
                        System.out.println(result.getParent().getName());
                        return result;
                    }
                }
            }

            return null;
        }

        return null;
    }

    // For static imported vars and constants
    public static VariableOrConst findQualifiedVariable(List<FileRepresentative> files, String target, Qualifier qualifier) {
        if (files == null || target == null || qualifier == null) {
            return null;
        }


        for (FileRepresentative file : files) {
            if (!qualifier.startsWith(file.curPackage)) {
                continue;
            }

            DefinedClass result = null;
            int i = file.curPackage.getLength();

            // Get first entity
            while (i < qualifier.getLength()) {
                for (DefinedClass innerClass : file.classes) {
                    if (innerClass.getName().equals(qualifier.getContent()[i])) {
                        result = innerClass;
                        ++i;
                        break;
                    }
                }
            }
            if (result == null) {
                continue;
            }

            // Moving pointer to the end
            for (; i < qualifier.getLength(); ++i) {
                boolean changed = false; // observe result shifting

                for (DefinedClass innerClass : result.innerClasses) {
                    if (innerClass.getName().equals(qualifier.getContent()[i])) {
                        result = innerClass;
                        changed = true;
                        break;
                    }
                }

                if (!changed) {
                    result = null;
                    break;
                }
            }
            if (result == null) {
                continue;
            }

            // Get result from qualifier
            for (VariableOrConst var : result.variablesAndConsts) {
                if (var.getName().equals(target)) {
                    return var;
                }
            }
        }

        return null;
    }

}
