package mai.student.internet.common.stats.collector;

import mai.student.internet.common.stats.FileStatisticExtractable;

public class FileStatisticCollector extends AbstractStatisticCollector<FileStatisticExtractable> {

    @Override
    public void process(FileStatisticExtractable data) {
        incrementCounter(data.extractFileUrl(), data.extractFileName());
    }
}
