package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

public class JoResponse {
    private long totalCount;
    private List<JoNotice> results;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<JoNotice> getResults() {
        return results;
    }

    public void setResults(List<JoNotice> results) {
        this.results = results;
    }

}
