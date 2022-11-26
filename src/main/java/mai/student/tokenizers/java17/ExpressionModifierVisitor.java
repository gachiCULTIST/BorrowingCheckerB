package mai.student.tokenizers.java17;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import mai.student.intermediateStates.*;
import mai.student.tokenizers.java17.preprocessing.AnalysisVisitor;
import org.checkerframework.checker.units.qual.A;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpressionModifierVisitor extends VoidVisitorAdapter<Void> {

    // TODO: test
    public static Clock clock = Clock.systemDefaultZone();
    public static long test1 = 0;
    public static long test2 = 0;

    private static final String TYPE_ARRAY = "Array";

    private List<FileRepresentative> files;

    private IStructure scope;

    public ExpressionModifierVisitor(List<FileRepresentative> files, IStructure scope) {
        this.files = files;
        this.scope = scope;
    }

    @Override
    public void visit(NameExpr nameExpr, Void arg) {
        IStructure variable = IStructure.findEntity(files, scope, nameExpr.getNameAsString(), false, null);

        if (variable != null && variable.getStrucType() == StructureType.Variable) {
            long start = clock.millis();
            VariableOrConst var = (VariableOrConst) variable;
            // TODO: rework field and method parameters types
            var.actuateTypes((ArrayList<FileRepresentative>) files);
            test1 += clock.millis() - start;

            if (var.getReplacer() != null) {
                start = clock.millis();
                nameExpr.replace(var.getReplacer());
                test2 += clock.millis() - start;
            }
        }

//        nameExpr.replace(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, "ChildNextClass"), new NodeList<>()));
//        nameExpr.replace(StaticJavaParser.parseExpression("new ChildNextClass<Double>()"));
//        System.out.println(StaticJavaParser.parseExpression("new ChildNextClass<Double>()").asObjectCreationExpr().getType());
//        System.out.println(StaticJavaParser.parseExpression("new ChildNextClass<Double>()").asObjectCreationExpr().getAnonymousClassBody());
    }
}
