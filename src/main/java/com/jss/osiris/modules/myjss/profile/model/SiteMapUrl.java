package com.jss.osiris.modules.myjss.profile.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SiteMapUrl {

    @JacksonXmlProperty(localName = "loc")
    public String loc;

    @JacksonXmlProperty(localName = "lastmod")
    public String lastmod;

    public SiteMapUrl(String loc, String lastmod) {
        this.loc = loc;
        this.lastmod = lastmod;
    }
}