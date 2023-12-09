package mai.student;

import mai.student.tokenizers.java17.ReducingJavaTokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckComparingResult {
    private static final Logger log = Logger.getLogger(CheckComparingResult.class.getName());

    private static final String resourceMissingMessage = "Missing token dictionary: ";
    private static final String DEFAULT_THRESHOLDS = "thresholds.txt";
    private static final ClassLoader cl = ReducingJavaTokenizer.class.getClassLoader();
    private static final double DIVIDER = 1000;

    private double defaultThreshold = 0.2;
    private boolean onlyDefaultThreshold = false;

    private HashMap<String, Double> thresholds = new HashMap<>();

    public CheckComparingResult() {
        InputStream resourceToLoad = cl.getResourceAsStream(DEFAULT_THRESHOLDS);
        if (resourceToLoad == null) {
            log.severe(resourceMissingMessage + DEFAULT_THRESHOLDS);
            throw new RuntimeException(resourceMissingMessage + DEFAULT_THRESHOLDS);
        }

        try (Scanner scanner = new Scanner(resourceToLoad)) {
            while (scanner.hasNext()) {
                thresholds.put(scanner.next().strip(), scanner.nextInt() / DIVIDER);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public CheckComparingResult(double defaultThreshold) {
        this();

        this.defaultThreshold = defaultThreshold;
    }

    public CheckComparingResult(double defaultThreshold, boolean onlyDefaultThreshold) {
        this();

        this.defaultThreshold = defaultThreshold;
        this.onlyDefaultThreshold = onlyDefaultThreshold;
    }

    public CheckComparingResult(String thresholdsFile) {
        try (Scanner scanner = new Scanner(new FileReader(thresholdsFile))) {
            while (scanner.hasNext()) {
                thresholds.put(scanner.next().strip(), scanner.nextInt() / DIVIDER);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(resourceMissingMessage + thresholdsFile);
        }
    }

    public CheckComparingResult(String thresholdsFile, double defaultThreshold) {
        this(thresholdsFile);

        this.defaultThreshold = defaultThreshold;
    }

    public void setOnlyDefaultThresholdMode(boolean onlyDefaultThresholdMode) {
        this.onlyDefaultThreshold = onlyDefaultThresholdMode;
    }

    public boolean check(double ratio, String assignment) {
        if (ratio == -1) {
            throw new RuntimeException("Not even compared!");
        }

        if (onlyDefaultThreshold) {
            return ratio >= defaultThreshold;
        }

        if (thresholds.containsKey(assignment)) {
            return ratio >= thresholds.get(assignment);
        } else {
            return ratio >= defaultThreshold;
        }
    }

    public static void setLoggerHandler(FileHandler fileHandler) {
        log.setUseParentHandlers(false);

        if (fileHandler == null) {
            return;
        }
        log.addHandler(fileHandler);
    }
}
