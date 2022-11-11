package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

interface StatementProcessor {

    void run();

    // []
    void process(ArrayAccessExpr arrayAccessExpr);

    // new int[5][][]{{1}, {2,3}}
    void process(ArrayCreationExpr arrayCreationExpr);

    // {{1}, {2,3}}
    void process(ArrayInitializerExpr arrayInitializerExpr);

    // Expression = Expression
    void process(AssignExpr assignExpr);

    // Expression * Expression
    void process(BinaryExpr binaryExpr);

    // (long)
    void process(CastExpr castExpr);

    // Type.class
    void process(ClassExpr classExpr);

    // Exp ? Exp : Exp
    void process(ConditionalExpr conditionalExpr);

    // (Expression)
    void process(EnclosedExpr enclosedExpr);

    // var.field
    void process(FieldAccessExpr fieldAccessExpr);

    // var instanceOf Type castedVar
    void process(InstanceOfExpr instanceOfExpr);

    // (a, b) -> a + b
    void process(LambdaExpr lambdaExpr);

    // true
    void process(BooleanLiteralExpr booleanLiteralExpr);

    // "str"
    void process(StringLiteralExpr stringLiteralExpr);

    // 'c'
    void process(CharLiteralExpr charLiteralExpr);

    // 1.2
    void process(DoubleLiteralExpr doubleLiteralExpr);

    // 123
    void process(IntegerLiteralExpr integerLiteralExpr);

    // 123L
    void process(LongLiteralExpr longLiteralExpr);

    // """ str """
    void process(TextBlockLiteralExpr textBlockLiteralExpr);

    // null
    void process(NullLiteralExpr nullLiteralExpr);

    // Expr.method()
    void process(MethodCallExpr methodCallExpr);

    // Expr::<type1, type2>method
    void process(MethodReferenceExpr methodReferenceExpr);

    // var
    void process(NameExpr nameExpr);

    // new Type<>(){}
    void process(ObjectCreationExpr objectCreationExpr);

    // right part of full instanceOf expression
    void process(PatternExpr patternExpr);

    // Type.super
    void process(SuperExpr superExpr);

    // switch with result
    void process(SwitchExpr switchExpr);

    // Type.this
    void process(ThisExpr thisExpr);

    // left part of reference expression if (only type - Type::method)
    void process(TypeExpr typeExpr);

    // ++var
    void process(UnaryExpr unaryExpr);

    // final int a = 1, b
    void process(VariableDeclarationExpr variableDeclarationExpr);

    // assert condition : "message";
    void process(AssertStmt assertStmt);

    // {}
    void process(BlockStmt blockStmt);

    // break label;
    void process(BreakStmt breakStmt);

    // continue label;
    void process(ContinueStmt continueStmt);

    // do {} while();
    void process(DoStmt doStmt);

    // ;
    void process(EmptyStmt emptyStmt);

    // super(); or this();
    void process(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt);

    // for (Type var : iterable) {}
    void process(ForEachStmt forEachStmt);

    // for (;;) {}
    void process(ForStmt forStmt);

    // if () {} else {}
    void process(IfStmt ifStmt);

    // label: Statement
    void process(LabeledStmt labeledStmt);

    // local class
    void process(LocalClassDeclarationStmt localClassDeclarationStmt);

    // local record
    void process(LocalRecordDeclarationStmt localRecordDeclarationStmt);

    // return value;
    void process(ReturnStmt returnStmt);

    // switch () {}
    void process(SwitchStmt switchStmt);

    // synchronized () {}
    void process(SynchronizedStmt synchronizedStmt);

    // throw Expression;
    void process(ThrowStmt throwStmt);

    // try () {} catch () {} finally {}
    void process(TryStmt tryStmt);

    // unsupported operation
    void process(UnparsableStmt unparsableStmt);

    // while () {}
    void process(WhileStmt whileStmt);

    // yield Expression;
    void process(YieldStmt yieldStmt);

    // Expression wrapper
    void process(ExpressionStmt expressionStmt);

}
