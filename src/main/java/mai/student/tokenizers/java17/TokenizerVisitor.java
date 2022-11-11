package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TokenizerVisitor extends VoidVisitorAdapter<StatementProcessor> {

    // В реализации процессора будет экземпляр визитора
    // визитор просто вызывает соответствующий метод обработки
    // это позволяет реализации процессора самому решать идти вглубь по поддереву или нет

    // []
    @Override
    public void visit(ArrayAccessExpr arrayAccessExpr, StatementProcessor processor) {
        processor.process(arrayAccessExpr);
    }

    // new int[5][][]{{1}, {2,3}}
    @Override
    public void visit(ArrayCreationExpr arrayCreationExpr, StatementProcessor processor) {
         processor.process(arrayCreationExpr);
    }

    // {{1}, {2,3}}
    @Override
    public void visit(ArrayInitializerExpr arrayInitializerExpr, StatementProcessor processor) {
        processor.process(arrayInitializerExpr);
    }

    // Expression = Expression
    @Override
    public void visit(AssignExpr assignExpr, StatementProcessor processor) {
        processor.process(assignExpr);
    }

    // Expression * Expression
    @Override
    public void visit(BinaryExpr binaryExpr, StatementProcessor processor) {
        processor.process(binaryExpr);
    }

    // (long)
    @Override
    public void visit(CastExpr castExpr, StatementProcessor processor) {
        processor.process(castExpr);
    }

    // Type.class
    @Override
    public void visit(ClassExpr classExpr, StatementProcessor processor) {
        processor.process(classExpr);
    }

    // Exp ? Exp : Exp
    @Override
    public void visit(ConditionalExpr conditionalExpr, StatementProcessor processor) {
        processor.process(conditionalExpr);
    }

    // (Expression)
    @Override
    public void visit(EnclosedExpr enclosedExpr, StatementProcessor processor) {
        processor.process(enclosedExpr);
    }

    // var.field
    @Override
    public void visit(FieldAccessExpr fieldAccessExpr, StatementProcessor processor) {
        processor.process(fieldAccessExpr);
    }

    // var instanceOf Type castedVar
    @Override
    public void visit(InstanceOfExpr instanceOfExpr, StatementProcessor processor) {
        processor.process(instanceOfExpr);
    }

    // (a, b) -> a + b
    @Override
    public void visit(LambdaExpr lambdaExpr, StatementProcessor processor) {
        processor.process(lambdaExpr);
    }

    // true
    @Override
    public void visit(BooleanLiteralExpr booleanLiteralExpr, StatementProcessor processor) {
        processor.process(booleanLiteralExpr);
    }

    // "str"
    @Override
    public void visit(StringLiteralExpr stringLiteralExpr, StatementProcessor processor) {
        processor.process(stringLiteralExpr);
    }

    // 'c'
    @Override
    public void visit(CharLiteralExpr charLiteralExpr, StatementProcessor processor) {
        processor.process(charLiteralExpr);
    }

    // 1.2
    @Override
    public void visit(DoubleLiteralExpr doubleLiteralExpr, StatementProcessor processor) {
        processor.process(doubleLiteralExpr);
    }

    // 123
    @Override
    public void visit(IntegerLiteralExpr integerLiteralExpr, StatementProcessor processor) {
        processor.process(integerLiteralExpr);
    }

    // 123L
    @Override
    public void visit(LongLiteralExpr longLiteralExpr, StatementProcessor processor) {
        processor.process(longLiteralExpr);
    }

    // """ str """
    @Override
    public void visit(TextBlockLiteralExpr textBlockLiteralExpr, StatementProcessor processor) {
        processor.process(textBlockLiteralExpr);
    }

    // null
    @Override
    public void visit(NullLiteralExpr nullLiteralExpr, StatementProcessor processor) {
        processor.process(nullLiteralExpr);
    }

    // Expr.method()
    @Override
    public void visit(MethodCallExpr methodCallExpr, StatementProcessor processor) {
        processor.process(methodCallExpr);
    }

    // Expr::method
    @Override
    public void visit(MethodReferenceExpr methodReferenceExpr, StatementProcessor processor) {
        processor.process(methodReferenceExpr);
    }

    // var
    @Override
    public void visit(NameExpr nameExpr, StatementProcessor processor) {
        processor.process(nameExpr);
    }

    // new Type<>(){}
    @Override
    public void visit(ObjectCreationExpr objectCreationExpr, StatementProcessor processor) {
        processor.process(objectCreationExpr);
    }

    // right part of full instanceOf expression
    @Override
    public void visit(PatternExpr patternExpr, StatementProcessor processor) {
        processor.process(patternExpr);
    }

    // Type.super
    @Override
    public void visit(SuperExpr superExpr, StatementProcessor processor) {
        processor.process(superExpr);
    }

    // switch with result
    @Override
    public void visit(SwitchExpr switchExpr, StatementProcessor processor) {
        processor.process(switchExpr);
    }

    // Type.this
    @Override
    public void visit(ThisExpr thisExpr, StatementProcessor processor) {
        processor.process(thisExpr);
    }

    // left part of reference expression if (only type - Type::method)
    @Override
    public void visit(TypeExpr typeExpr, StatementProcessor processor) {
        processor.process(typeExpr);
    }

    // ++var
    @Override
    public void visit(UnaryExpr unaryExpr, StatementProcessor processor) {
        processor.process(unaryExpr);
    }

    // final int a = 1, b
    @Override
    public void visit(VariableDeclarationExpr variableDeclarationExpr, StatementProcessor processor) {
        processor.process(variableDeclarationExpr);
    }

    // assert condition : "message";
    @Override
    public void visit(AssertStmt assertStmt, StatementProcessor processor) {
        processor.process(assertStmt);
    }

    // {}
    @Override
    public void visit(BlockStmt blockStmt, StatementProcessor processor) {
        processor.process(blockStmt);
    }

    // break label;
    @Override
    public void visit(BreakStmt breakStmt, StatementProcessor processor) {
        processor.process(breakStmt);
    }

    // continue label;
    @Override
    public void visit(ContinueStmt continueStmt, StatementProcessor processor) {
        processor.process(continueStmt);
    }

    // do {} while();
    @Override
    public void visit(DoStmt doStmt, StatementProcessor processor) {
        processor.process(doStmt);
    }

    // ;
    @Override
    public void visit(EmptyStmt emptyStmt, StatementProcessor processor) {
        processor.process(emptyStmt);
    }

    // super(); or this();
    @Override
    public void visit(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt, StatementProcessor processor) {
        processor.process(explicitConstructorInvocationStmt);
    }

    // for (Type var : iterable) {}
    @Override
    public void visit(ForEachStmt forEachStmt, StatementProcessor processor) {
        processor.process(forEachStmt);
    }

    // for (;;) {}
    @Override
    public void visit(ForStmt forStmt, StatementProcessor processor) {
        processor.process(forStmt);
    }

    // if () {} else {}
    @Override
    public void visit(IfStmt ifStmt, StatementProcessor processor) {
        processor.process(ifStmt);
    }

    // label: Statement
    @Override
    public void visit(LabeledStmt labeledStmt, StatementProcessor processor) {
        processor.process(labeledStmt);
    }

    // local class
    @Override
    public void visit(LocalClassDeclarationStmt localClassDeclarationStmt, StatementProcessor processor) {
        processor.process(localClassDeclarationStmt);
    }

    // local record
    @Override
    public void visit(LocalRecordDeclarationStmt localRecordDeclarationStmt, StatementProcessor processor) {
        processor.process(localRecordDeclarationStmt);
    }

    // return value;
    @Override
    public void visit(ReturnStmt returnStmt, StatementProcessor processor) {
        processor.process(returnStmt);
    }

    // switch () {}
    @Override
    public void visit(SwitchStmt switchStmt, StatementProcessor processor) {
        processor.process(switchStmt);
    }

    // synchronized () {}
    @Override
    public void visit(SynchronizedStmt synchronizedStmt, StatementProcessor processor) {
        processor.process(synchronizedStmt);
    }

    // throw Expression;
    @Override
    public void visit(ThrowStmt throwStmt, StatementProcessor processor) {
        processor.process(throwStmt);
    }

    // try () {} catch () {} finally {}
    @Override
    public void visit(TryStmt tryStmt, StatementProcessor processor) {
        processor.process(tryStmt);
    }

    // unsupported operation
    @Override
    public void visit(UnparsableStmt unparsableStmt, StatementProcessor processor) {
        processor.process(unparsableStmt);
    }

    // while () {}
    @Override
    public void visit(WhileStmt whileStmt, StatementProcessor processor) {
        processor.process(whileStmt);
    }

    // yield Expression;
    @Override
    public void visit(YieldStmt yieldStmt, StatementProcessor processor) {
        processor.process(yieldStmt);
    }
}
