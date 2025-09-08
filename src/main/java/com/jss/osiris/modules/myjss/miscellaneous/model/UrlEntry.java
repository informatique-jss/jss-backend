package com.jss.osiris.modules.myjss.miscellaneous.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UrlEntry {

    @JacksonXmlProperty(localName = "loc")
    public String loc;

    @JacksonXmlProperty(localName = "lastmod")
    public String lastmod;

    @JacksonXmlProperty(localName = "changefreq")
    public String changefreq;

    @JacksonXmlProperty(localName = "priority")
    public String priority;

    public UrlEntry(String loc, String lastmod, String changefreq, String priority) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.changefreq = changefreq;
        this.priority = priority;
    }

    public UrlEntry(String loc) {
        this.loc = loc;
    }
}