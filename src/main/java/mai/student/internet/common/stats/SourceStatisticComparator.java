package mai.student.internet.common.stats;

import java.util.Comparator;

public class SourceStatisticComparator implements Comparator<SourceStatistic> {

    @Override
    public int compare(SourceStatistic o1, SourceStatistic o2) {
        return o2.getCounter() - o1.getCounter() == 0 ?
                o2.getSourceName().compareTo(o1.getSourceName()) == 0 ?
                        o2.getSourceUrl().toString().compareTo(o1.getSourceUrl().toString())
                        : o2.getSourceName().compareTo(o1.getSourceName())
                : o2.getCounter() - o1.getCounter();
    }
}
