package mai.student.tokenizers.python3.tokenization;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;
import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.intermediateStates.python.PyFuncRepresentative;
import mai.student.tokenizers.python3.ast.nodes.PyNode;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyAttribute;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyCall;
import mai.student.tokenizers.python3.ast.nodes.expressions.PyKeyword;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.utility.PyEntitySearcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (element.getFunc() instanceof PyName) {
            String name = ((PyName) element.getFunc()).getId();

            List<PyFuncRepresentative> searchResults = PyEntitySearcher.findMethodsAnywhere(files, name, toRoot(scope), false);
            if (!searchResults.isEmpty()) {
                PyFuncRepresentative func = searchResults.get(0);

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
        } else if (element.getFunc() instanceof PyAttribute) {
            String name = ((PyAttribute) element.getFunc()).getAttr();
            System.out.println(name);
            List<PyFuncRepresentative> searchResults = PyEntitySearcher.findMethodsAnywhere(files, name, toRoot(scope), false);
            if (!searchResults.isEmpty()) {
                PyFuncRepresentative func = searchResults.get(0);

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
    }

    private IStructure<PyFileRepresentative> toRoot(IStructure<PyFileRepresentative> target) {
        if (target.getStrucType() == StructureType.File) {
            return target;
        }

        return toRoot(target.getParent());
    }
}
