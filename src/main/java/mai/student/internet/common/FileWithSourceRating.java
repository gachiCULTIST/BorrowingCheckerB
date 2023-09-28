package mai.student.internet.common;

import lombok.Getter;
import mai.student.internet.common.stats.SourceStatistic;
import mai.student.internet.common.stats.SourceStatisticComparator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class FileWithSourceRating extends AbstractFile {

    private final List<SourceStatistic> stats = new ArrayList<>();

    public FileWithSourceRating(Path filePath) {
        super(filePath);
    }

    public void addStat(SourceStatistic stat) {
        this.stats.add(stat);
    }

    public void addStats(Collection<SourceStatistic> stats) {
        this.stats.addAll(stats);
    }

    public List<SourceStatistic> getStats() {
        stats.sort(new SourceStatisticComparator());

        return stats;
    }
}
