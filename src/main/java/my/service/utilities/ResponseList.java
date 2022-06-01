package my.service.utilities;

import java.util.List;

public class ResponseList {

    private Long numberOfResults;
    private List<?> data;

    public ResponseList(Long numberOfResults, List<?> data) {
        this.numberOfResults = numberOfResults;
        this.data = data;
    }

    public Long getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(Long numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
