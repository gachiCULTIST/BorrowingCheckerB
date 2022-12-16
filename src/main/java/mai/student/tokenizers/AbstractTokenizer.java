package mai.student.tokenizers;

import mai.student.intermediateStates.FileRepresentative;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractTokenizer {

    protected CodeLanguage language;
    protected List<FileRepresentative> files;
    protected Map<String, Integer> tokens;
    protected List<Integer> result;

    public AbstractTokenizer(Path source, CodeLanguage lang) {
        language = lang;

        this.files = new ArrayList<>();

        if (Files.isDirectory(source)) {
            for (Path path : collectFiles(source)) {
                FileRepresentative file = new FileRepresentative(path);
                this.files.add(file);
            }
        } else {
            this.files.add(new FileRepresentative(source));
        }

        tokens = new HashMap<>();
    }

    private List<Path> collectFiles(Path source) {
        try (Stream<Path> insides = Files.list(source)) {
            return insides.reduce(new ArrayList<>(),
                    (paths, path) -> {
                        if (Files.isDirectory(path)) {
                            paths.addAll(collectFiles(path));
                        } else {
                            if (path.endsWith(language.getExtension())) {
                                paths.add(path);
                            }
                        }
                        return paths;
                    }, (paths1, paths2) -> {
                        paths1.addAll(paths2);
                        return paths1;
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("Wrong source path: " + source, e);
        }
    }

    public List<Integer> getResult() {
        return result;
    }

    public CodeLanguage getLanguage() {
        return language;
    }

    public abstract void tokenize();
}
