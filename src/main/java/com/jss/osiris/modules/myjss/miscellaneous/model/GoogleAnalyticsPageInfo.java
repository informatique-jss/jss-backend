package com.jss.osiris.modules.myjss.miscellaneous.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleAnalyticsPageInfo {

    @JsonProperty("page_type")
    private String pageType;

    @JsonProperty("page_name")
    private String pageName;

    @JsonProperty("page_website")
    private String pageWebsite;

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String type) {
        this.pageType = type;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String name) {
        this.pageName = name;
    }

    public String getPageWebsite() {
        return pageWebsite;
    }

    public void setPageWebsite(String website) {
        this.pageWebsite = website;
    }
}
