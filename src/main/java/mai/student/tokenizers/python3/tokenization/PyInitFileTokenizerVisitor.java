package mai.student.tokenizers.python3.tokenization;

import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.intermediateStates.python.PyImport;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyClassDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyFunctionDef;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PyInitFileTokenizerVisitor extends PyFullAbstractingTokenizerVisitor {


    public PyInitFileTokenizerVisitor(Map<String, Integer> tokenDictionary, List<PyFileRepresentative> files, PyFileRepresentative scope, List<Integer> result) {
        super(tokenDictionary, files, scope, result);

        Set<PyFileRepresentative> addedFiles = new HashSet<>();
        for (PyImport i : scope.getImports()) {
                Path root = scope.getPath().getParent();
                if (root == null) {
                    continue;
                }

                Path otherFile = root.resolve(i.getModule());
                PyFileRepresentative file = null;
                for (PyFileRepresentative f : files) {
                    if (f.getPath().equals(otherFile)) {
                        file = f;
                    }
                }
                if (file == null || addedFiles.contains(file)) {
                    continue;
                }

                if (!file.isTokenized()) {
                    PyBasicTokenizerVisitor visitor = new PyInitFileTokenizerVisitor(tokenDictionary, files, file, file.getInitCode());
                    file.setTokenized(true);
                    file.getAst().accept(visitor, null);
                }

                addedFiles.add(file);
                result.addAll(file.getInitCode());
        }
    }

    @Override
    public void visit(PyClassDef element, Object arg) {
    }

    @Override
    public void visit(PyFunctionDef element, Object arg) {
    }

    @Override
    public void visit(PyAsyncFunctionDef element, Object arg) {
    }
}
