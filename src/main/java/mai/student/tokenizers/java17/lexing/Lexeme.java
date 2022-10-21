package mai.student.tokenizers.java17.lexing;

public class Lexeme {

    private String content;
    private LexemeType type;

    public Lexeme(String content, LexemeType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public LexemeType getType() {
        return type;
    }
}
