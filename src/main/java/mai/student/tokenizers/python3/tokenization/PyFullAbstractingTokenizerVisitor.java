package mai.student.tokenizers.python3.tokenization;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;
import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.intermediateStates.python.PyFuncRepresentative;
import mai.student.intermediateStates.python.PyVariableRepresentative;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.PyPositionalNode;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyAttribute;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCall;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyKeyword;
import mai.student.tokenizers.python3.ast.nodes.statements.PyAnnAssign;
import mai.student.tokenizers.python3.ast.nodes.statements.PyAssign;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.utility.PyEntitySearcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PyFullAbstractingTokenizerVisitor extends PyNameAbstractingTokenizerVisitor {

    private final List<PyFileRepresentative> files;
    private final IStructure<PyFileRepresentative> scope;

    public PyFullAbstractingTokenizerVisitor(Map<String, Integer> tokenDictionary, List<PyFileRepresentative> files,
                                             IStructure<PyFileRepresentative> scope, List<Integer> result) {
        super(tokenDictionary, result);
        this.files = files;
        this.scope = scope;
    }

    @Override
    public void visit(PyAssign element, Object arg) {
        // Пытаемся определить тип значения
        if (element.getValue() instanceof PyCall) {
            List<PyFuncRepresentative> searchResults = callToLink((PyCall) element.getValue());
            if (searchResults != null && !searchResults.isEmpty()) {
                PyFuncRepresentative func = selectFunc(searchResults, element.getLineno());

                if (func.getReturnValue() != null && func.getReturnValue().getLinkToClass() != null) {
                    element.getTargets().forEach(s -> {
                        PyVariableRepresentative var = null;
                        if (s instanceof PyName) {
                            var = PyEntitySearcher.findVariable(files, ((PyName) s).getId(), scope, false);
                        } else if (s instanceof PyAttribute) {
                            IStructure<PyFileRepresentative> entity = resolveAttribute((PyAttribute) s, scope, false);
                            if (entity instanceof PyVariableRepresentative) {
                                var = (PyVariableRepresentative) entity;
                            }
                        }

                        if (var != null) {
                            var.setRealType(func.getReturnValue());
                        }
                    });
                }
            }
        }

        super.visit(element, arg);
    }

    @Override
    public void visit(PyAnnAssign element, Object arg) {
        // Пытаемся определить тип значения
        if (element.getValue() instanceof PyCall) {
            List<PyFuncRepresentative> searchResults = callToLink((PyCall) element.getValue());
            if (searchResults != null && !searchResults.isEmpty()) {
                PyFuncRepresentative func = selectFunc(searchResults, element.getLineno());

                if (func.getReturnValue() != null && func.getReturnValue().getLinkToClass() != null) {
                    PyNode s = element.getTarget();
                    PyVariableRepresentative var = null;
                    if (s instanceof PyName) {
                        var = PyEntitySearcher.findVariable(files, ((PyName) s).getId(), scope, false);
                    } else if (s instanceof PyAttribute) {
                        IStructure<PyFileRepresentative> entity = resolveAttribute((PyAttribute) s, scope, false);
                        if (entity instanceof PyVariableRepresentative) {
                            var = (PyVariableRepresentative) entity;
                        }
                    }

                    if (var != null) {
                        var.setRealType(func.getReturnValue());
                    }
                }
            }
        }

        super.visit(element, arg);
    }

    @Override
    public void visit(PyCall element, Object arg) {
        element.getFunc().accept(this, arg);
        addToken(LEFT_PAREN);
        boolean needComma = false;
        if (element.getArgs() != null && !element.getArgs().isEmpty()) {
            needComma = true;
            boolean isFirst = true;
            for (PyNode s : element.getArgs()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        }

        if (element.getKeywords() != null && !element.getKeywords().isEmpty()) {
            if (needComma) {
                addToken(COMMA);
            }

            boolean isFirst = true;
            for (PyKeyword s : element.getKeywords()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        }
        addToken(RIGHT_PAREN);

        // add body
        List<PyFuncRepresentative> searchResults = callToLink(element);
        if (searchResults != null && !searchResults.isEmpty()) {
            PyFuncRepresentative func = selectFunc(searchResults, element.getLineno());

            if (!func.isTokenized()) {
                func.setTokenized(true);
                if (func.isAsync()) {
                    ((PyAsyncFunctionDef) func.getSelfNode()).getBody()
                            .forEach(s -> s.accept(new PyFullAbstractingTokenizerVisitor(tokenDictionary, files, func, func.getTokens()), null));
                } else {
                    ((PyFunctionDef) func.getSelfNode()).getBody()
                            .forEach(s -> s.accept(new PyFullAbstractingTokenizerVisitor(tokenDictionary, files, func, func.getTokens()), null));
                }
            }
            result.addAll(func.getTokens());
        }
    }

    private List<PyFuncRepresentative> callToLink(PyCall element) {
        List<PyFuncRepresentative> searchResults = null;
        if (element.getFunc() instanceof PyName) {
            String name = ((PyName) element.getFunc()).getId();

            searchResults = PyEntitySearcher.findMethod(files, name, scope, false);
            searchResults = searchResults.isEmpty() ? PyEntitySearcher.findMethodsAnywhere(files, name, toRoot(scope), false) : searchResults;
        } else if (element.getFunc() instanceof PyAttribute) {
            PyAttribute attr = (PyAttribute) element.getFunc(); // эта штука может быть модулем, классом или другой функцией
            IStructure<PyFileRepresentative> root = resolveAttribute(attr, scope, true);

            searchResults = PyEntitySearcher.findMethod(files, attr.getAttr(), root, false);
            searchResults = searchResults.isEmpty() ? PyEntitySearcher.findMethodsAnywhere(files, attr.getAttr(), toRoot(scope), false)
                    : searchResults;
            searchResults = searchResults.stream().filter(f -> f.getParent() != scope && f.getParent() != toRoot(scope)).collect(Collectors.toList());
        }

        if (searchResults != null) {
            searchResults.forEach(r -> r.actuateTypes(files));
        }
        return searchResults;
    }

    private IStructure<PyFileRepresentative> resolveAttribute(PyAttribute element, IStructure<PyFileRepresentative> scope, boolean methodSearch) {
        IStructure<PyFileRepresentative> root = null;
        if (element.getValue() instanceof PyName) {
            String name = ((PyName) element.getValue()).getId();
            root = PyEntitySearcher.findEntityExceptMethod(files, name, scope);
        } else if (element.getValue() instanceof PyAttribute) {
            PyAttribute attr = (PyAttribute) element.getValue();
            Path forModule = attrToPath(attr);
            root = PyEntitySearcher.findModuleByFullPath(files, forModule, scope);
            root = root == null ? resolveAttribute(attr, scope, false) : root;
        }

        if (methodSearch) {
            return root;
        }

        return root == null ? null : PyEntitySearcher.findEntityExceptMethod(files, element.getAttr(), root);
    }

    private Path attrToPath(PyAttribute attr) {
        if (attr.getValue() instanceof PyName) {
            String name = ((PyName) attr.getValue()).getId();
            return Path.of(name);
        } else if (attr.getValue() instanceof PyAttribute) {
            Path result = attrToPath((PyAttribute) attr.getValue());
            return result == null ? null : result.resolve(attr.getAttr());
        }

        return null;
    }

    private IStructure<PyFileRepresentative> toRoot(IStructure<PyFileRepresentative> target) {
        if (target.getStrucType() == StructureType.File) {
            return target;
        }

        return toRoot(target.getParent());
    }

    private PyFuncRepresentative selectFunc(List<PyFuncRepresentative> functions, Integer usePosition) {
        if (usePosition == null) {
            return functions.get(0);
        }

        // Для найденных в том же файле берем ближайшее определение ДО использования
        PyFileRepresentative root = (PyFileRepresentative) toRoot(scope);
        List<PyFuncRepresentative> sameFile = functions.stream().filter(f -> toRoot(f).equals(root)).collect(Collectors.toList());
        PyFuncRepresentative result;
        if (!sameFile.isEmpty()) {
            result = sameFile.get(0);
            for (int i = 1; i < sameFile.size(); ++i) {
                PyFuncRepresentative f = sameFile.get(i);
                if (f.getSelfNode() == null || f.getSelfNode() instanceof PyPositionalNode &&
                        ((PyPositionalNode) f.getSelfNode()).getLineno() > usePosition) {
                    return result;
                }

                result = f;
            }

            return result;
        }

        // Для найденных в других файлах берем последнее из первого файла
        List<PyFuncRepresentative> otherFiles = functions.stream().filter(f -> !toRoot(f).equals(root)).collect(Collectors.toList());
        result = otherFiles.get(0);
        for (int i = 1; i < otherFiles.size(); ++i) {
            if (toRoot(result) != toRoot(otherFiles.get(i))) {
                return result;
            }

            result = otherFiles.get(i);
        }

        return result;
    }
}
