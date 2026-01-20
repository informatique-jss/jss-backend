package com.jss.osiris.modules.myjss.miscellaneous.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleAnalyticsEvent {

    @JsonProperty("name")
    private String name;

    @JsonProperty("page")
    private GoogleAnalyticsPageInfo page;

    @JsonProperty("params")
    private GoogleAnalyticsParams params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoogleAnalyticsPageInfo getPage() {
        return page;
    }

    public void setPage(GoogleAnalyticsPageInfo page) {
        this.page = page;
    }

    public GoogleAnalyticsParams getParams() {
        return params;
    }

    public void setParams(GoogleAnalyticsParams params) {
        this.params = params;
    }
}
