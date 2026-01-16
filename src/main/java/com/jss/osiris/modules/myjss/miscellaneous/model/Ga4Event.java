package com.jss.osiris.modules.myjss.miscellaneous.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ga4Event {

    @JsonProperty("name")
    private String name;

    @JsonProperty("page")
    private Ga4PageInfo page;

    @JsonProperty("params")
    private Ga4Params params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ga4PageInfo getPage() {
        return page;
    }

    public void setPage(Ga4PageInfo page) {
        this.page = page;
    }

    public Ga4Params getParams() {
        return params;
    }

    public void setParams(Ga4Params params) {
        this.params = params;
    }
}
