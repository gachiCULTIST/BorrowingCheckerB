package mai.student.internet.common.stats.collector;

import mai.student.internet.common.stats.Extractable;
import mai.student.internet.common.stats.SourceStatistic;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStatisticCollector<T extends Extractable> implements StatisticCollector<T> {

    protected Map<URL, SourceStatistic> stats = new HashMap<>();

    protected void incrementCounter(URL url, String name) {
        if (stats.containsKey(url)) {
            stats.get(url).count();
            return;
        }

        stats.put(url, new SourceStatistic(name, url));
    }

    @Override
    public Collection<SourceStatistic> getResult() {
        return stats.values();
    }
}
