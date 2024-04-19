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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPyGenericListVisitor<T, K> implements PyGenericListVisitor<T, K> {


    @Override
    public List<T> visit(PyAsyncFor element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTarget().accept(this, arg));
        result.addAll(element.getIter().accept(this, arg));
        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyAsyncFunctionDef element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getArgs() != null) {
            result.addAll(element.getArgs().accept(this, arg));
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getReturns() != null) {
            result.addAll(element.getReturns().accept(this, arg));
        }
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyAsyncWith element, K arg) {
        List<T> result = new ArrayList<>();

        element.getItems().forEach(s -> result.addAll(s.accept(this, arg)));
        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyAwait element, K arg) {
        return element.getValue().accept(this, arg);
    }

    @Override
    public List<T> visit(PyComp element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTarget().accept(this, arg));
        result.addAll(element.getIter().accept(this, arg));
        if (element.getIfs() != null) {
            element.getIfs().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyDictComp element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getKey().accept(this, arg));
        result.addAll(element.getValue().accept(this, arg));
        element.getGenerators().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyGeneratorExp element, K arg) {
        List<T> result = new ArrayList<>(element.getElt().accept(this, arg));

        element.getGenerators().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyListComp element, K arg) {
        List<T> result = new ArrayList<>(element.getElt().accept(this, arg));

        element.getGenerators().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PySetComp element, K arg) {
        List<T> result = new ArrayList<>(element.getElt().accept(this, arg));

        element.getGenerators().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyBreak element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyContinue element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyExceptHandler element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getExcType() != null) {
            result.addAll(element.getExcType().accept(this, arg));
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyFor element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTarget().accept(this, arg));
        result.addAll(element.getIter().accept(this, arg));
        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyIf element, K arg) {
        List<T> result = new ArrayList<>(element.getTest().accept(this, arg));

        element.getBody().forEach(s -> {
            result.addAll(s.accept(this, arg));
        });
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyTry element, K arg) {
        List<T> result = new ArrayList<>();

        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        element.getHandlers().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getFinalbody() != null) {
            element.getFinalbody().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyTryStar element, K arg) {
        List<T> result = new ArrayList<>();

        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        element.getHandlers().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getFinalbody() != null) {
            element.getFinalbody().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyWhile element, K arg) {
        List<T> result = new ArrayList<>(element.getTest().accept(this, arg));

        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getOrelse() != null) {
            element.getOrelse().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyWith element, K arg) {
        List<T> result = new ArrayList<>();

        element.getItems().forEach(s -> result.addAll(s.accept(this, arg)));
        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyWithItem element, K arg) {
        List<T> result = new ArrayList<>(element.getContextExpr().accept(this, arg));

        if (element.getOptionalVars() != null) {
            result.addAll(element.getOptionalVars().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyArg element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getAnnotation() != null) {
            result.addAll(element.getAnnotation().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyArguments element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getPosonlyargs() != null) {
            element.getPosonlyargs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getArgs() != null) {
            element.getArgs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getVararg() != null) {
            result.addAll(element.getVararg().accept(this, arg));
        }
        if (element.getKwonlyargs() != null) {
            element.getKwonlyargs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getKwDefaults() != null) {
            element.getKwDefaults().forEach(s -> {
                if (s != null) {
                    result.addAll(s.accept(this, arg));
                }
            });
        }
        if (element.getKwarg() != null) {
            result.addAll(element.getKwarg().accept(this, arg));
        }
        if (element.getDefaults() != null) {
            element.getDefaults().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyClassDef element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getBases() != null) {
            element.getBases().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getKeywords() != null) {
            element.getKeywords().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyFunctionDef element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getArgs() != null) {
            result.addAll(element.getArgs().accept(this, arg));
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getReturns() != null) {
            result.addAll(element.getReturns().accept(this, arg));
        }
        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyGlobal element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyLambda element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getArgs().accept(this, arg));
        result.addAll(element.getBody().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyNonlocal element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyReturn element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getValue() != null) {
            result.addAll(element.getValue().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyYield element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getValue() != null) {
            result.addAll(element.getValue().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyYieldFrom element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getValue() != null) {
            result.addAll(element.getValue().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyAttribute element, K arg) {
        return element.getValue().accept(this, arg);
    }

    @Override
    public List<T> visit(PyBinOp element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getLeft().accept(this, arg));
        result.addAll(element.getRight().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyBoolOp element, K arg) {
        List<T> result = new ArrayList<>();

        element.getValues().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyCall element, K arg) {
        List<T> result = new ArrayList<>(element.getFunc().accept(this, arg));

        if (element.getArgs() != null) {
            element.getArgs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getKeywords() != null) {
            element.getKeywords().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyCompare element, K arg) {
        List<T> result = new ArrayList<>(element.getLeft().accept(this, arg));

        element.getComparators().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyExpr element, K arg) {
        return element.getValue().accept(this, arg);
    }

    @Override
    public List<T> visit(PyIfExp element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTest().accept(this, arg));
        result.addAll(element.getBody().accept(this, arg));
        result.addAll(element.getOrelse().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyKeyword element, K arg) {
        return element.getValue().accept(this, arg);
    }

    @Override
    public List<T> visit(PyNamedExpr element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTarget().accept(this, arg));
        result.addAll(element.getValue().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyUnaryOp element, K arg) {
        return element.getOperand().accept(this, arg);
    }

    @Override
    public List<T> visit(PyAlias element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyImport element, K arg) {
        List<T> result = new ArrayList<>();

        element.getNames().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyImportFrom element, K arg) {
        List<T> result = new ArrayList<>();

        element.getNames().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyConstant element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyDict element, K arg) {
        List<T> result = new ArrayList<>();

        element.getKeys().forEach(s -> result.addAll(s.accept(this, arg)));
        element.getValues().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyFormattedValue element, K arg) {
        List<T> result = new ArrayList<>(element.getValue().accept(this, arg));

        if (element.getFormatSpec() != null) {
            result.addAll(element.getFormatSpec().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyJoinedStr element, K arg) {
        List<T> result = new ArrayList<>();

        element.getValues().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyList element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getElts() != null) {
            element.getElts().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PySet element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getElts() != null) {
            element.getElts().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyTuple element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getElts() != null) {
            element.getElts().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyMatch element, K arg) {
        List<T> result = new ArrayList<>(element.getSubject().accept(this, arg));

        element.getCases().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyMatchAs element, K arg) {
        return element.getPattern().accept(this, arg);
    }

    @Override
    public List<T> visit(PyMatchCase element, K arg) {
        List<T> result = new ArrayList<>(element.getPattern().accept(this, arg));

        if (element.getGuard() != null) {
            result.addAll(element.getGuard().accept(this, arg));
        }
        if (element.getBody() != null) {
            element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyMatchClass element, K arg) {
        List<T> result = new ArrayList<>(element.getCls().accept(this, arg));

        if (element.getPatterns() != null) {
            element.getPatterns().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getKwdPatterns() != null) {
            element.getKwdPatterns().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyMatchMapping element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getKeys() != null) {
            element.getKeys().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getPatterns() != null) {
            element.getPatterns().forEach(s -> result.addAll(s.accept(this, arg)));
        }

        return result;
    }

    @Override
    public List<T> visit(PyMatchOr element, K arg) {
        List<T> result = new ArrayList<>();

        element.getPatterns().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyMatchSequence element, K arg) {
        List<T> result = new ArrayList<>();

        element.getPatterns().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyMatchSingleton element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyMatchStar element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyMatchValue element, K arg) {
        return element.getValue().accept(this, arg);
    }

    @Override
    public List<T> visit(PyExpressionContainer element, K arg) {
        return element.getBody().accept(this, arg);
    }

    @Override
    public List<T> visit(PyFunctionType element, K arg) {
        List<T> result = new ArrayList<>();

        element.getArgTypes().forEach(s -> result.addAll(s.accept(this, arg)));
        result.addAll(element.getReturnType().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyInteractive element, K arg) {
        List<T> result = new ArrayList<>();

        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyModule element, K arg) {
        List<T> result = new ArrayList<>();

        element.getBody().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyAnnAssign element, K arg) {
        List<T> result = new ArrayList<>(element.getTarget().accept(this, arg));

        if (element.getAnnotation() != null) {
            result.addAll(element.getAnnotation().accept(this, arg));
        }
        if (element.getValue() != null) {
            result.addAll(element.getValue().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyAssert element, K arg) {
        List<T> result = new ArrayList<>(element.getTest().accept(this, arg));

        if (element.getMsg() != null) {
            result.addAll(element.getMsg().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyAssign element, K arg) {
        List<T> result = new ArrayList<>();

        element.getTargets().forEach(s -> result.addAll(s.accept(this, arg)));
        result.addAll(element.getValue().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyAugAssign element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getTarget().accept(this, arg));
        result.addAll(element.getValue().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyDelete element, K arg) {
        List<T> result = new ArrayList<>();

        element.getTargets().forEach(s -> result.addAll(s.accept(this, arg)));

        return result;
    }

    @Override
    public List<T> visit(PyPass element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyRaise element, K arg) {
        List<T> result = new ArrayList<>(element.getExc().accept(this, arg));

        if (element.getCause() != null) {
            result.addAll(element.getCause().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PyTypeAlias element, K arg) {
        List<T> result = new ArrayList<>(element.getName().accept(this, arg));

        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        result.addAll(element.getValue().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PySlice element, K arg) {
        List<T> result = new ArrayList<>();

        if (element.getLower() != null) {
            result.addAll(element.getLower().accept(this, arg));
        }
        if (element.getUpper() != null) {
            result.addAll(element.getUpper().accept(this, arg));
        }
        if (element.getStep() != null) {
            result.addAll(element.getStep().accept(this, arg));
        }

        return result;
    }

    @Override
    public List<T> visit(PySubscript element, K arg) {
        List<T> result = new ArrayList<>();

        result.addAll(element.getValue().accept(this, arg));
        result.addAll(element.getSlice().accept(this, arg));

        return result;
    }

    @Override
    public List<T> visit(PyParamSpec element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyTypeVar element, K arg) {
        return element.getBound().accept(this, arg);
    }

    @Override
    public List<T> visit(PyTypeVarTuple element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyName element, K arg) {
        return new ArrayList<>();
    }

    @Override
    public List<T> visit(PyStarred element, K arg) {
        return element.getValue().accept(this, arg);
    }
}
