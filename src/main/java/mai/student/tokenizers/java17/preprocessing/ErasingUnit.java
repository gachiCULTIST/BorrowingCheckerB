package mai.student.tokenizers.java17.preprocessing;

class ErasingUnit implements Comparable<ErasingUnit> {
    int index;
    String lexeme;

    ErasingUnit(int index, String lexeme) {
        this.index = index;
        this.lexeme = lexeme;
    }

    @Override
    public int compareTo(ErasingUnit o) {
        return this.index - o.index;
    }
}
