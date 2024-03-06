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

public class AbstractPyVoidVisitor<T> implements PyVoidVisitor<T> {

    @Override
    public void visit(PyAsyncFor element, T arg) {
        element.getTarget().accept(this, arg);
        element.getIter().accept(this, arg);
        element.getBody().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyAsyncFunctionDef element, T arg) {
        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> s.accept(this, arg));
        }
        if (element.getArgs() != null) {
            element.getArgs().accept(this, arg);
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
        if (element.getReturns() != null) {
            element.getReturns().accept(this, arg);
        }
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyAsyncWith element, T arg) {
        element.getItems().forEach(s -> s.accept(this, arg));
        element.getBody().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyAwait element, T arg) {
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyComp element, T arg) {
        element.getTarget().accept(this, arg);
        element.getIter().accept(this, arg);
        if (element.getIfs() != null) {
            element.getIfs().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyDictComp element, T arg) {
        element.getKey().accept(this, arg);
        element.getValue().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyGeneratorExp element, T arg) {
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyListComp element, T arg) {
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PySetComp element, T arg) {
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyBreak element, T arg) {

    }

    @Override
    public void visit(PyContinue element, T arg) {

    }

    @Override
    public void visit(PyExceptHandler element, T arg) {
        if (element.getType() != null) {
            element.getType().accept(this, arg);
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyFor element, T arg) {
        element.getTarget().accept(this, arg);
        element.getIter().accept(this, arg);
        element.getBody().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyIf element, T arg) {
        element.getTest().accept(this, arg);
        element.getBody().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyTry element, T arg) {
        element.getBody().forEach(s -> s.accept(this, arg));
        element.getHandlers().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
        if (element.getFinalbody() != null) {
            element.getFinalbody().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyTryStar element, T arg) {
        element.getBody().forEach(s -> s.accept(this, arg));
        element.getHandlers().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
        if (element.getFinalbody() != null) {
            element.getFinalbody().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyWhile element, T arg) {
        element.getTest().accept(this, arg);
        element.getBody().forEach(s -> s.accept(this, arg));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyWith element, T arg) {
        element.getItems().forEach(s -> s.accept(this, arg));
        element.getBody().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyWithItem element, T arg) {
        element.getContextExpr().accept(this, arg);
        if (element.getOptionalVars() != null) {
            element.getOptionalVars().accept(this, arg);
        }
    }

    @Override
    public void visit(PyArg element, T arg) {
        if (element.getAnnotation() != null) {
            element.getAnnotation().accept(this, arg);
        }
    }

    @Override
    public void visit(PyArguments element, T arg) {
        if (element.getPosonlyargs() != null) {
            element.getPosonlyargs().forEach(s -> s.accept(this, arg));
        }
        if (element.getArgs() != null) {
            element.getArgs().forEach(s -> s.accept(this, arg));
        }
        if (element.getVararg() != null) {
            element.getVararg().accept(this, arg);
        }
        if (element.getKwonlyargs() != null) {
            element.getKwonlyargs().forEach(s -> s.accept(this, arg));
        }
        if (element.getKwDefaults() != null) {
            element.getKwDefaults().forEach(s -> {
                if (s != null) {
                    s.accept(this, arg);
                }
            });
        }
        if (element.getKwarg() != null) {
            element.getKwarg().accept(this, arg);
        }
        if (element.getDefaults() != null) {
            element.getDefaults().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyClassDef element, T arg) {
        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> s.accept(this, arg));
        }
        if (element.getBases() != null) {
            element.getBases().forEach(s -> s.accept(this, arg));
        }
        if (element.getKeywords() != null) {
            element.getKeywords().forEach(s -> s.accept(this, arg));
        }
        element.getBody().forEach(s -> s.accept(this, arg));
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyFunctionDef element, T arg) {
        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> s.accept(this, arg));
        }
        if (element.getArgs() != null) {
            element.getArgs().accept(this, arg);
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
        if (element.getReturns() != null) {
            element.getReturns().accept(this, arg);
        }
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyGlobal element, T arg) {

    }

    @Override
    public void visit(PyLambda element, T arg) {
        element.getArgs().accept(this, arg);
        element.getBody().accept(this, arg);
    }

    @Override
    public void visit(PyNonlocal element, T arg) {

    }

    @Override
    public void visit(PyReturn element, T arg) {
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyYield element, T arg) {
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyYieldFrom element, T arg) {
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAttribute element, T arg) {
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyBinOp element, T arg) {
        element.getLeft().accept(this, arg);
        element.getRight().accept(this, arg);
    }

    @Override
    public void visit(PyBoolOp element, T arg) {
        element.getValues().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyCall element, T arg) {
        element.getFunc().accept(this, arg);
        if (element.getArgs() != null) {
            element.getArgs().forEach(s -> s.accept(this, arg));
        }
        if (element.getKeywords() != null) {
            element.getKeywords().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyCompare element, T arg) {
        element.getLeft().accept(this, arg);
        element.getComparators().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyExpr element, T arg) {
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyIfExp element, T arg) {
        element.getTest().accept(this, arg);
        element.getBody().accept(this, arg);
        element.getOrelse().accept(this, arg);
    }

    @Override
    public void visit(PyKeyword element, T arg) {
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyNamedExpr element, T arg) {
        element.getTarget().accept(this, arg);
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyUnaryOp element, T arg) {
        element.getOperand().accept(this, arg);
    }

    @Override
    public void visit(PyAlias element, T arg) {

    }

    @Override
    public void visit(PyImport element, T arg) {
        element.getNames().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyImportFrom element, T arg) {
        element.getNames().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyConstant element, T arg) {

    }

    @Override
    public void visit(PyFormattedValue element, T arg) {
        element.getValue().accept(this, arg);
        if (element.getFormatSpec() != null) {
            element.getFormatSpec().accept(this, arg);
        }
    }

    @Override
    public void visit(PyJoinedStr element, T arg) {
        element.getValues().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyList element, T arg) {
        if (element.getElts() != null) {
            element.getElts().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PySet element, T arg) {
        if (element.getElts() != null) {
            element.getElts().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyTuple element, T arg) {
        if (element.getElts() != null) {
            element.getElts().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyMatch element, T arg) {
        element.getSubject().accept(this, arg);
        element.getCases().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyMatchAs element, T arg) {
        element.getPattern().accept(this, arg);
    }

    @Override
    public void visit(PyMatchCase element, T arg) {
        element.getPattern().accept(this, arg);
        if (element.getGuard() != null) {
            element.getGuard().accept(this, arg);
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyMatchClass element, T arg) {
        element.getCls().accept(this, arg);
        if (element.getPatterns() != null) {
            element.getPatterns().forEach(s -> s.accept(this, arg));
        }
        if (element.getKwdPatterns() != null) {
            element.getKwdPatterns().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyMatchMapping element, T arg) {
        if (element.getKeys() != null) {
            element.getKeys().forEach(s -> s.accept(this, arg));
        }
        if (element.getPatterns() != null) {
            element.getPatterns().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyMatchOr element, T arg) {
        element.getPatterns().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyMatchSequence element, T arg) {
        element.getPatterns().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyMatchSingleton element, T arg) {

    }

    @Override
    public void visit(PyMatchStar element, T arg) {

    }

    @Override
    public void visit(PyMatchValue element, T arg) {
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyExpressionContainer element, T arg) {
        element.getBody().accept(this, arg);
    }

    @Override
    public void visit(PyFunctionType element, T arg) {
        element.getArgTypes().forEach(s -> s.accept(this, arg));
        element.getReturnType().accept(this, arg);
    }

    @Override
    public void visit(PyInteractive element, T arg) {
        element.getBody().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyModule element, T arg) {
        element.getBody().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyAnnAssign element, T arg) {
        element.getTarget().accept(this, arg);
        if (element.getAnnotation() != null) {
            element.getAnnotation().accept(this, arg);
        }
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAssert element, T arg) {
        element.getTest().accept(this, arg);
        if (element.getMsg() != null) {
            element.getMsg().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAssign element, T arg) {
        element.getTargets().forEach(s -> s.accept(this, arg));
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyAugAssign element, T arg) {
        element.getTarget().accept(this, arg);
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyDelete element, T arg) {
        element.getTargets().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyPass element, T arg) {

    }

    @Override
    public void visit(PyRaise element, T arg) {
        element.getExc().accept(this, arg);
        if (element.getCause() != null) {
            element.getCause().accept(this, arg);
        }
    }

    @Override
    public void visit(PyTypeAlias element, T arg) {
        element.getName().accept(this, arg);
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PySlice element, T arg) {
        if (element.getLower() != null) {
            element.getLower().accept(this, arg);
        }
        if (element.getUpper() != null) {
            element.getUpper().accept(this, arg);
        }
        if (element.getStep() != null) {
            element.getStep().accept(this, arg);
        }
    }

    @Override
    public void visit(PySubscript element, T arg) {
        element.getValue().accept(this, arg);
        element.getSlice().accept(this, arg);
    }

    @Override
    public void visit(PyParamSpec element, T arg) {
    }

    @Override
    public void visit(PyTypeVar element, T arg) {
        element.getBound().accept(this, arg);
    }

    @Override
    public void visit(PyTypeVarTuple element, T arg) {

    }

    @Override
    public void visit(PyName element, T arg) {

    }

    @Override
    public void visit(PyStarred element, T arg) {
        element.getValue().accept(this, arg);
    }
}
