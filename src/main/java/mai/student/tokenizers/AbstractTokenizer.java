package mai.student.tokenizers;

import mai.student.intermediateStates.FileRepresentative;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractTokenizer {

    protected CodeLanguage language;
    protected ArrayList<FileRepresentative> files;
    protected HashMap<String, Integer> tokens;
    protected ArrayList<Integer> result;

    public AbstractTokenizer(ArrayList<Path> files, CodeLanguage lang) throws Exception {
        language = lang;

        this.files = new ArrayList<>();
        for (var path : files) {
            FileRepresentative file = new FileRepresentative(path);
            this.files.add(file);
        }

        tokens = new HashMap<>();
    }

    public ArrayList<Integer> getResult() {
        return result;
    }

    public CodeLanguage getLanguage() {
        return language;
    }

    public abstract void tokenize();
}
