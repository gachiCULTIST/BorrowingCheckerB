package mai.student.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class ConfigReader {

    private static final String FILE_PATH = "application.properties";
    private static final Properties PROPERTIES;

    static {
        try (FileInputStream file = new FileInputStream(
                new File(Objects.requireNonNull(
                        ConfigReader.class.getClassLoader().getResource(FILE_PATH)).toURI()))) {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(file, StandardCharsets.UTF_8));
            PROPERTIES = properties;
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Не удалось прочитать файл с свойствами: " + FILE_PATH, ex);
        }
    }

    public static String getProperty(String name) {
        return Optional.ofNullable(System.getenv(name)).orElse(PROPERTIES.getProperty(name));
    }
}
