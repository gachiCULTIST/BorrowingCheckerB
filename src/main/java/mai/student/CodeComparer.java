package mai.student;

import mai.student.intermediateStates.FileRepresentative;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.java17.JavaTokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CodeComparer {

    private static final String DEFAULT_THRESHOLDS = "thresholds.txt";
    private static ClassLoader cl = JavaTokenizer.class.getClassLoader();
    private static final double DIVIDER = 1000;
    private double defaultThreshold = 0.2;
    private boolean onlyDefaultThreshold = false;
    private HashMap<String, Double> thresholds = new HashMap<>();

    private static Logger log = Logger.getLogger(CodeComparer.class.getName());

    private ArrayList<Path> files1;
    private ArrayList<Integer> code1Tokenized;
    private ArrayList<Path> files2;
    private ArrayList<Integer> code2Tokenized;

    private final int COST = 1;

    private double result = -1;

    public CodeComparer(ArrayList<Path> files1, ArrayList<Path> files2) throws Exception {
        // TODO: delete
//        System.out.println(files1.get(0) + "\n" + files2.get(0));

        this.files1 = files1;
        this.files2 = files2;

        try (Scanner scanner = new Scanner(cl.getResourceAsStream(DEFAULT_THRESHOLDS))) {
            while (scanner.hasNext()) {
                thresholds.put(scanner.next().strip(), scanner.nextInt() / DIVIDER);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        try {
            CodeLanguage lang1 = UtilityClass.getLanguage(files1.get(0).toString()),
                    lang2 = UtilityClass.getLanguage(files2.get(0).toString());

            if (lang1 == lang2) {
                AbstractTokenizer tokenizer1, tokenizer2;

                switch (lang1) {
                    case Java:
                        tokenizer1 = new JavaTokenizer(files1, lang1);
                        tokenizer2 = new JavaTokenizer(files2, lang2);
                        break;
                    case C:
                        log.log(Level.SEVERE, "Unsupported file extension: C/C++ is not supported yet!");
                        throw new UnsupportedOperationException("C/C++ is not supported yet!");
                    case Python:
                        log.log(Level.SEVERE, "Python is not supported yet!");
                        throw new UnsupportedOperationException("Python is not supported yet!");
                    default:
                        log.log(Level.SEVERE, "Unknown language. Hables Espanol?");
                        throw new UnsupportedOperationException("Unknown language. Hables Espanol?");
                }

                tokenizer1.tokenize();
                tokenizer2.tokenize();

                // TODO: proprocessor test
//                if (tokenizer1.getResult().size() <= tokenizer2.getResult().size()) {
//                    code1Tokenized = tokenizer1.getResult();
//                    code2Tokenized = tokenizer2.getResult();
//                } else {
//                    code1Tokenized = tokenizer2.getResult();
//                    code2Tokenized = tokenizer1.getResult();
//                }

            } else {
                log.log(Level.SEVERE, "Comparing files with different extensions!");
                throw new UnsupportedOperationException("Comparing files with different extensions!");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

    }

    public CodeComparer(ArrayList<Path> files1, ArrayList<Path> files2, double threshold) throws Exception {
        this.files1 = files1;
        this.files2 = files2;

        defaultThreshold = threshold;
        onlyDefaultThreshold = true;

        try {
            CodeLanguage lang1 = UtilityClass.getLanguage(files1.get(0).toString()),
                    lang2 = UtilityClass.getLanguage(files2.get(0).toString());

            if (lang1 == lang2) {
                AbstractTokenizer tokenizer1, tokenizer2;

                switch (lang1) {
                    case Java:
                        tokenizer1 = new JavaTokenizer(files1, lang1);
                        tokenizer2 = new JavaTokenizer(files2, lang2);
                        break;
                    case C:
                        log.log(Level.SEVERE, "Unsupported file extension: C/C++ is not supported yet!");
                        throw new UnsupportedOperationException("C/C++ is not supported yet!");
                    case Python:
                        log.log(Level.SEVERE, "Python is not supported yet!");
                        throw new UnsupportedOperationException("Python is not supported yet!");
                    default:
                        log.log(Level.SEVERE, "Unknown language. Hables Espanol?");
                        throw new UnsupportedOperationException("Unknown language. Hables Espanol?");
                }

                tokenizer1.tokenize();
                tokenizer2.tokenize();

                if (tokenizer1.getResult().size() <= tokenizer2.getResult().size()) {
                    code1Tokenized = tokenizer1.getResult();
                    code2Tokenized = tokenizer2.getResult();
                } else {
                    code1Tokenized = tokenizer2.getResult();
                    code2Tokenized = tokenizer1.getResult();
                }

            } else {
                log.log(Level.SEVERE, "Comparing files with different extensions!");
                throw new UnsupportedOperationException("Comparing files with different extensions!");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

    }

    public void compare() {
        log.info("Comparing stage start: " + files1.get(0) + ", " + files2.get(0));

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

        result = (m1 < m2) ? m1 : m2;
    }

    public double getResult() {
        return result;
    }

    public boolean isCheckPassed() {
        if (result == -1) {
            throw new RuntimeException("Not even compared!");
        }

        if (onlyDefaultThreshold) {
            return result >= defaultThreshold;
        }

        String assignment = "";
        try {
            assignment = files1.get(0).getFileName().toString().split("_")[1];
        } catch (IndexOutOfBoundsException e) {
        }

        if (thresholds.containsKey(assignment)) {
            return result >= thresholds.get(assignment);
        } else {
            return result >= defaultThreshold;
        }
    }

    public static void setLoggerHandler(FileHandler fileHandler) {
        log.setUseParentHandlers(false);
        JavaTokenizer.setLoggerHandler(fileHandler);
        FileRepresentative.setLoggerHandler(fileHandler);

        if (fileHandler == null) {
            return;
        }
        log.addHandler(fileHandler);
    }
}