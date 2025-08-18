package com.jss.osiris.modules.myjss.profile.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "urlset")
public class SiteMapEntry {

    public SiteMapEntry(List<SiteMapUrl> urls) {
        this.urls = urls;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    public final String xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "url")
    public List<SiteMapUrl> urls;

    public String getXmlns() {
        return xmlns;
    }

    public List<SiteMapUrl> getUrls() {
        return urls;
    }

    public void setUrls(List<SiteMapUrl> urls) {
        this.urls = urls;
    }

}