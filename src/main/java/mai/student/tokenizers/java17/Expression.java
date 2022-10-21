package mai.student.tokenizers.java17;

import mai.student.intermediateStates.Type;
import mai.student.tokenizers.java17.lexing.Lexeme;

import java.util.ArrayList;

class Expression {
    private Expression parent;

    ArrayList<Lexeme> operators = new ArrayList<>();
    ArrayList<Type> operands = new ArrayList<>();
    ArrayList<Expression> subExpressions = new ArrayList<>();

    Expression(Expression parent) {
        this.parent = parent;
    }

    Expression getParent() {
        return parent;
    }
}