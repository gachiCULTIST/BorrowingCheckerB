package mai.student.tokenizers.java17.lexing;

public enum LexemeType { Integer("int"), Long("long"), Float("float"), Double("double"),
    Char("char"), String("String"), Bool("boolean"), Operator("operator"), Cast("cast"),
    Identifier("identifier"), EoF("");

    private String str;

    LexemeType(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
