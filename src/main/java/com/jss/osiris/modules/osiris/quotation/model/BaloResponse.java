package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

public class BaloResponse {
    private long totalCount;
    private List<BaloNotice> results;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<BaloNotice> getResults() {
        return results;
    }

    public void setResults(List<BaloNotice> results) {
        this.results = results;
    }

}
