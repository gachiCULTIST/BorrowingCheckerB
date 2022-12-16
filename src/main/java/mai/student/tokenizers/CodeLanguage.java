package mai.student.tokenizers;

// Плнаируется в зависимости от языка использовать частные функции токенизации и единые функции для сравнения
public enum CodeLanguage {
    Java(".java"), Python(".py"), C(".cpp");

    private final String extension;

    CodeLanguage(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
