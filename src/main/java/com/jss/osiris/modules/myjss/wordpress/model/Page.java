package com.jss.osiris.modules.myjss.wordpress.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;

public class Page {
    // Common fields
    private Integer id;
    private AcfPage acf;
    private Integer author;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime date;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime modified;
    private Integer menu_order;
    private Integer parent;
    private Content title;
    private String slug;

    // Single page request fields
    private Content content;
    private Content excerpt;
    private Integer featured_media;

    // Computed fields
    private Media media;
    private Author fullAuthor;
    private List<Page> childrenPages;
    private boolean isPremium;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AcfPage getAcf() {
        return acf;
    }

    public void setAcf(AcfPage acf) {
        this.acf = acf;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public Integer getMenu_order() {
        return menu_order;
    }

    public void setMenu_order(Integer menu_order) {
        this.menu_order = menu_order;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Content getTitle() {
        return title;
    }

    public void setTitle(Content title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Content excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getFeatured_media() {
        return featured_media;
    }

    public void setFeatured_media(Integer featured_media) {
        this.featured_media = featured_media;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public List<Page> getChildrenPages() {
        return childrenPages;
    }

    public void setChildrenPages(List<Page> childrenPages) {
        this.childrenPages = childrenPages;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Author getFullAuthor() {
        return fullAuthor;
    }

    public void setFullAuthor(Author fullAuthor) {
        this.fullAuthor = fullAuthor;
    }

}
