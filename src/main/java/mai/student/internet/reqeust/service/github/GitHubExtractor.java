package mai.student.internet.reqeust.service.github;

import lombok.Getter;
import lombok.Setter;
import mai.student.internet.reqeust.service.github.dto.RepoFileResponse;
import mai.student.utility.ConfigReader;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
@Setter
public class GitHubExtractor {

    private final GitHubRepoClient client = new GitHubRepoClient();
    private Path destination = Path.of(ConfigReader.getProperty("extractor.default.dir"));

    public GitHubExtractor() {
        setUpFolder();
    }

    public GitHubExtractor(Path destination) {
        this.destination = destination;
        setUpFolder();
    }

    private void setUpFolder() {
        if (Files.notExists(destination)) {
            try {
                Files.createDirectories(destination);
            } catch (IOException ex) {
                throw new RuntimeException("Не удалось создать директорию для выгрузки файлов: " + destination, ex);
            }
        }

        clean(destination.toFile());
    }

    private void clean(File root) {
        for (File child : Objects.requireNonNull(root.listFiles())) {
            if (child.isDirectory()) {
                clean(child);
            }
            child.delete();
        }
    }

    public void extractRepo(String owner, String repo) {
        clean(destination.toFile());

        byte[] bytes = client.getFullRepo(owner, repo, false);
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[1024];

            ZipEntry zipEntry = zip.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destination.toFile(), zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Не удалось создать директорию " + newFile);
                    }
                } else {
                    // Че-то с архивами созданными в Окнах
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Не удалось создать директорию " + parent);
                    }

                    // Запись
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zip.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zip.getNextEntry();
            }
            zip.closeEntry();
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка разархивации", ex);
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public void extractFile(String owner, String repo, String path) {
        RepoFileResponse file = client.getFile(owner, repo, path);

        try (OutputStream writer = new BufferedOutputStream(new FileOutputStream(Path.of(destination.toString(), file.getName()).toFile()))) {
            if (file.getEncoding().equals("base64")) {
                Base64.Decoder fromBase64 = Base64.getDecoder();
                writer.write(fromBase64.decode(file.getContent().replaceAll("[\n\r]", "")));
            } else {
                writer.write(file.getContent().getBytes(file.getEncoding()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Ошибка записи файла " + path, ex);
        }
    }
}
