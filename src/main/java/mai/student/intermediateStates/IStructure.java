package mai.student.intermediateStates;

import java.util.List;

public interface IStructure {
    String getName();

    StructureType getStrucType();

    default IStructure getParent() {
        return null;
    }

    default void actuateTypes(List<FileRepresentative> files) {
    }

    default boolean isLinked() {
        return true;
    }
}