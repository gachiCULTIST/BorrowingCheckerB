package mai.student.internet.common.stats.collector;

import mai.student.internet.common.stats.Extractable;
import mai.student.internet.common.stats.SourceStatistic;

import java.util.Collection;

public interface StatisticCollector<T extends Extractable> {

    void process(T data);
    Collection<SourceStatistic> getResult();
}
