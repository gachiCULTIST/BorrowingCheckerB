package mai.student.tokenizers.java17;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import mai.student.intermediateStates.DefinedFunction;
import mai.student.intermediateStates.FileRepresentative;

import java.util.List;
import java.util.Map;

// Базовый токенизатор
// Просто вставляет элемент в представление при этом,
// если конкретного элемента нет в словаре токенов,
// то он туда добавляется с генерируемым идентификатором
public class BasicLexemeProcessor implements LexemeProcessor {

    private static final int START_INDEX = 1000;

    private static int indexForNextElement = START_INDEX;

    private final Map<String, Integer> tokenDictionary;

    private final DefinedFunction function;

    private final List<FileRepresentative> files;

    // Вообще визитор используется для определения типа Expression,
    // по скольку все элементы составных выражений представляет данный тип
    private final VoidVisitor<LexemeProcessor> visitor;


    public BasicLexemeProcessor(Map<String, Integer> tokenDictionary, DefinedFunction function,
                                List<FileRepresentative> files, VoidVisitor<LexemeProcessor> visitor) {
        // TODO: throw exception f someone == null

        this.tokenDictionary = tokenDictionary;
        this.function = function;
        this.files = files;
        this.visitor = visitor;
    }

    @Override
    public void process(ArrayAccessExpr arrayAccessExpr) {

    }

    @Override
    public void process(ArrayCreationExpr arrayCreationExpr) {

    }

    @Override
    public void process(ArrayInitializerExpr arrayInitializerExpr) {

    }

    @Override
    public void process(AssignExpr assignExpr) {

    }

    @Override
    public void process(BinaryExpr binaryExpr) {

    }

    @Override
    public void process(CastExpr castExpr) {

    }

    @Override
    public void process(ClassExpr classExpr) {

    }

    @Override
    public void process(ConditionalExpr conditionalExpr) {

    }

    @Override
    public void process(EnclosedExpr enclosedExpr) {

    }

    @Override
    public void process(FieldAccessExpr fieldAccessExpr) {

    }

    @Override
    public void process(InstanceOfExpr instanceOfExpr) {

    }

    @Override
    public void process(LambdaExpr lambdaExpr) {

    }

    @Override
    public void process(BooleanLiteralExpr booleanLiteralExpr) {

    }

    @Override
    public void process(StringLiteralExpr stringLiteralExpr) {

    }

    @Override
    public void process(CharLiteralExpr charLiteralExpr) {

    }

    @Override
    public void process(DoubleLiteralExpr doubleLiteralExpr) {

    }

    @Override
    public void process(IntegerLiteralExpr integerLiteralExpr) {

    }

    @Override
    public void process(LongLiteralExpr longLiteralExpr) {

    }

    @Override
    public void process(TextBlockLiteralExpr textBlockLiteralExpr) {

    }

    @Override
    public void process(NullLiteralExpr nullLiteralExpr) {

    }

    @Override
    public void process(MethodCallExpr methodCallExpr) {

    }

    @Override
    public void process(MethodReferenceExpr methodReferenceExpr) {

    }

    @Override
    public void process(NameExpr nameExpr) {

    }

    @Override
    public void process(ObjectCreationExpr objectCreationExpr) {

    }

    @Override
    public void process(PatternExpr patternExpr) {

    }

    @Override
    public void process(SuperExpr superExpr) {

    }

    @Override
    public void process(SwitchExpr switchExpr) {

    }

    @Override
    public void process(ThisExpr thisExpr) {

    }

    @Override
    public void process(TypeExpr typeExpr) {

    }

    @Override
    public void process(UnaryExpr unaryExpr) {

    }

    @Override
    public void process(VariableDeclarationExpr variableDeclarationExpr) {

    }

    @Override
    public void process(AssertStmt assertStmt) {

    }

    @Override
    public void process(BlockStmt blockStmt) {

    }

    @Override
    public void process(BreakStmt breakStmt) {

    }

    @Override
    public void process(ContinueStmt continueStmt) {

    }

    @Override
    public void process(DoStmt doStmt) {

    }

    @Override
    public void process(EmptyStmt emptyStmt) {

    }

    @Override
    public void process(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt) {

    }

    @Override
    public void process(ForEachStmt forEachStmt) {

    }

    @Override
    public void process(ForStmt forStmt) {

    }

    @Override
    public void process(IfStmt ifStmt) {

    }

    @Override
    public void process(LabeledStmt labeledStmt) {

    }

    @Override
    public void process(LocalClassDeclarationStmt localClassDeclarationStmt) {

    }

    @Override
    public void process(LocalRecordDeclarationStmt localRecordDeclarationStmt) {

    }

    @Override
    public void process(ReturnStmt returnStmt) {

    }

    @Override
    public void process(SwitchStmt switchStmt) {

    }

    @Override
    public void process(SynchronizedStmt synchronizedStmt) {

    }

    @Override
    public void process(ThrowStmt throwStmt) {

    }

    @Override
    public void process(TryStmt tryStmt) {

    }

    @Override
    public void process(UnparsableStmt unparsableStmt) {

    }

    @Override
    public void process(WhileStmt whileStmt) {

    }

    @Override
    public void process(YieldStmt yieldStmt) {

    }
}
