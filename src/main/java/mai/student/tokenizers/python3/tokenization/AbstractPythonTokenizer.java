package mai.student.tokenizers.python3.tokenization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mai.student.intermediateStates.python.PyFileRepresentative;
import mai.student.tokenizers.AbstractTokenizer;
import mai.student.tokenizers.CodeLanguage;
import mai.student.utility.Python3AstExtractor;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractPythonTokenizer extends AbstractTokenizer<PyFileRepresentative> {

    protected static final String DEFAULT_DICTIONARY = "tockenVocabularyPython3_12_2.txt";

    public AbstractPythonTokenizer(Path source, CodeLanguage lang) {
        super(source, lang);

        ObjectMapper mapper = new ObjectMapper();
        try {
//            System.out.println(Python3AstExtractor.extractAsts(source));
            this.files = Arrays.stream(mapper.readValue(Python3AstExtractor.extractAsts(source), PyFileRepresentative[].class))
                    .collect(Collectors.toList());
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Проблема чтения исходных файлов", ex);
        }

        tokenMapping = new HashMap<>();
    }

    // Печать результата для дебага
    protected void printTree(List<Integer> tokens) {
        try (FileWriter saveResult = new FileWriter(files.get(0).getPath().getParent() + "/results/"
                + files.get(0).getPath().getFileName() + "_result.txt", false)) {
            StringBuilder result = new StringBuilder();
            for (int i : tokens) {
                String eq = "";
                for (Map.Entry<String, Integer> var : tokenMapping.entrySet()) {
                    if (var.getValue() == i) {
                        eq = var.getKey();
                    }
                }

                result.append(i).append(eq).append(" ");
                if (eq.equals("{") || eq.equals("}") || eq.equals(";")) {
                    result.append('\n');
                }
            }

            saveResult.write(result.toString());
            saveResult.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
