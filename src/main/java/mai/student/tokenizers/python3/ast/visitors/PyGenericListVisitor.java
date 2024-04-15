package mai.student.tokenizers.python3.ast.visitors;

import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFor;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncWith;
import mai.student.tokenizers.python3.ast.nodes.async.PyAwait;
import mai.student.tokenizers.python3.ast.nodes.comprehensions.*;
import mai.student.tokenizers.python3.ast.nodes.control.*;
import mai.student.tokenizers.python3.ast.nodes.definitions.*;
import mai.student.tokenizers.python3.ast.nodes.expressions.*;
import mai.student.tokenizers.python3.ast.nodes.imports.PyAlias;
import mai.student.tokenizers.python3.ast.nodes.imports.PyImport;
import mai.student.tokenizers.python3.ast.nodes.imports.PyImportFrom;
import mai.student.tokenizers.python3.ast.nodes.literals.*;
import mai.student.tokenizers.python3.ast.nodes.matching.*;
import mai.student.tokenizers.python3.ast.nodes.roots.PyExpressionContainer;
import mai.student.tokenizers.python3.ast.nodes.roots.PyFunctionType;
import mai.student.tokenizers.python3.ast.nodes.roots.PyInteractive;
import mai.student.tokenizers.python3.ast.nodes.roots.PyModule;
import mai.student.tokenizers.python3.ast.nodes.statements.*;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySlice;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.types.PyParamSpec;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVar;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVarTuple;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.nodes.variables.PyStarred;

import java.util.List;

public interface PyGenericListVisitor<T, K> {

    List<T> visit(PyAsyncFor element, K arg);

    List<T> visit(PyAsyncFunctionDef element, K arg);

    List<T> visit(PyAsyncWith element, K arg);

    List<T> visit(PyAwait element, K arg);


    List<T> visit(PyComp element, K arg);

    List<T> visit(PyDictComp element, K arg);

    List<T> visit(PyGeneratorExp element, K arg);

    List<T> visit(PyListComp element, K arg);

    List<T> visit(PySetComp element, K arg);


    List<T> visit(PyBreak element, K arg);

    List<T> visit(PyContinue element, K arg);

    List<T> visit(PyExceptHandler element, K arg);

    List<T> visit(PyFor element, K arg);

    List<T> visit(PyIf element, K arg);

    List<T> visit(PyTry element, K arg);

    List<T> visit(PyTryStar element, K arg);

    List<T> visit(PyWhile element, K arg);

    List<T> visit(PyWith element, K arg);

    List<T> visit(PyWithItem element, K arg);


    List<T> visit(PyArg element, K arg);

    List<T> visit(PyArguments element, K arg);

    List<T> visit(PyClassDef element, K arg);

    List<T> visit(PyFunctionDef element, K arg);

    List<T> visit(PyGlobal element, K arg);

    List<T> visit(PyLambda element, K arg);

    List<T> visit(PyNonlocal element, K arg);

    List<T> visit(PyReturn element, K arg);

    List<T> visit(PyYield element, K arg);

    List<T> visit(PyYieldFrom element, K arg);


    List<T> visit(PyAttribute element, K arg);

    List<T> visit(PyBinOp element, K arg);

    List<T> visit(PyBoolOp element, K arg);

    List<T> visit(PyCall element, K arg);

    List<T> visit(PyCompare element, K arg);

    List<T> visit(PyExpr element, K arg);

    List<T> visit(PyIfExp element, K arg);

    List<T> visit(PyKeyword element, K arg);

    List<T> visit(PyNamedExpr element, K arg);

    List<T> visit(PyUnaryOp element, K arg);


    List<T> visit(PyAlias element, K arg);

    List<T> visit(PyImport element, K arg);

    List<T> visit(PyImportFrom element, K arg);


    List<T> visit(PyConstant element, K arg);

    List<T> visit(PyDict element, K arg);

    List<T> visit(PyFormattedValue element, K arg);

    List<T> visit(PyJoinedStr element, K arg);

    List<T> visit(PyList element, K arg);

    List<T> visit(PySet element, K arg);

    List<T> visit(PyTuple element, K arg);


    List<T> visit(PyMatch element, K arg);

    List<T> visit(PyMatchAs element, K arg);

    List<T> visit(PyMatchCase element, K arg);

    List<T> visit(PyMatchClass element, K arg);

    List<T> visit(PyMatchMapping element, K arg);

    List<T> visit(PyMatchOr element, K arg);

    List<T> visit(PyMatchSequence element, K arg);

    List<T> visit(PyMatchSingleton element, K arg);

    List<T> visit(PyMatchStar element, K arg);

    List<T> visit(PyMatchValue element, K arg);


    List<T> visit(PyExpressionContainer element, K arg);

    List<T> visit(PyFunctionType element, K arg);

    List<T> visit(PyInteractive element, K arg);

    List<T> visit(PyModule element, K arg);


    List<T> visit(PyAnnAssign element, K arg);

    List<T> visit(PyAssert element, K arg);

    List<T> visit(PyAssign element, K arg);

    List<T> visit(PyAugAssign element, K arg);

    List<T> visit(PyDelete element, K arg);

    List<T> visit(PyPass element, K arg);

    List<T> visit(PyRaise element, K arg);

    List<T> visit(PyTypeAlias element, K arg);


    List<T> visit(PySlice element, K arg);

    List<T> visit(PySubscript element, K arg);


    List<T> visit(PyParamSpec element, K arg);

    List<T> visit(PyTypeVar element, K arg);

    List<T> visit(PyTypeVarTuple element, K arg);


    List<T> visit(PyName element, K arg);

    List<T> visit(PyStarred element, K arg);
}
