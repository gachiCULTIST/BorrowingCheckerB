package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TokenizerVisitor extends VoidVisitorAdapter<LexemeProcessor> {

    // В реализации процессора будет будет экземпляр визитора
    // визитор просто вызывает соотвутствующий метод обработки
    // это позволяет реализации процессора самому решать идти вглубь по поддереву или нет

    //TODO: transer to abstract implementation
    // Вообще визитор используется для определения типа Expression,
    // по скольку все элементы составных выражений представля.т данный тип

    // []
    @Override
    public void visit(ArrayAccessExpr arrayAccessExpr, LexemeProcessor processor) {
        processor.process(arrayAccessExpr);
    }

    // new int[5][][]{{1}, {2,3}}
    @Override
    public void visit(ArrayCreationExpr arrayCreationExpr, LexemeProcessor processor) {
         processor.process(arrayCreationExpr);
    }

    // {{1}, {2,3}}
    @Override
    public void visit(ArrayInitializerExpr arrayInitializerExpr, LexemeProcessor processor) {
        processor.process(arrayInitializerExpr);
    }

    // Expression = Expression
    @Override
    public void visit(AssignExpr assignExpr, LexemeProcessor processor) {
        processor.process(assignExpr);
    }

    // Expression * Expression
    @Override
    public void visit(BinaryExpr binaryExpr, LexemeProcessor processor) {
        processor.process(binaryExpr);
    }

    // (long)
    @Override
    public void visit(CastExpr castExpr, LexemeProcessor processor) {
        processor.process(castExpr);
    }

    // Type.class
    @Override
    public void visit(ClassExpr classExpr, LexemeProcessor processor) {
        processor.process(classExpr);
    }

    // Exp ? Exp : Exp
    @Override
    public void visit(ConditionalExpr conditionalExpr, LexemeProcessor processor) {
        processor.process(conditionalExpr);
    }

    // (Expression)
    @Override
    public void visit(EnclosedExpr enclosedExpr, LexemeProcessor processor) {
        processor.process(enclosedExpr);
    }

    // var.field
    @Override
    public void visit(FieldAccessExpr fieldAccessExpr, LexemeProcessor processor) {
        processor.process(fieldAccessExpr);
    }

    // var instanceOf Type castedVar
    @Override
    public void visit(InstanceOfExpr instanceOfExpr, LexemeProcessor processor) {
        processor.process(instanceOfExpr);
    }

    // (a, b) -> a + b
    @Override
    public void visit(LambdaExpr lambdaExpr, LexemeProcessor processor) {
        processor.process(lambdaExpr);
    }

    // true
    @Override
    public void visit(BooleanLiteralExpr booleanLiteralExpr, LexemeProcessor processor) {
        processor.process(booleanLiteralExpr);
    }

    // "str"
    @Override
    public void visit(StringLiteralExpr stringLiteralExpr, LexemeProcessor processor) {
        processor.process(stringLiteralExpr);
    }

    // 'c'
    @Override
    public void visit(CharLiteralExpr charLiteralExpr, LexemeProcessor processor) {
        processor.process(charLiteralExpr);
    }

    // 1.2
    @Override
    public void visit(DoubleLiteralExpr doubleLiteralExpr, LexemeProcessor processor) {
        processor.process(doubleLiteralExpr);
    }

    // 123
    @Override
    public void visit(IntegerLiteralExpr integerLiteralExpr, LexemeProcessor processor) {
        processor.process(integerLiteralExpr);
    }

    // 123L
    @Override
    public void visit(LongLiteralExpr longLiteralExpr, LexemeProcessor processor) {
        processor.process(longLiteralExpr);
    }

    // """ str """
    @Override
    public void visit(TextBlockLiteralExpr textBlockLiteralExpr, LexemeProcessor processor) {
        processor.process(textBlockLiteralExpr);
    }

    // null
    @Override
    public void visit(NullLiteralExpr nullLiteralExpr, LexemeProcessor processor) {
        processor.process(nullLiteralExpr);
    }

    // Expr.method()
    @Override
    public void visit(MethodCallExpr methodCallExpr, LexemeProcessor processor) {
        processor.process(methodCallExpr);
    }

    // Expr::method
    @Override
    public void visit(MethodReferenceExpr methodReferenceExpr, LexemeProcessor processor) {
        processor.process(methodReferenceExpr);
    }

    // var
    @Override
    public void visit(NameExpr nameExpr, LexemeProcessor processor) {
        processor.process(nameExpr);
    }

    // new Type<>(){}
    @Override
    public void visit(ObjectCreationExpr objectCreationExpr, LexemeProcessor processor) {
        processor.process(objectCreationExpr);
    }

    // right part of full instanceOf expression
    @Override
    public void visit(PatternExpr patternExpr, LexemeProcessor processor) {
        processor.process(patternExpr);
    }

    // Type.super
    @Override
    public void visit(SuperExpr superExpr, LexemeProcessor processor) {
        processor.process(superExpr);
    }

    // switch with result
    @Override
    public void visit(SwitchExpr switchExpr, LexemeProcessor processor) {
        processor.process(switchExpr);
    }

    // Type.this
    @Override
    public void visit(ThisExpr thisExpr, LexemeProcessor processor) {
        processor.process(thisExpr);
    }

    // left part of reference expression if (only type - Type::method)
    @Override
    public void visit(TypeExpr typeExpr, LexemeProcessor processor) {
        processor.process(typeExpr);
    }

    // ++var
    @Override
    public void visit(UnaryExpr unaryExpr, LexemeProcessor processor) {
        processor.process(unaryExpr);
    }

    // final int a = 1, b
    @Override
    public void visit(VariableDeclarationExpr variableDeclarationExpr, LexemeProcessor processor) {
        processor.process(variableDeclarationExpr);
    }

    // assert condition : "message";
    @Override
    public void visit(AssertStmt assertStmt, LexemeProcessor processor) {
        processor.process(assertStmt);
    }

    // {}
    @Override
    public void visit(BlockStmt blockStmt, LexemeProcessor processor) {
        processor.process(blockStmt);
    }

    // break label;
    @Override
    public void visit(BreakStmt breakStmt, LexemeProcessor processor) {
        processor.process(breakStmt);
    }

    // continue label;
    @Override
    public void visit(ContinueStmt continueStmt, LexemeProcessor processor) {
        processor.process(continueStmt);
    }

    // do {} while();
    @Override
    public void visit(DoStmt doStmt, LexemeProcessor processor) {
        processor.process(doStmt);
    }

    // ;
    @Override
    public void visit(EmptyStmt emptyStmt, LexemeProcessor processor) {
        processor.process(emptyStmt);
    }

    // super(); or this();
    @Override
    public void visit(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt, LexemeProcessor processor) {
        processor.process(explicitConstructorInvocationStmt);
    }

    // for (Type var : iterable) {}
    @Override
    public void visit(ForEachStmt forEachStmt, LexemeProcessor processor) {
        processor.process(forEachStmt);
    }

    // for (;;) {}
    @Override
    public void visit(ForStmt forStmt, LexemeProcessor processor) {
        processor.process(forStmt);
    }

    // if () {} else {}
    @Override
    public void visit(IfStmt ifStmt, LexemeProcessor processor) {
        processor.process(ifStmt);
    }

    // label: Statement
    @Override
    public void visit(LabeledStmt labeledStmt, LexemeProcessor processor) {
        processor.process(labeledStmt);
    }

    // local class
    @Override
    public void visit(LocalClassDeclarationStmt localClassDeclarationStmt, LexemeProcessor processor) {
        processor.process(localClassDeclarationStmt);
    }

    // local record
    @Override
    public void visit(LocalRecordDeclarationStmt localRecordDeclarationStmt, LexemeProcessor processor) {
        processor.process(localRecordDeclarationStmt);
    }

    // return value;
    @Override
    public void visit(ReturnStmt returnStmt, LexemeProcessor processor) {
        processor.process(returnStmt);
    }

    // switch () {}
    @Override
    public void visit(SwitchStmt switchStmt, LexemeProcessor processor) {
        processor.process(switchStmt);
    }

    // synchronized () {}
    @Override
    public void visit(SynchronizedStmt synchronizedStmt, LexemeProcessor processor) {
        processor.process(synchronizedStmt);
    }

    // throw Expression;
    @Override
    public void visit(ThrowStmt throwStmt, LexemeProcessor processor) {
        processor.process(throwStmt);
    }

    // try () {} catch () {} finally {}
    @Override
    public void visit(TryStmt tryStmt, LexemeProcessor processor) {
        processor.process(tryStmt);
    }

    // unsupported operation
    @Override
    public void visit(UnparsableStmt unparsableStmt, LexemeProcessor processor) {
        processor.process(unparsableStmt);
    }

    // while () {}
    @Override
    public void visit(WhileStmt whileStmt, LexemeProcessor processor) {
        processor.process(whileStmt);
    }

    // yield Expression;
    @Override
    public void visit(YieldStmt yieldStmt, LexemeProcessor processor) {
        processor.process(yieldStmt);
    }
}
