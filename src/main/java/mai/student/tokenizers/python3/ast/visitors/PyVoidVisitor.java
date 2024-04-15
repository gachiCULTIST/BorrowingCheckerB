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

public interface PyVoidVisitor<T> {

    void visit(PyAsyncFor element, T arg);

    void visit(PyAsyncFunctionDef element, T arg);

    void visit(PyAsyncWith element, T arg);

    void visit(PyAwait element, T arg);


    void visit(PyComp element, T arg);

    void visit(PyDictComp element, T arg);

    void visit(PyGeneratorExp element, T arg);

    void visit(PyListComp element, T arg);

    void visit(PySetComp element, T arg);


    void visit(PyBreak element, T arg);

    void visit(PyContinue element, T arg);

    void visit(PyExceptHandler element, T arg);

    void visit(PyFor element, T arg);

    void visit(PyIf element, T arg);

    void visit(PyTry element, T arg);

    void visit(PyTryStar element, T arg);

    void visit(PyWhile element, T arg);

    void visit(PyWith element, T arg);

    void visit(PyWithItem element, T arg);


    void visit(PyArg element, T arg);

    void visit(PyArguments element, T arg);

    void visit(PyClassDef element, T arg);

    void visit(PyFunctionDef element, T arg);

    void visit(PyGlobal element, T arg);

    void visit(PyLambda element, T arg);

    void visit(PyNonlocal element, T arg);

    void visit(PyReturn element, T arg);

    void visit(PyYield element, T arg);

    void visit(PyYieldFrom element, T arg);


    void visit(PyAttribute element, T arg);

    void visit(PyBinOp element, T arg);

    void visit(PyBoolOp element, T arg);

    void visit(PyCall element, T arg);

    void visit(PyCompare element, T arg);

    void visit(PyExpr element, T arg);

    void visit(PyIfExp element, T arg);

    void visit(PyKeyword element, T arg);

    void visit(PyNamedExpr element, T arg);

    void visit(PyUnaryOp element, T arg);


    void visit(PyAlias element, T arg);

    void visit(PyImport element, T arg);

    void visit(PyImportFrom element, T arg);


    void visit(PyConstant element, T arg);

    void visit(PyDict element, T arg);

    void visit(PyFormattedValue element, T arg);

    void visit(PyJoinedStr element, T arg);

    void visit(PyList element, T arg);

    void visit(PySet element, T arg);

    void visit(PyTuple element, T arg);


    void visit(PyMatch element, T arg);

    void visit(PyMatchAs element, T arg);

    void visit(PyMatchCase element, T arg);

    void visit(PyMatchClass element, T arg);

    void visit(PyMatchMapping element, T arg);

    void visit(PyMatchOr element, T arg);

    void visit(PyMatchSequence element, T arg);

    void visit(PyMatchSingleton element, T arg);

    void visit(PyMatchStar element, T arg);

    void visit(PyMatchValue element, T arg);


    void visit(PyExpressionContainer element, T arg);

    void visit(PyFunctionType element, T arg);

    void visit(PyInteractive element, T arg);

    void visit(PyModule element, T arg);


    void visit(PyAnnAssign element, T arg);

    void visit(PyAssert element, T arg);

    void visit(PyAssign element, T arg);

    void visit(PyAugAssign element, T arg);

    void visit(PyDelete element, T arg);

    void visit(PyPass element, T arg);

    void visit(PyRaise element, T arg);

    void visit(PyTypeAlias element, T arg);


    void visit(PySlice element, T arg);

    void visit(PySubscript element, T arg);


    void visit(PyParamSpec element, T arg);

    void visit(PyTypeVar element, T arg);

    void visit(PyTypeVarTuple element, T arg);


    void visit(PyName element, T arg);

    void visit(PyStarred element, T arg);
}
