package mai.student.intermediateStates;

import mai.student.intermediateStates.DefinedClass;
import mai.student.intermediateStates.IStructure;
import mai.student.tokenizers.java17.JavaTokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

// Класс для представления файлов с исходным кодом
public class FileRepresentative implements IStructure {

    private static Logger log = Logger.getLogger(FileRepresentative.class.getName());

    private Path filePath;
    public String curPackage;
    public ArrayList<String> imports;
    public ArrayList<String> staticImports;
    public ArrayList<DefinedClass> classes;

    // Единый редактируемый (препроцессинг) код
    @Deprecated
    public StringBuilder code;

    public FileRepresentative(Path filePath) throws Exception {
        this.filePath = filePath;

        imports = new ArrayList<>();
        staticImports = new ArrayList<>();
        classes = new ArrayList<>();
        try {
            code = new StringBuilder(Files.readString(filePath));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to read file: " + filePath, e);
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getName() {
        return filePath.getFileName().toString();
    }

    @Override
    public StructureType getStrucType() {
        return StructureType.File;
    }

    public Path getFilePath() {
        return filePath;
    }

    public static void setLoggerHandler(FileHandler fileHandler) {
        log.setUseParentHandlers(false);
        if (fileHandler == null) {
            return;
        }
        log.addHandler(fileHandler);
    }
}
