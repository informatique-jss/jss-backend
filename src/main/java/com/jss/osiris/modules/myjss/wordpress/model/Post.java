package com.jss.osiris.modules.myjss.wordpress.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;

public class Post {
    private Integer id;
    private AcfPost acf;
    private Integer author;
    private Integer[] myjss_category;
    private Content title;
    private Content excerpt;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime date;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime modified;
    private Integer[] departement;
    private Integer featured_media;
    private String slug;
    private boolean sticky;
    private Integer[] tags;
    private Content content;

    // Computed field
    private Author fullAuthor;
    private List<MyJssCategory> fullCategories;
    private List<PublishingDepartment> fullDepartment;
    private List<Tag> fullTags;
    private Media media;
    private boolean isPremium;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AcfPost getAcf() {
        return acf;
    }

    public void setAcf(AcfPost acf) {
        this.acf = acf;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Integer[] getMyjss_category() {
        return myjss_category;
    }

    public void setMyjss_category(Integer[] myjss_category) {
        this.myjss_category = myjss_category;
    }

    public Content getTitle() {
        return title;
    }

    public void setTitle(Content title) {
        this.title = title;
    }

    public Content getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Content excerpt) {
        this.excerpt = excerpt;
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

    public Integer[] getDepartement() {
        return departement;
    }

    public void setDepartement(Integer[] departement) {
        this.departement = departement;
    }

    public Integer getFeatured_media() {
        return featured_media;
    }

    public void setFeatured_media(Integer featured_media) {
        this.featured_media = featured_media;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public Integer[] getTags() {
        return tags;
    }

    public void setTags(Integer[] tags) {
        this.tags = tags;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Author getFullAuthor() {
        return fullAuthor;
    }

    public void setFullAuthor(Author fullAuthor) {
        this.fullAuthor = fullAuthor;
    }

    public List<MyJssCategory> getFullCategories() {
        return fullCategories;
    }

    public void setFullCategories(List<MyJssCategory> fullCategories) {
        this.fullCategories = fullCategories;
    }

    public List<PublishingDepartment> getFullDepartment() {
        return fullDepartment;
    }

    public void setFullDepartment(List<PublishingDepartment> fullDepartment) {
        this.fullDepartment = fullDepartment;
    }

    public List<Tag> getFullTags() {
        return fullTags;
    }

    public void setFullTags(List<Tag> fullTags) {
        this.fullTags = fullTags;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

}
