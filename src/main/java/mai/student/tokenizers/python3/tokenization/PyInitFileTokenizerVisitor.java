package mai.student.tokenizers.python3.tokenization;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyClassDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyFunctionDef;

import java.util.List;
import java.util.Map;

public class PyInitFileTokenizerVisitor extends PyFullAbstractingTokenizerVisitor {


    public PyInitFileTokenizerVisitor(Map<String, Integer> tokenDictionary, List<PyFileRepresentative> files, IStructure<PyFileRepresentative> scope, List<Integer> result) {
        super(tokenDictionary, files, scope, result);
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
