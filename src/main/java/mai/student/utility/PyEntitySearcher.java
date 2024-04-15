package mai.student.utility;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.python.*;
import mai.student.tokenizers.python3.preprocessing.PySpecificFunction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PyEntitySearcher {

    public static List<PyFuncRepresentative> findMethodsAnywhere(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean called) {
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
                        PyFuncRepresentative init = c.getFunctions().stream()
                                .filter(f -> f.getName().equals(PySpecificFunction.INIT.getName())).findFirst().orElse(null);
                        if (init != null) {
                            result.add(init);
                        }
                    }

                    result.addAll(findMethodsAnywhere(files, name, c, false));
                });

                if (!called) {
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

    public static PyFuncRepresentative findMethod(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean justInheritance) {
        if (files == null || name == null) {
            return null;
        }

        PyFuncRepresentative result = null;
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyFuncRepresentative f : file.getFunctions()) {
                    if (f.getName().equals(name)) {
                        return f;
                    }
                }

                for (PyClassRepresentation c : file.getClasses()) {
                    if (c.getName().equals(name)) {
                        PyFuncRepresentative init = c.getFunctions().stream()
                                .filter(f -> f.getName().equals(PySpecificFunction.INIT.getName())).findFirst().orElse(null);
                        if (init != null) {
                            return init;
                        }
                    }

                    result = findMethod(files, name, c, false);
                    if (result != null) {
                        return result;
                    }
                }
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
                for (PyFuncRepresentative f : classs.getFunctions()) {
                    if (f.getName().equals(name)) {
                        return f;
                    }
                }

                for (PyType supers : classs.getInheritanceList()) {
                    PyClassRepresentation sc = findClass(files, supers.getName(), classs);
                    if (sc != null) {
                        result = findMethod(files, name, sc, true);
                        if (result != null) {
                            return result;
                        }
                    }
                }

                if (!justInheritance) {
                    result = findMethod(files, name, classs.getParent(), false);
                    if (result != null) {
                        return result;
                    }
                }
                break;
            case Function:
                PyFuncRepresentative func = (PyFuncRepresentative) start;
                for (PyFuncRepresentative f : func.getFunctions()) {
                    if (f.getName().equals(name)) {
                        return f;
                    }
                }

                result = findMethod(files, name, func.getParent(), false);
                if (result != null) {
                    return result;
                }
                break;
        }

        return null;
    }

    public static PyClassRepresentation findClass(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start) {
        if (files == null || name == null) {
            return null;
        }

        PyClassRepresentation result = null;
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
        }

        return null;
    }

    public static PyVariableRepresentative findVariable(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start, boolean justInheritance) {
        if (files == null || name == null) {
            return null;
        }

        PyVariableRepresentative result = null;
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyVariableRepresentative v : file.getVariables()) {
                    if (v.getName().equals(name)) {
                        return v;
                    }
                }
            case Class:
                PyClassRepresentation classs = (PyClassRepresentation) start;
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
        }

        return null;
    }

    public static PyFileRepresentative findModule(List<PyFileRepresentative> files, String name, IStructure<PyFileRepresentative> start) {
        if (files == null || name == null) {
            return null;
        }

        PyVariableRepresentative result = null;
        switch (start.getStrucType()) {
            case File:
                PyFileRepresentative file = (PyFileRepresentative) start;
                for (PyImport i : file.getImports()) {
                    if (i.getAlias().equals(name)) {
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
                return findModule(files, name, start.getParent());
        }

        return null;
    }
}
