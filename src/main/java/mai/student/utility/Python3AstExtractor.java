package mai.student.utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Python3AstExtractor {

    private static final int waitMinutes = 2;
    private static final String scriptName = "astExtractor.py";
    private static final List<String> commandBase = List.of(
            "py",
            scriptName
    );

    static {

        Path scriptPath = Path.of(scriptName);
        if (Files.notExists(scriptPath)) {
            try (InputStream script = Python3AstExtractor.class.getClassLoader().getResourceAsStream(scriptName);
                 OutputStream writer = new BufferedOutputStream(new FileOutputStream(scriptName))) {
                if (script == null) {
                    throw new RuntimeException("Ошибка установки скрипта - в ресурсах не найден файл " + scriptName);
                }

                writer.write(script.readAllBytes());
            } catch (IOException ex) {
                throw new RuntimeException("Ошибка установки скрипта", ex);
            }
        }
    }

    public static String extractAsts(Path path) {
        List<String> command = new ArrayList<>(commandBase);
        command.add(path.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        try {
            process = processBuilder.start();
            boolean exited = process.waitFor(waitMinutes, TimeUnit.MINUTES);

            if (!exited) {
                process.destroy();
            }

            String output;
            try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(process.getInputStream())))) {
                List<String> lines = new ArrayList<>();
                while (scanner.hasNext()) {
                    lines.add(scanner.nextLine());
                }
                output = String.join("\n", lines);
            }

            if (!exited || process.exitValue() != 0) {
                throw new RuntimeException("Ошибка выполнения скрипта, вывод: \n" + output);
            }

            return output;
        } catch (IOException | InterruptedException ex) {
            if (process != null && process.isAlive()) {
                process.destroy();
            }
            throw new RuntimeException("Ошибка запуска скрипта", ex);
        }
    }
}
