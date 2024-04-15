package mai.student.intermediateStates.java;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.StructureType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Класс для представления файлов с исходным кодом
public class FileRepresentative implements IStructure<FileRepresentative> {

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