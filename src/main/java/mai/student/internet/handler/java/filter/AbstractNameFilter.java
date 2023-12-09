package mai.student.internet.handler.java.filter;

import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class AbstractNameFilter extends ModifierVisitor<Void> {
    public static final String SIMPLE_NAME_PATTERN = "[\\w&&[^\\d]]+[\\w]*";
    public static final String SCREENED_REPLACER = " ???" + SIMPLE_NAME_PATTERN + "??? ";

    @Override
    public Visitable visit(Name n, Void arg) {
        setComplexName(n);
        return super.visit(n, arg);
    }

    @Override
    public Visitable visit(SimpleName n, Void arg) {
        n.setIdentifier(SCREENED_REPLACER);
        return super.visit(n, arg);
    }

    private void setComplexName(Name name) {
        if (name == null) {
            return;
        }

        name.setIdentifier(SCREENED_REPLACER);
        setComplexName(name.getQualifier().orElse(null));
    }
}
