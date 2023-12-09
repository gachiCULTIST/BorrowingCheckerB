package mai.student;

import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.ReducingJavaTokenizer;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public abstract class AbstractCodeComparer implements CodeComparer {

    protected static final Logger LOGGER = Logger.getLogger(ReducingCodeComparer.class.getName());

    protected Path source1;
    protected List<Integer> code1Tokenized;
    protected Path source2;
    protected List<Integer> code2Tokenized;

    protected double result = -1;

    public void setFirstProgram(Path source) {
        setFirstProgram(source, null);
    }

    public void setFirstProgram(Path source, CodeLanguage lang) {
        if (source == null || source.toString().isEmpty()) {
            throw new IllegalArgumentException("List of paths must not be null or empty!");
        }

        this.source1 = source;
        this.code1Tokenized = tokenizeOnSetUp(source, lang);
    }

    public void setSecondProgram(Path source) {
        setSecondProgram(source, null);
    }

    public void setSecondProgram(Path source, CodeLanguage lang) {
        if (source == null || source.toString().isEmpty()) {
            throw new IllegalArgumentException("List of paths must not be null or empty!");
        }

        this.source2 = source;
        this.code2Tokenized = tokenizeOnSetUp(source, lang);
    }

    protected abstract List<Integer> tokenizeOnSetUp(Path source, CodeLanguage lang);

    public void compare() {
        LOGGER.info("Comparing stage start: " + source1 + ", " + source2);

        if (this.code1Tokenized == null || this.code2Tokenized == null) {
            throw new UnsupportedOperationException("Code sources must be set!");
        }

        List<Integer> code1Tokenized, code2Tokenized;
        if (this.code1Tokenized.size() <= this.code2Tokenized.size()) {
            code1Tokenized = this.code1Tokenized;
            code2Tokenized = this.code2Tokenized;
        } else {
            code1Tokenized = this.code2Tokenized;
            code2Tokenized = this.code1Tokenized;
        }

        final int COLUMN_LENGTH = code1Tokenized.size() + 1;

        int[] firstColumn = new int[COLUMN_LENGTH];
        int[] secondColumn = new int[COLUMN_LENGTH];

        // Заполняем нулевой столбец
        for (int i = 0; i < COLUMN_LENGTH; ++i) {
            firstColumn[i] = i;
        }

        // Заполняем таблицу
        for (int i = 0; i < code2Tokenized.size(); ++i) {
            secondColumn[0] = i + 1;
            // Levenshtein algorithm implementation
            for (int j = 1; j < COLUMN_LENGTH; ++j) {
                int COST = 1;
                int a = firstColumn[j] + COST,
                        b = firstColumn[j - 1],
                        c = secondColumn[j - 1] + COST;

                if (!code1Tokenized.get(j - 1).equals(code2Tokenized.get(i))) {
                    b += COST;
                }

                if (a <= b && a <= c) {
                    secondColumn[j] = a;
                } else if (b <= a && b <= c) {
                    secondColumn[j] = b;
                } else {
                    secondColumn[j] = c;
                }
            }

            int[] temp = firstColumn;
            firstColumn = secondColumn;
            secondColumn = temp;
        }

        // Находим редакционное расстояние
        int distance = firstColumn[0];
        for (int i = 1; i < COLUMN_LENGTH; ++i) {
            if (distance > firstColumn[i]) {
                distance = firstColumn[i];
            }
        }

        // Определяем итоговое значение
        double m1 = ((double) distance) / code1Tokenized.size(),
                m2 = ((double) distance) / code2Tokenized.size();

        result = Math.min(m1, m2);
    }

    public double getResult() {
        return result;
    }

    public static void setLoggerHandler(FileHandler fileHandler) {
        LOGGER.setUseParentHandlers(false);
        ReducingJavaTokenizer.setLoggerHandler(fileHandler);

        if (fileHandler == null) {
            return;
        }
        LOGGER.addHandler(fileHandler);
    }
}
