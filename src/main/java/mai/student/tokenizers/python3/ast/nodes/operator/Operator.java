package mai.student.tokenizers.python3.ast.nodes.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = UAdd.class, name = "UAdd"),
        @JsonSubTypes.Type(value = USub.class, name = "USub"),
        @JsonSubTypes.Type(value = Not.class, name = "Not"),
        @JsonSubTypes.Type(value = Invert.class, name = "Invert"),

        @JsonSubTypes.Type(value = Add.class, name = "Add"),
        @JsonSubTypes.Type(value = Sub.class, name = "Sub"),
        @JsonSubTypes.Type(value = Mult.class, name = "Mult"),
        @JsonSubTypes.Type(value = Div.class, name = "Div"),
        @JsonSubTypes.Type(value = FloorDiv.class, name = "FloorDiv"),
        @JsonSubTypes.Type(value = Mod.class, name = "Mod"),
        @JsonSubTypes.Type(value = Pow.class, name = "Pow"),
        @JsonSubTypes.Type(value = LShift.class, name = "LShift"),
        @JsonSubTypes.Type(value = RShift.class, name = "RShift"),
        @JsonSubTypes.Type(value = BitOr.class, name = "BitOr"),
        @JsonSubTypes.Type(value = BitXor.class, name = "BitXor"),
        @JsonSubTypes.Type(value = BitAnd.class, name = "BitAnd"),
        @JsonSubTypes.Type(value = MatMult.class, name = "MatMult"),

        @JsonSubTypes.Type(value = And.class, name = "And"),
        @JsonSubTypes.Type(value = Or.class, name = "Or"),

        @JsonSubTypes.Type(value = Eq.class, name = "Eq"),
        @JsonSubTypes.Type(value = NotEq.class, name = "NotEq"),
        @JsonSubTypes.Type(value = Lt.class, name = "Lt"),
        @JsonSubTypes.Type(value = LtE.class, name = "LtE"),
        @JsonSubTypes.Type(value = Gt.class, name = "Gt"),
        @JsonSubTypes.Type(value = GtE.class, name = "GtE"),
        @JsonSubTypes.Type(value = Is.class, name = "Is"),
        @JsonSubTypes.Type(value = IsNot.class, name = "IsNot"),
        @JsonSubTypes.Type(value = In.class, name = "In"),
        @JsonSubTypes.Type(value = NotIn.class, name = "NotIn")
})
@Getter
public abstract class Operator {

    @JsonProperty("_type")
    private String type;

    public abstract Instance getSelfOps();

    @Getter
    public enum Instance {
        U_ADD("+"), U_SUB("-"), NOT("not"), INVERT("~"),
        ADD("+"), SUB("-"), MULT("*"), DIV("/"), FLOOR_DIV("//"), MOD("%"), POW("**"), L_SHIFT("<<"), R_SHIFT(">>"), BIT_OR("|"), BIT_XOR("^"), BIT_AND("&"), MAT_MULT("@"),
        AND("and"), OR("or"),
        EQ("=="), NOT_EQ("!="), LT("<"), LT_E("<="), GT(">"), GT_E(">="), IS("is"), IS_NOT("is", "not"), IN("in"), NOT_IN("not", "in");

        private final List<String> ops;

        Instance(String... ops) {
            this.ops = Arrays.stream(ops).collect(Collectors.toList());
        }
    }
}
