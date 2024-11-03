package com.jss.osiris.modules.myjss.wordpress.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class Page {
    // Common fields
    @Id
    private Integer id;

    @Transient
    private AcfPage acf;

    private Integer author;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime date;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime modified;
    private Integer menu_order;

    @Transient
    private Integer parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent_page")
    private Page parentPage;

    @Transient
    private Content title;
    @Column(columnDefinition = "TEXT")
    private String titleText;
    private String slug;

    // Single page request fields
    @Transient
    private Content content;
    @Column(columnDefinition = "TEXT")
    private String contentText;

    @Transient
    private Content excerpt;
    @Column(columnDefinition = "TEXT")
    private String excerptText;

    @Transient
    private Integer featured_media;

    // Computed fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    private Media media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_author")
    private Author fullAuthor;

    @OneToMany(mappedBy = "parentPage", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "parentPage" }, allowSetters = true)
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

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getExcerptText() {
        return excerptText;
    }

    public void setExcerptText(String excerptText) {
        this.excerptText = excerptText;
    }

    public Page getParentPage() {
        return parentPage;
    }

    public void setParentPage(Page parentPage) {
        this.parentPage = parentPage;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

}
