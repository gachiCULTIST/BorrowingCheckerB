package mai.student.intermediateStates;

import java.util.List;

public interface IStructure<T> {
    String getName();

    StructureType getStrucType();

    default IStructure<T> getParent() {
        return null;
    }

    default void actuateTypes(List<T> files) {
    }

    default boolean isLinked() {
        return true;
    }
}