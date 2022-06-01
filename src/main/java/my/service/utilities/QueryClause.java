package my.service.utilities;

import java.util.Map;

public class QueryClause {

    private String queryText;
    private Map<String,String> queryParams;

    public QueryClause(String queryText, Map<String, String> queryParams) {
        this.queryText = queryText;
        this.queryParams = queryParams;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
