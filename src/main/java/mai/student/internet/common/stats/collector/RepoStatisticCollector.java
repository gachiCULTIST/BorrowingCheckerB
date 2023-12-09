package mai.student.internet.common.stats.collector;

import mai.student.internet.common.stats.RepoStatisticExtractable;

public class RepoStatisticCollector extends AbstractStatisticCollector<RepoStatisticExtractable> {

    @Override
    public void process(RepoStatisticExtractable data) {
        incrementCounter(data.extractRepoUrl(), data.extractRepoName(), data.getRepo(), data.getOwner());
    }
}
