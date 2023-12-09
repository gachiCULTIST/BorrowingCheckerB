package mai.student.internet.common.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URL;

/**
 * Может быть использован как для подсчета кол-во одинаковых файлов, так и репозиториев
 * (в зависимости от используемого коллектора)
 */

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SourceStatistic {

    private int counter = 1;
    private final String sourceName;
    private final URL sourceUrl;
    private final String repo;
    private final String owner;

    public int count() {
        return ++counter;
    }
}
