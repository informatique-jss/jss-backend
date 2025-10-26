package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

public class BodaccResponse {
    private long totalCount;
    private List<BodaccNotice> results;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<BodaccNotice> getResults() {
        return results;
    }

    public void setResults(List<BodaccNotice> results) {
        this.results = results;
    }
}
