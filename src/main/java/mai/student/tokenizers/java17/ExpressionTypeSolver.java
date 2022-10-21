package mai.student.tokenizers.java17;

import mai.student.intermediateStates.Type;
import mai.student.tokenizers.java17.lexing.Lexeme;
import mai.student.tokenizers.java17.lexing.LexemeType;

import java.util.Collection;

public class ExpressionTypeSolver {

    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_STRING = "String";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_INTEGER = "int";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_UNDEFINED = "null";

    public Expression expression = new Expression(null);
    private Expression curSubexpression = expression;
    private boolean casting = false;

    private int depth = 0;
    public void pushOperator(Lexeme operator) {
        if (operator.getType() == LexemeType.Cast) {
            casting = true;
        }

        switch (operator.getContent()) {
            case "(":
                Expression newExp = new Expression(curSubexpression);
                curSubexpression.subExpressions.add(newExp);
                curSubexpression = newExp;
                break;
            case ")":
                if (casting) {
                    casting = false;
                    break;
                }

                if (curSubexpression.getParent() != null) {
                    curSubexpression = curSubexpression.getParent();
                }
                break;
            default:
                curSubexpression.operators.add(operator);
                break;
        }
    }

    public void pushAll(Collection<Object> parts) {
        for (Object part : parts) {
            if (part instanceof Lexeme) {
                this.pushOperator((Lexeme) part);
            } else if (part instanceof Type) {
                this.pushOperand((Type) part);
            } else {
                throw new UnsupportedOperationException("Undefined lexeme type!");
            }
        }
    }

    public void pushOperand(Type type) {
        if (curSubexpression.operators.size() > 0 && curSubexpression.operators.get(0).getType() == LexemeType.Cast &&
        curSubexpression.operands.size() == 1) {
            curSubexpression.operands.add(type);
            curSubexpression = curSubexpression.getParent();
        } else {
            curSubexpression.operands.add(type);
        }
    }

    public Type getExpressionType() {
        return reqTypeSolver(expression);
    }

    private static Type reqTypeSolver(Expression exp) {
        if (exp.operators.isEmpty()) {
            if (exp.operands.isEmpty() && exp.subExpressions.isEmpty()) {
                return null;
            }

            if (exp.operands.size() > 0) {
                return exp.operands.get(0);
            }

            if (exp.subExpressions.size() > 0) {
                return reqTypeSolver(exp.subExpressions.get(0));
            }
            throw new UnsupportedOperationException("Invalid expression!");
        }

        if (exp.operators.get(0).getType() == LexemeType.Cast) {
            return exp.operands.get(0);
        }

        for (int i = 0; i < exp.subExpressions.size(); ++i) {
            Type subExpRes = reqTypeSolver(exp.subExpressions.get(i));
            if (subExpRes != null) {
                exp.operands.add(subExpRes);
            }
        }

        boolean isBool = false;
        for (int i = 0; i < exp.operators.size(); ++i) {
            Lexeme op = exp.operators.get(i);

            if ("?".equals(op.getContent())) {
                return new Type(TYPE_UNDEFINED);
            }

            if ("==".equals(op.getContent()) || ">".equals(op.getContent()) || ">=".equals(op.getContent()) ||
                    "<".equals(op.getContent()) || "<=".equals(op.getContent()) || "!=".equals(op.getContent()) ||
                    "instanceof".equals(op.getContent()) || "&&".equals(op.getContent()) ||
                    "||".equals(op.getContent()) || "!".equals(op.getContent())) {
                isBool = true;
            }
        }

        if (isBool) {
            return new Type(TYPE_BOOLEAN);
        }

        boolean isUndefined = false, hasDouble = false, hasFloat = false, hasLong = false;
        for (int i = 0; i < exp.operands.size(); ++i) {
            if (exp.operands.get(i).equals(new Type(TYPE_STRING))){
                return new Type(TYPE_STRING);
            }
            if (exp.operands.get(i).equals(new Type(TYPE_UNDEFINED))) {
                isUndefined = true;
            }
            if (exp.operands.get(i).equals(new Type(TYPE_DOUBLE))) {
                hasDouble = true;
            }
            if (exp.operands.get(i).equals(new Type(TYPE_FLOAT))) {
                hasFloat = true;
            }
            if (exp.operands.get(i).equals(new Type(TYPE_LONG))) {
                hasLong = true;
            }
        }

        if (isUndefined) {
            return new Type(TYPE_UNDEFINED);
        }
        if (hasDouble) {
            return new Type(TYPE_DOUBLE);
        }
        if (hasFloat) {
            return new Type(TYPE_FLOAT);
        }
        if (hasLong) {
            return new Type(TYPE_LONG);
        }

        return new Type(TYPE_INTEGER);
    }

    public void print() {
        reqPrint(this.expression, 0);
    }

    private void reqPrint(Expression exp, int level) {
        for (Lexeme op : exp.operators) {
            System.out.println("\t".repeat(level) + op.getContent() + ":" + op.getType() );
        }

        for (Type type : exp.operands) {
            System.out.println("\t".repeat(level) + type.toString());
        }

        for (Expression ex : exp.subExpressions) {
            reqPrint(ex, level + 1);
        }
    }
}