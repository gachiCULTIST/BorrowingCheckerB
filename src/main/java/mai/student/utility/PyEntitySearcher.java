package mai.student.utility;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.python.*;
import mai.student.tokenizers.python3.preprocessing.PySpecificFunction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PyEntitySearcher {

    public static List<PyFuncRepresentative> findMethodsAnywhere(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean notInit) {
        if (files == null || name == null) {
            return null;
        }

        List<PyFuncRepresentative> result = new ArrayList<>();
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                file.getFunctions().forEach(f -> {
                    if (f.getName().equals(name)) {
                        result.add(f);
                    }
                });

                file.getClasses().forEach(c -> {
                    if (c.getName().equals(name)) {
                        result.addAll(c.getFunctions().stream()
                                .filter(f -> f.getName().equals(PySpecificFunction.INIT.getName())).collect(Collectors.toList()));
                    }

                    result.addAll(findMethodsAnywhere(files, name, c, false));
                });

                if (!notInit) {
                    file.getImports().forEach(i -> {
                        Path root = file.getPath().getParent();
                        if (root != null) {
                            Path otherFile = root.resolve(i.getModule());
                            files.forEach(f -> {
                                if (f.getPath().equals(otherFile)) {
                                    result.addAll(findMethodsAnywhere(files, name, f, true));
                                }
                            });
                        }
                    });
                }
                break;
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
                if (!classs.isLinked()) {
                    classs.actuateTypes(files);
                }

                classs.getFunctions().forEach(f -> {
                    if (f.getName().equals(name)) {
                        result.add(f);
                    }
                });

                classs.getClasses().forEach(c -> {
                    if (c.getName().equals(name)) {
                        PyFuncRepresentative init = c.getFunctions().stream()
                                .filter(f -> f.getName().equals(PySpecificFunction.INIT.getName())).findFirst().orElse(null);
                        if (init != null) {
                            result.add(init);
                        }
                    }

                    result.addAll(findMethodsAnywhere(files, name, c, false));
                });
                break;
            case Function:
                PyFuncRepresentative func = (PyFuncRepresentative) start;
                if (!func.isLinked()) {
                    func.actuateTypes(files);
                }

                func.getFunctions().forEach(f -> {
                    if (f.getName().equals(name)) {
                        result.add(f);
                    }
                });

                func.getClasses().forEach(c -> {
                    if (c.getName().equals(name)) {
                        PyFuncRepresentative init = c.getFunctions().stream()
                                .filter(f -> f.getName().equals(PySpecificFunction.INIT.getName())).findFirst().orElse(null);
                        if (init != null) {
                            result.add(init);
                        }
                    }

                    result.addAll(findMethodsAnywhere(files, name, c, false));
                });
                break;
        }

        return result;
    }

    public static List<PyFuncRepresentative> findMethod(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean justInheritance) {
        if (files == null || name == null || start == null) {
            return new ArrayList<>();
        }

        List<PyFuncRepresentative> result = new ArrayList<>();
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyFuncRepresentative f : file.getFunctions()) {
                    if (f.getName().equals(name)) {
                        result.add(f);
                    }
                }

                // Находим конструкторы
                for (PyClassRepresentation c : file.getClasses()) {
                    if (c.getName().equals(name)) {
                        result.addAll(findMethod(files, PySpecificFunction.INIT.getName(), c, true));
                    }
                }

                Path root = file.getPath().getParent();
                if (root != null) {
                    // Для статического импорта
                    file.getImports().stream().filter(PyImport::isStatic).forEach(i -> {
                        if (i.getAlias() != null && i.getAlias().equals(name)) {
                            Path otherFile = root.resolve(i.getModule());
                            files.forEach(f -> {
                                if (f.getPath().equals(otherFile)) {
                                    result.addAll(findMethod(files, i.getEntity(), f, false));
                                }
                            });
                        } else if (i.getEntity().equals(name)) {
                            Path otherFile = root.resolve(i.getModule());
                            files.forEach(f -> {
                                if (f.getPath().equals(otherFile)) {
                                    result.addAll(findMethod(files, name, f, false));
                                }
                            });
                        }
                    });
                }
                break;
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
                List<PyFuncRepresentative> semiResult = new ArrayList<>();
                if (!classs.isLinked()) {
                    classs.actuateTypes(files);
                }

                for (PyFuncRepresentative f : classs.getFunctions()) {
                    if (f.getName().equals(name)) {
                        semiResult.add(f);
                    }
                }

                if (semiResult.isEmpty()) {
                    for (PyType supers : classs.getInheritanceList()) {
                        PyClassRepresentation sc = supers.getLinkToClass();
                        if (sc != null) {
                            result.addAll(findMethod(files, name, sc, true));
                        }
                    }
                }

                result.addAll(semiResult);
                if (!justInheritance) {
                    result.addAll(findMethod(files, name, classs.getParent(), false));
                }
                break;
            case Function:
                PyFuncRepresentative func = (PyFuncRepresentative) start;
                if (!func.isLinked()) {
                    func.actuateTypes(files);
                }

                for (PyFuncRepresentative f : func.getFunctions()) {
                    if (f.getName().equals(name)) {
                        result.add(f);
                    }
                }

                result.addAll(findMethod(files, name, func.getParent(), false));
                break;
            case Variable:
                PyVariableRepresentative var = (PyVariableRepresentative) start;
                if (!var.isLinked()) {
                    var.actuateTypes(files);
                }

                if (var.getRealType() != null && var.getRealType().getLinkToClass() != null) {
                    result.addAll(findMethod(files, name, var.getRealType().getLinkToClass(), false));
                    break;
                }

                if (var.getType() != null && var.getType().getLinkToClass() != null) {
                    result.addAll(findMethod(files, name, var.getType().getLinkToClass(), false));
                    break;
                }
                break;
        }

        return result;
    }

    public static PyClassRepresentation findClass(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start) {
        if (files == null || name == null) {
            return null;
        }

        PyClassRepresentation result;
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;

                for (PyClassRepresentation c : file.getClasses()) {
                    if (c.getName().equals(name)) {
                        return c;
                    }
                }
                break;
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
                if (!classs.isLinked()) {
                    classs.actuateTypes(files);
                }

                for (PyClassRepresentation c : classs.getClasses()) {
                    if (c.getName().equals(name)) {
                        return c;
                    }
                }

                result = findClass(files, name, classs.getParent());
                if (result != null) {
                    return result;
                }
                break;
            case Function:
                PyFuncRepresentative func = (PyFuncRepresentative) start;
                if (!func.isLinked()) {
                    func.actuateTypes(files);
                }

                for (PyClassRepresentation c : func.getClasses()) {
                    if (c.getName().equals(name)) {
                        return c;
                    }
                }

                result = findClass(files, name, func.getParent());
                if (result != null) {
                    return result;
                }
                break;
            case Variable:
                PyVariableRepresentative var = (PyVariableRepresentative) start;
                if (!var.isLinked()) {
                    var.actuateTypes(files);
                }

                if (var.getRealType() != null && var.getRealType().getLinkToClass() != null) {
                    return findClass(files, name, var.getRealType().getLinkToClass());
                }

                if (var.getType() != null && var.getType().getLinkToClass() != null) {
                    return findClass(files, name, var.getType().getLinkToClass());
                }
                break;
        }

        return null;
    }

    public static PyVariableRepresentative findVariable(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean justInheritance) {
        if (files == null || name == null) {
            return null;
        }

        PyVariableRepresentative result;
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyVariableRepresentative v : file.getVariables()) {
                    if (v.getName().equals(name)) {
                        return v;
                    }
                }
                break;
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
                if (!classs.isLinked()) {
                    classs.actuateTypes(files);
                }

                for (PyVariableRepresentative v : classs.getVariables()) {
                    if (v.getName().equals(name)) {
                        return v;
                    }
                }

                for (PyType supers : classs.getInheritanceList()) {
                    PyClassRepresentation sc = findClass(files, supers.getName(), classs);
                    if (sc != null) {
                        result = findVariable(files, name, sc, true);
                        if (result != null) {
                            return result;
                        }
                    }
                }

                if (!justInheritance) {
                    result = findVariable(files, name, classs.getParent(), false);
                    if (result != null) {
                        return result;
                    }
                }
                break;
            case Function:
                PyFuncRepresentative func = (PyFuncRepresentative) start;
                if (!func.isLinked()) {
                    func.actuateTypes(files);
                }

                for (PyVariableRepresentative v : func.getVariables()) {
                    if (v.getName().equals(name)) {
                        return v;
                    }
                }

                result = findVariable(files, name, func.getParent(), false);
                if (result != null) {
                    return result;
                }
                break;
            case Variable:
                PyVariableRepresentative var = (PyVariableRepresentative) start;
                if (!var.isLinked()) {
                    var.actuateTypes(files);
                }

                if (var.getRealType() != null && var.getRealType().getLinkToClass() != null) {
                    return findVariable(files, name, var.getRealType().getLinkToClass(), false);
                }

                if (var.getType() != null && var.getType().getLinkToClass() != null) {
                    return findVariable(files, name, var.getType().getLinkToClass(), false);
                }
                break;
        }

        return null;
    }

    public static PyFileRepresentative findModuleByAlias(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start) {
        if (files == null || name == null) {
            return null;
        }

        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyImport i : file.getImports()) {
                    if (i.getAlias() != null && i.getAlias().equals(name)) {
                        Path root = file.getPath().getParent();
                        if (root != null) {
                            Path otherFile = root.resolve(i.getModule());
                            for (PyFileRepresentative f : files) {
                                if (f.getPath().equals(otherFile)) {
                                    return f;
                                }
                            }
                        }
                    }
                }
                break;
            case Class:
            case Function:
                return findModuleByAlias(files, name, start.getParent());
        }

        return null;
    }

    public static PyFileRepresentative findModuleByFullPath(List<PyFileRepresentative> files, Path path, IStructure<PyFileRepresentative> start) {
        if (files == null || path == null) {
            return null;
        }

        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyImport i : file.getImports()) {
                    if (i.getModule().equals(path)) {
                        Path root = file.getPath().getParent();
                        if (root != null) {
                            Path otherFile = root.resolve(i.getModule());
                            for (PyFileRepresentative f : files) {
                                if (f.getPath().equals(otherFile)) {
                                    return f;
                                }
                            }
                        }
                    }
                }
                break;
            case Class:
            case Function:
                return findModuleByFullPath(files, path, start.getParent());
        }

        return null;
    }

    public static IStructure<PyFileRepresentative> findEntityExceptMethod(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start) {
        IStructure<PyFileRepresentative> result = PyEntitySearcher.findVariable(files, name, start, false);
        result = result == null ? PyEntitySearcher.findClass(files, name, start) : result;
        result = result == null ? PyEntitySearcher.findModuleByAlias(files, name, start) : result;
        return result;
    }
}
