package mai.student.internet.handler.java;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mai.student.SimpleCodeComparer;
import mai.student.internet.AbstractInternetBorrowingFinder;
import mai.student.internet.common.File;
import mai.student.internet.common.FileWithSourceRating;
import mai.student.internet.common.stats.SourceStatistic;
import mai.student.internet.common.stats.collector.FileStatisticCollector;
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
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavaInternetBorrowingFinder extends AbstractInternetBorrowingFinder<FileWithSourceRating> {

    private final GitHubCodeClient client = new GitHubCodeClient();
    private final AbstractJavaDivider divider = new JavaLineDivider();
    private final GitHubExtractor extractor = new GitHubExtractor();

    public JavaInternetBorrowingFinder(Path source, CodeLanguage lang) {
        super(source, lang, FileWithSourceRating::new);
        configureParser(source);
        divider.addFilter(new DeleteImportFilter());
        divider.addFilter(new DeletePackageFilter());
        // TODO: статический парсер (посмотреть выходы)
    }

    @Override
    public void start() {
        for (FileWithSourceRating file : files) {

            List<String> queries = divider.divide(file);

            // TODO: one query
            queries = List.of(queries.get(5));

            List<CodeSearchResponse> responses = queries.stream().map(q -> sendRequest("\"" + q + "\"")).filter(Objects::nonNull).collect(Collectors.toList());

            FileStatisticCollector collector = new FileStatisticCollector();
            responses.stream().map(CodeSearchResponse::getItems).forEach(items -> items.forEach(collector::process));
            file.addStats(collector.getResult());

            // TETS сравнение
            SimpleCodeComparer codeComparer = new SimpleCodeComparer();
            codeComparer.setFirstProgram(file.getFilePath());

            GitHubExtractor extractor = new GitHubExtractor();
            List<CodeSearchResponse.Item> semitarget = responses.stream().map(CodeSearchResponse::getItems)
                    .reduce(new ArrayList<>(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    }, (a, b) -> {
                        a.addAll(b);
                        return a;
                    });
            CodeSearchResponse.Item target = semitarget.stream().filter(i -> i.extractFileUrl().equals(file.getStats().first().getSourceUrl())).findFirst().orElseThrow();
            extractor.extractFile(target.getRepository().getOwner().getLogin(), target.getRepository().getName(), target.getPath());

            codeComparer.setSecondProgram(Path.of(extractor.getDestination().toString(), target.getName()));
            codeComparer.compare();
            System.out.println(codeComparer.getResult());
            // TODO: test
            file.getStats().forEach(stat -> System.out.println(stat.count() + " " + stat.getSourceName()));
            System.out.println();
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
                // TODO: print uri
                System.out.println(ex.getUri().toString());
                throw ex;
            }
            // TODO: посмотреть коды
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

    @Override
    public double getResult() {
        return 0;
    }

    @Override
    public URL getSource() {
        return null;
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
