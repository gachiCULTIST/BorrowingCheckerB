package mai.student.tokenizers.python3.tokenization;

import mai.student.tokenizers.python3.ast.nodes.PyNode;
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
import mai.student.tokenizers.python3.ast.nodes.roots.PyFunctionType;
import mai.student.tokenizers.python3.ast.nodes.roots.PyInteractive;
import mai.student.tokenizers.python3.ast.nodes.roots.PyModule;
import mai.student.tokenizers.python3.ast.nodes.statements.*;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySlice;
import mai.student.tokenizers.python3.ast.nodes.subscripting.PySubscript;
import mai.student.tokenizers.python3.ast.nodes.types.PyParamSpec;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeParameter;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVar;
import mai.student.tokenizers.python3.ast.nodes.types.PyTypeVarTuple;
import mai.student.tokenizers.python3.ast.nodes.variables.PyName;
import mai.student.tokenizers.python3.ast.nodes.variables.PyStarred;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyVoidVisitor;

import java.util.List;
import java.util.Map;

public class PyBasicTokenizerVisitor extends AbstractPyVoidVisitor<Object> {

    protected static final String LITERAL = "*literal*";
    protected static final String IDENTIFIER = "*ident*";
    protected static final String CLASS = "class";
    protected static final String RETURN = "return";
    protected static final String TRY = "try";
    protected static final String FINALLY = "finally";
    protected static final String CASE = "case";
    protected static final String BREAK = "break";
    protected static final String YIELD = "yield";
    protected static final String CONTINUE = "continue";
    protected static final String WHILE = "while";
    protected static final String FOR = "for";
    protected static final String IF = "if";
    protected static final String ELSE = "else";
    protected static final String ELIF = "elif";
    protected static final String ASSERT = "assert";
    protected static final String EXCEPT = "except";
    protected static final String RAISE = "raise";
    protected static final String WITH = "with";
    protected static final String AS = "as";
    protected static final String ASYNC = "async";
    protected static final String AWAIT = "await";
    protected static final String DEF = "def";
    protected static final String DEL = "del";
    protected static final String MATCH = "match";
    protected static final String FROM = "from";
    protected static final String GLOBAL = "global";
    protected static final String IMPORT = "import";
    protected static final String IN = "in";
    protected static final String IS = "is";
    protected static final String LAMBDA = "lambda";
    protected static final String NONLOCAL = "nonlocal";
    protected static final String PASS = "pass";
    protected static final String WILDCARD = "_";
    protected static final String TYPE = "type";
    protected static final String OR = "or";
    protected static final String AND = "and";
    protected static final String NOT = "not";
    protected static final String DOT = ".";
    protected static final String COMMA = ",";
    protected static final String COLON = ":";
    protected static final String SEMICOLON = ";";
    protected static final String LEFT_PAREN = "(";
    protected static final String RIGHT_PAREN = ")";
    protected static final String LEFT_BRACKET = "[";
    protected static final String RIGHT_BRACKET = "]";
    protected static final String LEFT_BRACE = "{";
    protected static final String RIGHT_BRACE = "}";
    protected static final String ASSIGN = "=";
    protected static final String GREATER = ">";
    protected static final String LOWER = "<";
    protected static final String UNION = "|";
    protected static final String INTERSECTION = "&";
    protected static final String ARROW = "->";
    protected static final String ASTERISK = "*";
    protected static final String SLASH = "/";
    protected static final String EQUAL = "=";
    protected static final String DOG = "@";
    protected static final String WALRUS = ":=";

    protected final int TOKEN_SPAN = 1000;

    protected int indexForNextElement;

    protected final Map<String, Integer> tokenDictionary;
    protected final List<Integer> result;

    public PyBasicTokenizerVisitor(Map<String, Integer> tokenDictionary, List<Integer> result) {
        this.tokenDictionary = tokenDictionary;
        this.result = result;

        this.indexForNextElement = tokenDictionary.size() + TOKEN_SPAN;
    }

    @Override
    public void visit(PyAsyncFor element, Object arg) {
        addToken(ASYNC);
        addToken(FOR);
        element.getTarget().accept(this, arg);

        addToken(IN);
        element.getIter().accept(this, arg);

        addToken(COLON);
        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyAsyncFunctionDef element, Object arg) {
        if (element.getDecoratorList() != null) {
            for (PyNode s : element.getDecoratorList()) {
                addToken(DOG);
                s.accept(this, arg);
            }
        }

        addToken(ASYNC);
        addToken(DEF);
        addToken(element.getName());

        addToken(LEFT_PAREN);
        if (element.getArgs() != null) {
            element.getArgs().accept(this, arg);
        }
        addToken(RIGHT_PAREN);

        if (element.getReturns() != null) {
            addToken(ARROW);
            element.getReturns().accept(this, arg);
        }
        addToken(COLON);

        addToken(LEFT_BRACE);
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
        addToken(RIGHT_BRACE);

        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyAsyncWith element, Object arg) {
        addToken(ASYNC);
        addToken(WITH);

        boolean isFirst = true;
        for (PyWithItem s : element.getItems()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }
        addToken(COLON);

        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyAwait element, Object arg) {
        addToken(AWAIT);
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyComp element, Object arg) {
        addToken(FOR);
        element.getTarget().accept(this, arg);

        addToken(IN);
        element.getIter().accept(this, arg);

        if (element.getIfs() != null && !element.getIfs().isEmpty()) {
            addToken(IF);
            element.getIfs().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyDictComp element, Object arg) { // {x: x**2 for x in numbers}
        addToken(LEFT_BRACE);
        element.getKey().accept(this, arg);
        addToken(COLON);
        element.getValue().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyGeneratorExp element, Object arg) { // (n**2 for n in it if n>5 if n<10)
        addToken(LEFT_PAREN);
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_PAREN);
    }

    @Override
    public void visit(PyListComp element, Object arg) { // [ord(c) for line in file for c in line]
        addToken(LEFT_BRACKET);
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void visit(PySetComp element, Object arg) { // {x for x in numbers}
        addToken(LEFT_BRACE);
        element.getElt().accept(this, arg);
        element.getGenerators().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyBreak element, Object arg) {
        addToken(BREAK);
    }

    @Override
    public void visit(PyContinue element, Object arg) {
        addToken(CONTINUE);
    }

    @Override
    public void visit(PyExceptHandler element, Object arg) { // except вставляется в try и tryStar
        if (element.getExcType() != null) {
            element.getExcType().accept(this, arg);
        }

        if (element.getName() != null) {
            addToken(AS);
            addToken(element.getName());
        }

        addToken(COLON);
        if (element.getBody() != null) {
            addToken(LEFT_BRACE);
            element.getBody().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyFor element, Object arg) {
        addToken(FOR);
        element.getTarget().accept(this, arg);

        addToken(IN);
        element.getIter().accept(this, arg);

        addToken(COLON);
        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyIf element, Object arg) {
        addToken(IF);
        element.getTest().accept(this, arg);
        addToken(COLON);

        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyTry element, Object arg) {
        addToken(TRY);
        addToken(COLON);
        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        element.getHandlers().forEach(s -> {
            addToken(EXCEPT);
            s.accept(this, arg);
        });

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }

        if (element.getFinalbody() != null && !element.getFinalbody().isEmpty()) {
            addToken(FINALLY);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getFinalbody().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyTryStar element, Object arg) {
        addToken(TRY);
        addToken(COLON);
        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        element.getHandlers().forEach(s -> {
            addToken(EXCEPT);
            addToken(ASTERISK);
            s.accept(this, arg);
        });

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }

        if (element.getFinalbody() != null && !element.getFinalbody().isEmpty()) {
            addToken(FINALLY);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getFinalbody().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyWhile element, Object arg) {
        addToken(WHILE);
        element.getTest().accept(this, arg);
        addToken(COLON);

        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        if (element.getOrelse() != null && !element.getOrelse().isEmpty()) {
            addToken(ELSE);
            addToken(COLON);
            addToken(LEFT_BRACE);
            element.getOrelse().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyWith element, Object arg) {
        addToken(WITH);

        boolean isFirst = true;
        for (PyWithItem s : element.getItems()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            s.accept(this, arg);
        }
        addToken(COLON);

        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyWithItem element, Object arg) {
        element.getContextExpr().accept(this, arg);
        if (element.getOptionalVars() != null) {
            addToken(AS);
            element.getOptionalVars().accept(this, arg);
        }
    }

    @Override
    public void visit(PyArg element, Object arg) {
        addToken(element.getArg());
        if (element.getAnnotation() != null) {
            addToken(COLON);
            element.getAnnotation().accept(this, arg);
        }
    }

    @Override
    public void visit(PyArguments element, Object arg) {
        boolean needComma = false;
        if (element.getPosonlyargs() != null && !element.getPosonlyargs().isEmpty()) {
            boolean isFirst = true;
            for (PyArg s : element.getPosonlyargs()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }

            needComma = true;
        }

        if (element.getArgs() != null && !element.getArgs().isEmpty()) {
            if (needComma) {
                addToken(COMMA);
            }

            for (int i = 0, j = 0; i < element.getArgs().size(); ++i) {
                if (i != 0) {
                    addToken(COMMA);
                }
                element.getArgs().get(i).accept(this, arg);

                if (element.getArgs().size() - i == element.getDefaults().size() - j) {
                    addToken(EQUAL);
                    element.getDefaults().get(j).accept(this, arg);
                    ++j;
                }
            }
            needComma = true;
        }

        if (element.getVararg() != null) {
            if (needComma) {
                addToken(COMMA);
            }

            element.getVararg().accept(this, arg);
            needComma = true;
        }

        if (element.getKwonlyargs() != null && !element.getKwonlyargs().isEmpty()) {
            if (needComma) {
                addToken(COMMA);
            }

            for (int i = 0; i < element.getKwonlyargs().size(); ++i) {
                element.getKwonlyargs().get(i).accept(this, arg);

                PyNode d = element.getKwDefaults().get(i);
                if (d != null) {
                    addToken(EQUAL);
                    d.accept(this, arg);
                }
            }
            needComma = true;
        }

        if (element.getKwarg() != null) {
            if (needComma) {
                addToken(COMMA);
            }

            element.getKwarg().accept(this, arg);
        }
    }

    @Override
    public void visit(PyClassDef element, Object arg) {
        if (element.getDecoratorList() != null) {
            for (PyNode s : element.getDecoratorList()) {
                addToken(DOG);
                s.accept(this, arg);
            }
        }

        addToken(CLASS);
        addToken(element.getName());

        if (element.getBases() != null || element.getKeywords() != null) {
            addToken(LEFT_PAREN);
            boolean isFirst = true;
            if (element.getBases() != null) {
                for (PyNode s : element.getBases()) {
                    if (!isFirst) {
                        addToken(COMMA);
                    }

                    isFirst = false;
                    s.accept(this, arg);
                }
            }
            if (element.getKeywords() != null) {
                for (PyKeyword s : element.getKeywords()) {
                    if (!isFirst) {
                        addToken(COMMA);
                    }

                    isFirst = false;
                    s.accept(this, arg);
                }
            }
            addToken(RIGHT_PAREN);
        }

        addToken(COLON);
        addToken(LEFT_BRACE);
        element.getBody().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);

        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyFunctionDef element, Object arg) {
        if (element.getDecoratorList() != null) {
            for (PyNode s : element.getDecoratorList()) {
                addToken(DOG);
                s.accept(this, arg);
            }
        }

        addToken(DEF);
        addToken(element.getName());

        addToken(LEFT_PAREN);
        if (element.getArgs() != null) {
            element.getArgs().accept(this, arg);
        }
        addToken(RIGHT_PAREN);

        if (element.getReturns() != null) {
            addToken(ARROW);
            element.getReturns().accept(this, arg);
        }
        addToken(COLON);

        addToken(LEFT_BRACE);
        if (element.getBody() != null) {
            element.getBody().forEach(s -> s.accept(this, arg));
        }
        addToken(RIGHT_BRACE);

        if (element.getTypeParams() != null) {
            element.getTypeParams().forEach(s -> s.accept(this, arg));
        }
    }

    @Override
    public void visit(PyGlobal element, Object arg) {
        addToken(GLOBAL);
        boolean isFirst = true;
        for (String s : element.getNames()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            addToken(s);
        }
    }

    @Override
    public void visit(PyLambda element, Object arg) {
        addToken(LAMBDA);

        boolean isFirst = true;
        for (PyArg s : element.getArgs().getArgs()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }

        addToken(COLON);
        element.getBody().accept(this, arg);
    }

    @Override
    public void visit(PyNonlocal element, Object arg) {
        addToken(NONLOCAL);
        boolean isFirst = true;
        for (String s : element.getNames()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            addToken(s);
        }
    }

    @Override
    public void visit(PyReturn element, Object arg) {
        addToken(RETURN);
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyYield element, Object arg) {
        addToken(YIELD);
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyYieldFrom element, Object arg) {
        addToken(YIELD);
        addToken(FROM);
        if (element.getValue() != null) {
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAttribute element, Object arg) {
        element.getValue().accept(this, arg);
        addToken(DOT);
        addToken(element.getAttr());
    }

    @Override
    public void visit(PyBinOp element, Object arg) {
        element.getLeft().accept(this, arg);
        element.getOp().getSelfOps().getOps().forEach(this::addToken);
        element.getRight().accept(this, arg);
    }

    @Override
    public void visit(PyBoolOp element, Object arg) {
        boolean isFirst = true;
        for (PyNode s : element.getValues()) {
            if (!isFirst) {
                element.getOp().getSelfOps().getOps().forEach(this::addToken);
            }

            isFirst = false;
            s.accept(this, arg);
        }
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
    }

    @Override
    public void visit(PyCompare element, Object arg) {
        element.getLeft().accept(this, arg);
        for (int i = 0; i < element.getOps().size(); ++i) {
            element.getOps().get(i).getSelfOps().getOps().forEach(this::addToken);
            element.getComparators().get(i).accept(this, arg);
        }
    }

    @Override
    public void visit(PyIfExp element, Object arg) {
        element.getBody().accept(this, arg);
        addToken(IF);
        element.getTest().accept(this, arg);
        addToken(ELSE);
        element.getOrelse().accept(this, arg);
    }

    @Override
    public void visit(PyKeyword element, Object arg) {
        if (element.getArg() == null) {
            addToken(ASTERISK);
            addToken(ASTERISK);
            element.getValue().accept(this, arg);
        } else {
            addToken(element.getArg());
            addToken(EQUAL);
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyNamedExpr element, Object arg) {
        addToken(LEFT_PAREN);
        element.getTarget().accept(this, arg);
        addToken(WALRUS);
        element.getValue().accept(this, arg);
        addToken(RIGHT_PAREN);
    }

    @Override
    public void visit(PyUnaryOp element, Object arg) {
        element.getOp().getSelfOps().getOps().forEach(this::addToken);
        element.getOperand().accept(this, arg);
    }

    @Override
    public void visit(PyAlias element, Object arg) {
        addToken(element.getName());
        if (element.getAsname() != null) {
            addToken(AS);
            addToken(element.getAsname());
        }
    }

    @Override
    public void visit(PyImport element, Object arg) {
        addToken(IMPORT);
        boolean isFirst = true;
        for (PyAlias s : element.getNames()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(PyImportFrom element, Object arg) {
        addToken(IMPORT);
        addToken(element.getModule());
        addToken(FROM);

        boolean isFirst = true;
        for (PyAlias s : element.getNames()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(PyConstant element, Object arg) {
        addToken(LITERAL);
    }

    @Override
    public void visit(PyDict element, Object arg) {
        addToken(LEFT_BRACE);
        for (int i = 0; i < element.getKeys().size(); ++i) {
            if (i > 0) {
                addToken(COMMA);
            }

            PyNode key = element.getKeys().get(i);
            if (key == null) {
                addToken(ASTERISK);
                addToken(ASTERISK);
            } else {
                key.accept(this, arg);
                addToken(EQUAL);
            }
            element.getValues().get(i).accept(this, arg);
        }
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyJoinedStr element, Object arg) {
        addToken(LITERAL);
    }

    @Override
    public void visit(PyList element, Object arg) {
        addToken(LEFT_BRACKET);
        if (element.getElts() != null) {
            boolean isFirst = true;
            for (PyNode s : element.getElts()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        }
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void visit(PySet element, Object arg) {
        addToken(LEFT_BRACE);
        if (element.getElts() != null) {
            boolean isFirst = true;
            for (PyNode s : element.getElts()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        }
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyTuple element, Object arg) {
        addToken(LEFT_PAREN);
        if (element.getElts() != null) {
            boolean isFirst = true;
            for (PyNode s : element.getElts()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        }
        addToken(RIGHT_PAREN);
    }

    @Override
    public void visit(PyMatch element, Object arg) {
        addToken(MATCH);
        element.getSubject().accept(this, arg);
        addToken(COLON);

        addToken(LEFT_BRACE);
        element.getCases().forEach(s -> s.accept(this, arg));
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyMatchAs element, Object arg) {
        if (element.getName() == null) {
            addToken(WILDCARD);
            return;
        }

        element.getPattern().accept(this, arg);
        addToken(AS);
        addToken(element.getName());
    }

    @Override
    public void visit(PyMatchCase element, Object arg) {
        addToken(CASE);
        element.getPattern().accept(this, arg);

        if (element.getGuard() != null) {
            addToken(IF);
            element.getGuard().accept(this, arg);
        }

        addToken(COLON);
        if (element.getBody() != null) {
            addToken(LEFT_BRACE);
            element.getBody().forEach(s -> s.accept(this, arg));
            addToken(RIGHT_BRACE);
        }
    }

    @Override
    public void visit(PyMatchClass element, Object arg) {
        element.getCls().accept(this, arg);

        addToken(LEFT_PAREN);
        boolean needComma = false;
        if (element.getPatterns() != null && !element.getPatterns().isEmpty()) {
            boolean isFirst = true;
            for (PyMatching s : element.getPatterns()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }

            needComma = true;
        }
        if (element.getKwdPatterns() != null && !element.getKwdPatterns().isEmpty()) {
            if (needComma) {
                addToken(COMMA);
            }

            for (int i = 0; i < element.getKwdAttrs().size(); ++i) {
                if (i > 0) {
                    addToken(COMMA);
                }

                addToken(element.getKwdAttrs().get(i));
                addToken(EQUAL);
                element.getKwdPatterns().get(i).accept(this, arg);
            }
        }
        addToken(RIGHT_PAREN);
    }

    @Override
    public void visit(PyMatchMapping element, Object arg) {
        addToken(LEFT_BRACE);
        boolean needComma = false;
        if (element.getKeys() != null && !element.getKeys().isEmpty()) {
            for (int i = 0; i < element.getKeys().size(); ++i) {
                if (i > 0) {
                    addToken(COMMA);
                }

                element.getKeys().get(i).accept(this, arg);
                addToken(COLON);
                element.getPatterns().get(i).accept(this, arg);
            }

            needComma = true;
        }

        if (element.getRest() != null) {
            if (needComma) {
                addToken(COMMA);
            }

            addToken(ASTERISK);
            addToken(ASTERISK);
            addToken(element.getRest());
        }
        addToken(RIGHT_BRACE);
    }

    @Override
    public void visit(PyMatchOr element, Object arg) {
        boolean isFirst = true;
        for (PyMatching s : element.getPatterns()) {
            if (!isFirst) {
                addToken(UNION);
            }

            isFirst = false;
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(PyMatchSequence element, Object arg) {
        addToken(LEFT_BRACKET);
        boolean isFirst = true;
        for (PyMatching s : element.getPatterns()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void visit(PyMatchSingleton element, Object arg) {
        addToken(element.getValue());
    }

    @Override
    public void visit(PyMatchStar element, Object arg) {
        addToken(ASTERISK);
        if (element.getName() != null) {
            addToken(element.getName());
        } else {
            addToken(WILDCARD);
        }
    }

    @Override
    public void visit(PyFunctionType element, Object arg) {
    }

    @Override
    public void visit(PyInteractive element, Object arg) {
        boolean isFirst = true;
        for (PyNode s : element.getBody()) {
            if (!isFirst) {
                addToken(SEMICOLON);
            }

            isFirst = false;
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(PyModule element, Object arg) {
        element.getBody().forEach(s -> s.accept(this, arg));
    }

    @Override
    public void visit(PyAnnAssign element, Object arg) {
        element.getTarget().accept(this, arg);
        if (element.getAnnotation() != null) {
            addToken(COLON);
            element.getAnnotation().accept(this, arg);
        }
        if (element.getValue() != null) {
            addToken(EQUAL);
            element.getValue().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAssert element, Object arg) {
        addToken(ASSERT);
        element.getTest().accept(this, arg);

        if (element.getMsg() != null) {
            addToken(COMMA);
            element.getMsg().accept(this, arg);
        }
    }

    @Override
    public void visit(PyAssign element, Object arg) {
        for (PyNode s : element.getTargets()) {
            s.accept(this, arg);
            addToken(EQUAL);
        }
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyAugAssign element, Object arg) {
        element.getTarget().accept(this, arg);
        element.getOp().getSelfOps().getOps().forEach(this::addToken);
        addToken(EQUAL);
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PyDelete element, Object arg) {
        addToken(DEL);

        boolean isFirst = true;
        for (PyNode s : element.getTargets()) {
            if (!isFirst) {
                addToken(COMMA);
            }

            isFirst = false;
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(PyPass element, Object arg) {
        addToken(PASS);
    }

    @Override
    public void visit(PyRaise element, Object arg) {
        addToken(RAISE);
        if (element.getExc() != null) {
            element.getExc().accept(this, arg);
            if (element.getCause() != null) {
                addToken(FROM);
                element.getCause().accept(this, arg);
            }
        }
    }

    @Override
    public void visit(PyTypeAlias element, Object arg) {
        addToken(TYPE);
        element.getName().accept(this, arg);

        if (element.getTypeParams() != null) {
            addToken(LEFT_BRACKET);
            boolean isFirst = true;
            for (PyTypeParameter s : element.getTypeParams()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
            addToken(RIGHT_BRACKET);
        }

        addToken(EQUAL);
        element.getValue().accept(this, arg);
    }

    @Override
    public void visit(PySlice element, Object arg) {
        if (element.getLower() != null) {
            element.getLower().accept(this, arg);
        }
        addToken(COLON);

        if (element.getUpper() != null) {
            element.getUpper().accept(this, arg);
        }

        if (element.getStep() != null) {
            addToken(COLON);
            element.getStep().accept(this, arg);
        }
    }

    @Override
    public void visit(PySubscript element, Object arg) {
        element.getValue().accept(this, arg);
        addToken(LEFT_BRACKET);
        if (element.getSlice() instanceof PyTuple) {
            boolean isFirst = true;
            for (PyNode s : ((PyTuple) element.getSlice()).getElts()) {
                if (!isFirst) {
                    addToken(COMMA);
                }

                isFirst = false;
                s.accept(this, arg);
            }
        } else {
            element.getSlice().accept(this, arg);
        }
        addToken(RIGHT_BRACKET);
    }

    @Override
    public void visit(PyParamSpec element, Object arg) {
        addToken(ASTERISK);
        addToken(ASTERISK);
        addToken(element.getName());
    }

    @Override
    public void visit(PyTypeVar element, Object arg) {
        addToken(element.getName());
        addToken(COLON);
        element.getBound().accept(this, arg);
    }

    @Override
    public void visit(PyTypeVarTuple element, Object arg) {
        addToken(ASTERISK);
        addToken(element.getName());
    }

    @Override
    public void visit(PyName element, Object arg) {
        addToken(element.getId());
    }

    @Override
    public void visit(PyStarred element, Object arg) {
        addToken(ASTERISK);
        element.getValue().accept(this, arg);
    }

    protected void addToken(String lexeme) {
        if (!tokenDictionary.containsKey(lexeme)) {
            tokenDictionary.put(lexeme, indexForNextElement);
            ++indexForNextElement;
        }

        result.add(tokenDictionary.get(lexeme));
    }
}
