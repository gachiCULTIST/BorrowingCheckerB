package mai.student.internet;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.CodeComparer;
import mai.student.ReducingCodeComparer;
import mai.student.SimpleCodeComparer;
import mai.student.internet.AbstractInternetBorrowingFinder;
import mai.student.internet.InternetSearchResult;
import mai.student.internet.common.FileWithSourceRating;
import mai.student.internet.common.stats.SourceStatistic;
import mai.student.internet.common.stats.SourceStatisticComparator;
import mai.student.internet.common.stats.collector.FileStatisticCollector;
import mai.student.internet.common.stats.collector.RepoStatisticCollector;
import mai.student.internet.handler.java.divider.AbstractJavaDivider;
import mai.student.internet.handler.java.divider.JavaLineDivider;
import mai.student.internet.handler.java.filter.DeleteImportFilter;
import mai.student.internet.handler.java.filter.DeletePackageFilter;
import mai.student.internet.reqeust.service.github.GitHubCodeClient;
import mai.student.internet.reqeust.service.github.GitHubExtractor;
import mai.student.internet.reqeust.service.github.ResponseException;
import mai.student.internet.reqeust.service.github.dto.CodeSearchRequestBuilder;
import mai.student.internet.reqeust.service.github.dto.CodeSearchResponse;
import mai.student.internet.reqeust.service.github.dto.enums.QueryLanguages;
import mai.student.tokenizers.CodeLanguage;
import mai.student.tokenizers.NoStartPointException;
import mai.student.utility.ConfigReader;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class InternetBorrowingFinderImpl extends AbstractInternetBorrowingFinder<FileWithSourceRating> {

    private final int ANALYSING_AMOUNT = Integer.parseInt(ConfigReader.getProperty("finder.source.amount"));
    private final GitHubCodeClient client = new GitHubCodeClient();
    private final AbstractJavaDivider divider = new JavaLineDivider();
    private final GitHubExtractor extractor = new GitHubExtractor();
    private List<InternetSearchResult> result;

    public InternetBorrowingFinderImpl(Path source, CodeLanguage lang) {
        super(source, lang, FileWithSourceRating::new);
        configureParser(source);
        divider.addFilter(new DeleteImportFilter());
        divider.addFilter(new DeletePackageFilter());
        // TODO: статический парсер (посмотреть выходы)
    }

    @Override
    public List<InternetSearchResult> start() {
        if (result != null) {
            return result;
        }
        result = new ArrayList<>();

        Map<FileWithSourceRating, List<CodeSearchResponse.Item>> searchResult = new HashMap<>();
        RepoStatisticCollector repoStatisticCollector = new RepoStatisticCollector();
        for (FileWithSourceRating file : files) {

            List<String> queries = divider.divide(file);
            List<CodeSearchResponse> responses = queries.stream().map(q -> sendRequest("\"" + q + "\"")).filter(Objects::nonNull).collect(Collectors.toList());

            List<CodeSearchResponse.Item> semitarget = responses.stream().map(CodeSearchResponse::getItems)
                    .reduce(new ArrayList<>(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    }, (a, b) -> {
                        a.addAll(b);
                        return a;
                    });

            FileStatisticCollector fileStatisticCollector = new FileStatisticCollector();
            semitarget.forEach(fileStatisticCollector::process);
            file.addStats(fileStatisticCollector.getResult());

            semitarget.forEach(repoStatisticCollector::process);

            searchResult.put(file, semitarget);
        }

        try {
            CodeComparer codeComparer = new ReducingCodeComparer();
            codeComparer.setFirstProgram(source);

            List<SourceStatistic> reposStatistic = new ArrayList<>(repoStatisticCollector.getResult());
            reposStatistic.sort(new SourceStatisticComparator());
            for (int i = 0; i < ANALYSING_AMOUNT; ++i) {
                try {
                    hardWay(codeComparer, reposStatistic.get(i));
                } catch (NoStartPointException ex) {
                    System.out.println("Не удалось пройти сложный путь");
                    // TODO: log
                }
            }
        } catch (NoStartPointException ex) {
            // TODO: log
        }

        searchResult.forEach(this::simpleWay);

        return result;
    }

    private void hardWay(CodeComparer codeComparer, SourceStatistic source) {
        extractor.extractRepo(source.getOwner(), source.getRepo());
        codeComparer.setSecondProgram(extractor.getDestination());
        codeComparer.compare();
        result.add(new InternetSearchResult()
                .setSource(source.getSourceUrl())
                .setOriginality(codeComparer.getResult()));
    }

    private void simpleWay(FileWithSourceRating file, List<CodeSearchResponse.Item> sources) {
        SimpleCodeComparer codeComparer = new SimpleCodeComparer();
        codeComparer.setFirstProgram(file.getFilePath());

        for (int i = 0; i < ANALYSING_AMOUNT; ++i) {
            SourceStatistic statistic = file.getStats().get(i);
            if (statistic == null) {
                break;
            }

            CodeSearchResponse.Item target = sources.stream().filter(s -> s.extractFileUrl().equals(statistic.getSourceUrl())).findFirst().orElseThrow();
            extractor.extractFile(target.getRepository().getOwner().getLogin(), target.getRepository().getName(), target.getPath());

            codeComparer.setSecondProgram(Path.of(extractor.getDestination().toString(), target.getName()));
            codeComparer.compare();

            result.add(new InternetSearchResult().setTarget(file.getFilePath())
                    .setSource(target.getHtmlUrl())
                    .setOriginality(codeComparer.getResult()));
        }
    }

    private CodeSearchResponse sendRequest(String query) {
        List<NameValuePair> request = new CodeSearchRequestBuilder()
                .setQueryLanguage(QueryLanguages.JAVA)
                .setQueryContent(query)
                .build();

        CodeSearchResponse response;
        try {
            response = client.get(request);
        } catch (ResponseException ex) {

            ex.printStackTrace();

            if (ex.getCode() == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                System.out.println(ex.getUri().toString());
                throw ex;
            }

            try {
                Thread.sleep(60000);
                return sendRequest(query);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Не удалось поспать во время отправки запросов", e);
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return null;
        }

        return response;
    }

    // Настройка парсера
    private void configureParser(Path source) {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());

        if (Files.isDirectory(source)) {
            typeSolver.add(new JavaParserTypeSolver(source));
        }


        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false).
                setSymbolResolver(new JavaSymbolSolver(typeSolver)));
    }
}
