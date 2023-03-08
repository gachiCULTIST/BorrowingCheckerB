package mai.student.intermediateStates;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Класс для представления файлов с исходным кодом
public class FileRepresentative implements IStructure {

    private final Path filePath;

    public Package curPackage;
    public List<Import> imports;
    public List<Import> staticImports;
    public List<DefinedClass> classes;

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