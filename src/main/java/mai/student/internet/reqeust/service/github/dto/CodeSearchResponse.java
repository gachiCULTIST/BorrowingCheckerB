package mai.student.internet.reqeust.service.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import mai.student.internet.common.stats.FileStatisticExtractable;
import mai.student.internet.common.stats.RepoStatisticExtractable;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

@Getter
public class CodeSearchResponse {

    @JsonProperty("total_count")
    private int totalAmount;
    @JsonProperty("incomplete_results")
    private boolean isResultCompleted;
    private List<Item> items;

    @Getter
    public static class Item implements FileStatisticExtractable, RepoStatisticExtractable {
        private String name;
        private Path path;
        private String sha;
        private URL url;
        @JsonProperty("git_url")
        private URL gitUrl;
        @JsonProperty("html_url")
        private URL htmlUrl;
        private Repository repository;
        private int score;

        @Override
        public String extractFileName() {
            return this.name;
        }

        @Override
        public URL extractFileUrl() {
            return this.htmlUrl;
        }

        @Override
        public String getRepo() {
            return this.getRepository().getName();
        }

        @Override
        public String getOwner() {
            return this.getRepository().getOwner().getLogin();
        }

        @Override
        public String extractRepoName() {
            return this.repository.name;
        }

        @Override
        public URL extractRepoUrl() {
            return this.repository.htmlUrl;
        }

        @Getter
        public static class Repository {
            private long id;
            @JsonProperty("node_id")
            private String nodeId;
            private String name;
            @JsonProperty("full_name")
            private String fullName;
            private Owner owner;
            @JsonProperty("private")
            private boolean isPrivate;
            @JsonProperty("html_url")
            private URL htmlUrl;
            private String description;
            private boolean fork;
            private URL url;
            @JsonProperty("forks_url")
            private URL forksUrl;
            @JsonProperty("keys_url")
            private URL keysUrl;
            @JsonProperty("collaborators_url")
            private URL collaboratorsUrl;
            @JsonProperty("teams_url")
            private URL teamsUrl;
            @JsonProperty("hooks_url")
            private URL hooksUrl;
            @JsonProperty("issue_events_url")
            private URL issueEventsUrl;
            @JsonProperty("events_url")
            private URL eventsUrl;
            @JsonProperty("assignees_url")
            private URL assigneesUrl;
            @JsonProperty("branches_url")
            private URL branchesUrl;
            @JsonProperty("tags_url")
            private URL tagsUrl;
            @JsonProperty("blobs_url")
            private URL blobsUrl;
            @JsonProperty("git_tags_url")
            private URL gitTagsUrl;
            @JsonProperty("git_refs_url")
            private URL gitRefsUrl;
            @JsonProperty("trees_url")
            private URL treesUrl;
            @JsonProperty("statuses_url")
            private URL statusesUrl;
            @JsonProperty("languages_url")
            private URL languagesUrl;
            @JsonProperty("stargazers_url")
            private URL stargazersUrl;
            @JsonProperty("contributors_url")
            private URL contributorsUrl;
            @JsonProperty("subscribers_url")
            private URL subscribersUrl;
            @JsonProperty("subscription_url")
            private URL subscriptionUrl;
            @JsonProperty("commits_url")
            private URL commitsUrl;
            @JsonProperty("git_commits_url")
            private URL gitCommitsUrl;
            @JsonProperty("comments_url")
            private URL commentsUrl;
            @JsonProperty("issue_comment_url")
            private URL issueCommentUrl;
            @JsonProperty("contents_url")
            private URL contentsUrl;
            @JsonProperty("compare_url")
            private URL compareUrl;
            @JsonProperty("merges_url")
            private URL mergesUrl;
            @JsonProperty("archive_url")
            private URL archiveUrl;
            @JsonProperty("downloads_url")
            private URL downloadsUrl;
            @JsonProperty("issues_url")
            private URL issuesUrl;
            @JsonProperty("pulls_url")
            private URL pullsUrl;
            @JsonProperty("milestones_url")
            private URL milestonesUrl;
            @JsonProperty("notifications_url")
            private URL notificationsUrl;
            @JsonProperty("labels_url")
            private URL labelsUrl;
            @JsonProperty("deployments_url")
            private URL deploymentsUrl;
            @JsonProperty("releases_url")
            private URL releasesUrl;

            @Getter
            public static class Owner {
                private String login;
                private long id;
                @JsonProperty("node_id")
                private String nodeId;
                @JsonProperty("avatar_url")
                private URL avatarUrl;
                @JsonProperty("gravatar_id")
                private String gravatarId;
                private URL url;
                @JsonProperty("html_url")
                private URL htmlUrl;
                @JsonProperty("followers_url")
                private URL followersUrl;
                @JsonProperty("following_url")
                private URL followingUrl;
                @JsonProperty("gists_url")
                private URL gistsUrl;
                @JsonProperty("starred_url")
                private URL starredUrl;
                @JsonProperty("subscriptions_url")
                private URL subscriptionsUrl;
                @JsonProperty("organizations_url")
                private URL organizationsUrl;
                @JsonProperty("repos_url")
                private URL reposUrl;
                @JsonProperty("events_url")
                private URL eventsUrl;
                @JsonProperty("received_events_url")
                private URL receivedEventsUrl;
                private String type;
                @JsonProperty("site_admin")
                private boolean siteAdmin;
            }
        }
    }
}
