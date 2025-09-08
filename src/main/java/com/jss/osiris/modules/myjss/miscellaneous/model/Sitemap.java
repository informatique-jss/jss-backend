package com.jss.osiris.modules.myjss.miscellaneous.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "urlset")
public class Sitemap {

    public static String siteMapFolder = "Sitemaps";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    public final String xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "url")
    public List<UrlEntry> urls;

    public Sitemap(List<UrlEntry> urls) {
        this.urls = urls;
    }
}