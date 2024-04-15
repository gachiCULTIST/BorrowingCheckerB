package mai.student.internet.reqeust.service.github.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mai.student.internet.reqeust.service.github.dto.enums.QueryLanguages;
import mai.student.internet.reqeust.service.github.dto.enums.QueryParams;
import mai.student.internet.reqeust.service.github.dto.enums.RequestParams;
import mai.student.internet.reqeust.service.github.dto.enums.SortTypes;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CodeSearchRequestBuilder {

    public static final int QUERY_LENGTH_LIMIT = 1000;
    public static final int JAVA_LANG_EXTRA_SYMBOLS = 2; // для расчета остатка от лимита на сам запрос

    private QueryLanguages queryLanguage;
    private String queryContent;
    private SortTypes sortType;
    private Integer pageSize = 100;
    private Integer page;

    public List<NameValuePair> build() {
        if (queryContent == null) {
            throw new UnsupportedOperationException("Код для запроса не должен быть пустым");
        }

        StringBuilder query = new StringBuilder();
        if (this.queryLanguage != null) {
            query.append(QueryParams.LANGUAGE.getName().toLowerCase())
                    .append(":")
                    .append(queryLanguage.getName())
                    .append(" ");
        }
        query.append(queryContent);

        List<NameValuePair> result = new ArrayList<>();
        result.add(new BasicNameValuePair(RequestParams.QUERY.getName(), query.toString()));

        if (this.sortType != null) {
            result.add(new BasicNameValuePair(RequestParams.SORT_TYPE.getName(), this.sortType.getName()));
        }
        if (this.pageSize != null) {
            result.add(new BasicNameValuePair(RequestParams.PAGE_SIZE.getName(), this.pageSize.toString()));
        }
        if (this.page != null) {
            result.add(new BasicNameValuePair(RequestParams.PAGE.getName(), this.page.toString()));
        }
        return result;
    }
}
