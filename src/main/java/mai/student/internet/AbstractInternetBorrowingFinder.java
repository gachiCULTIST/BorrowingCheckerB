package mai.student.internet;

import mai.student.internet.common.File;
import mai.student.tokenizers.CodeLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractInternetBorrowingFinder<T extends File> implements InternetBorrowingFinder {

    protected CodeLanguage language;
    protected Set<T> files;
    protected Path source;
    private final Function<Path, T> constructor;

    public AbstractInternetBorrowingFinder(Path source, CodeLanguage lang, Function<Path, T> constructor){
        this.language = lang;
        this.constructor = constructor;
        this.setFiles(source);
    }


    @Override
    public void setProgram(Path source) {
        this.setFiles(source);
    }

    protected void setFiles(Path source) {
        this.files = new HashSet<>();

        if (Files.isDirectory(source)) {
            for (Path path : collectFiles(source)) {
                T file = constructor.apply(path);
                this.files.add(file);
            }
        } else {
            this.files.add(constructor.apply(source));
        }
    }

    protected List<Path> collectFiles(Path source) {
        try (Stream<Path> insides = Files.list(source)) {
            return insides.reduce(new ArrayList<>(),
                    (paths, path) -> {
                        if (Files.isDirectory(path)) {
                            paths.addAll(collectFiles(path));
                        } else {
                            if (path.toString().endsWith(language.getExtension())) {
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
}
