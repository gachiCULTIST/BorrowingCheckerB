package mai.student.tokenizers.python3.ast.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PyModule.class, name = "Module"),
        @JsonSubTypes.Type(value = PyInteractive.class, name = "Interactive"),
        @JsonSubTypes.Type(value = PyAssign.class, name = "Assign"),
        @JsonSubTypes.Type(value = PyModule.class, name = "Module"),
        @JsonSubTypes.Type(value = PyName.class, name = "Name"),
        @JsonSubTypes.Type(value = PyConstant.class, name = "Constant"),
        @JsonSubTypes.Type(value = PyExpressionContainer.class, name = "Expression"),
        @JsonSubTypes.Type(value = PyFormattedValue.class, name = "FormattedValue"),
        @JsonSubTypes.Type(value = PyJoinedStr.class, name = "JoinedStr"),
        @JsonSubTypes.Type(value = PyList.class, name = "List"),
        @JsonSubTypes.Type(value = PyTuple.class, name = "Tuple"),
        @JsonSubTypes.Type(value = PySet.class, name = "Set"),
        @JsonSubTypes.Type(value = PyStarred.class, name = "Starred"),
        @JsonSubTypes.Type(value = PyExpr.class, name = "Expr"),
        @JsonSubTypes.Type(value = PyUnaryOp.class, name = "UnaryOp"),
        @JsonSubTypes.Type(value = PyBinOp.class, name = "BinOp"),
        @JsonSubTypes.Type(value = PyBoolOp.class, name = "BoolOp"),
        @JsonSubTypes.Type(value = PyCompare.class, name = "Compare"),
        @JsonSubTypes.Type(value = PyCall.class, name = "Call"),
        @JsonSubTypes.Type(value = PyKeyword.class, name = "keyword"),
        @JsonSubTypes.Type(value = PyIfExp.class, name = "IfExp"),
        @JsonSubTypes.Type(value = PyAttribute.class, name = "Attribute"),
        @JsonSubTypes.Type(value = PyNamedExpr.class, name = "NamedExpr"),
        @JsonSubTypes.Type(value = PySubscript.class, name = "Subscript"),
        @JsonSubTypes.Type(value = PySlice.class, name = "Slice"),
        @JsonSubTypes.Type(value = PyComp.class, name = "comprehension"),
        @JsonSubTypes.Type(value = PyListComp.class, name = "ListComp"),
        @JsonSubTypes.Type(value = PySetComp.class, name = "SetComp"),
        @JsonSubTypes.Type(value = PyGeneratorExp.class, name = "GeneratorExp"),
        @JsonSubTypes.Type(value = PyDictComp.class, name = "DictComp"),
        @JsonSubTypes.Type(value = PyAnnAssign.class, name = "AnnAssign"),
        @JsonSubTypes.Type(value = PyAugAssign.class, name = "AugAssign"),
        @JsonSubTypes.Type(value = PyRaise.class, name = "Raise"),
        @JsonSubTypes.Type(value = PyAssert.class, name = "Assert"),
        @JsonSubTypes.Type(value = PyDelete.class, name = "Delete"),
        @JsonSubTypes.Type(value = PyPass.class, name = "Pass"),
        @JsonSubTypes.Type(value = PyTypeAlias.class, name = "TypeAlias"),
        @JsonSubTypes.Type(value = PyAlias.class, name = "alias"),
        @JsonSubTypes.Type(value = PyImport.class, name = "Import"),
        @JsonSubTypes.Type(value = PyImportFrom.class, name = "ImportFrom"),
        @JsonSubTypes.Type(value = PyIf.class, name = "If"),
        @JsonSubTypes.Type(value = PyFor.class, name = "For"),
        @JsonSubTypes.Type(value = PyWhile.class, name = "While"),
        @JsonSubTypes.Type(value = PyBreak.class, name = "Break"),
        @JsonSubTypes.Type(value = PyContinue.class, name = "Continue"),
        @JsonSubTypes.Type(value = PyExceptHandler.class, name = "ExceptHandler"),
        @JsonSubTypes.Type(value = PyTry.class, name = "Try"),
        @JsonSubTypes.Type(value = PyTryStar.class, name = "TryStar"),
        @JsonSubTypes.Type(value = PyWithItem.class, name = "withitem"),
        @JsonSubTypes.Type(value = PyWith.class, name = "With"),
        @JsonSubTypes.Type(value = PyMatchCase.class, name = "match_case"),
        @JsonSubTypes.Type(value = PyMatch.class, name = "Match"),
        @JsonSubTypes.Type(value = PyMatchValue.class, name = "MatchValue"),
        @JsonSubTypes.Type(value = PyMatchSingleton.class, name = "MatchSingleton"),
        @JsonSubTypes.Type(value = PyMatchSequence.class, name = "MatchSequence"),
        @JsonSubTypes.Type(value = PyMatchStar.class, name = "MatchStar"),
        @JsonSubTypes.Type(value = PyMatchMapping.class, name = "MatchMapping"),
        @JsonSubTypes.Type(value = PyMatchClass.class, name = "MatchClass"),
        @JsonSubTypes.Type(value = PyMatchAs.class, name = "MatchAs"),
        @JsonSubTypes.Type(value = PyMatchOr.class, name = "MatchOr"),
        @JsonSubTypes.Type(value = PyTypeVar.class, name = "TypeVar"),
        @JsonSubTypes.Type(value = PyParamSpec.class, name = "ParamSpec"),
        @JsonSubTypes.Type(value = PyTypeVarTuple.class, name = "TypeVarTuple"),
        @JsonSubTypes.Type(value = PyArg.class, name = "arg"),
        @JsonSubTypes.Type(value = PyArguments.class, name = "arguments"),
        @JsonSubTypes.Type(value = PyFunctionDef.class, name = "FunctionDef"),
        @JsonSubTypes.Type(value = PyLambda.class, name = "Lambda"),
        @JsonSubTypes.Type(value = PyReturn.class, name = "Return"),
        @JsonSubTypes.Type(value = PyYield.class, name = "Yield"),
        @JsonSubTypes.Type(value = PyYieldFrom.class, name = "YieldFrom"),
        @JsonSubTypes.Type(value = PyGlobal.class, name = "Global"),
        @JsonSubTypes.Type(value = PyNonlocal.class, name = "Nonlocal"),
        @JsonSubTypes.Type(value = PyClassDef.class, name = "ClassDef"),
        @JsonSubTypes.Type(value = PyAsyncFunctionDef.class, name = "AsyncFunctionDef"),
        @JsonSubTypes.Type(value = PyAwait.class, name = "Await"),
        @JsonSubTypes.Type(value = PyAsyncFor.class, name = "AsyncFor"),
        @JsonSubTypes.Type(value = PyAsyncWith.class, name = "AsyncWith")
})
@Getter
public abstract class PyNode implements PyVisitable {

    @JsonProperty("_type")
    private String type;
}
