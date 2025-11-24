package com.jss.osiris.modules.myjss.miscellaneous.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ga4Event {

    @JsonProperty("name")
    private String name;

    @JsonProperty("params")
    private Ga4ParamPurchase params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ga4ParamPurchase getParams() {
        return params;
    }

    public void setParams(Ga4ParamPurchase params) {
        this.params = params;
    }
}
