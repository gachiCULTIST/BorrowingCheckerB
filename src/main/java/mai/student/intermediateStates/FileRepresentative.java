package mai.student.intermediateStates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

// Класс для представления файлов с исходным кодом
public class FileRepresentative implements IStructure {

    private final Path filePath;
    public String curPackage;
    public ArrayList<String> imports;
    public ArrayList<String> staticImports;
    public ArrayList<DefinedClass> classes;

    public FileRepresentative(Path filePath) {
        this.filePath = filePath;

        imports = new ArrayList<>();
        staticImports = new ArrayList<>();
        classes = new ArrayList<>();
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

}