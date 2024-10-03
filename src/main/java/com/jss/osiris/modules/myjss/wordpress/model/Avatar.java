package com.jss.osiris.modules.myjss.wordpress.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Avatar {
    @JsonProperty("24")
    private String size_24;
    @JsonProperty("48")
    private String size_48;
    @JsonProperty("96")
    private String size_96;

    public String getSize_24() {
        return size_24;
    }

    public void setSize_24(String size_24) {
        this.size_24 = size_24;
    }

    public String getSize_48() {
        return size_48;
    }

    public void setSize_48(String size_48) {
        this.size_48 = size_48;
    }

    public String getSize_96() {
        return size_96;
    }

    public void setSize_96(String size_96) {
        this.size_96 = size_96;
    }

}
