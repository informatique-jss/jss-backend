package com.jss.osiris.modules.myjss.wordpress.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(indexes = { @Index(name = "idx_post_slug", columnList = "slug", unique = true) })
public class Post implements IId {
    @Id
    private Integer id;

    @Transient
    private AcfPost acf;

    private Integer author;

    @Transient
    private Integer[] myjss_category;

    @Transient
    private Integer[] categories;

    @Transient
    private Content title;
    @Column(columnDefinition = "TEXT")
    @IndexedField
    private String titleText;

    @Transient
    private Content excerpt;
    @Column(columnDefinition = "TEXT")
    @IndexedField
    private String excerptText;

    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    @IndexedField
    private LocalDateTime date;
    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    private LocalDateTime modified;

    @Transient
    private Integer[] departement;

    @Transient
    private Integer[] serie;

    @Transient
    private Integer featured_media;

    @IndexedField
    private String slug;
    private boolean sticky;

    @Transient
    private Integer[] tags;

    @Transient
    private Content content;
    @Column(columnDefinition = "TEXT")
    @IndexedField
    private String contentText;

    // Computed field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_author")
    @IndexedField
    private Author fullAuthor;

    @ManyToMany
    @JoinTable(name = "asso_post_my_jss_category", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_my_jss_category"))
    @IndexedField
    private List<MyJssCategory> myJssCategories;

    @ManyToMany
    @JoinTable(name = "asso_post_category", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_category"))
    private List<Category> postCategories;

    @ManyToMany
    @JoinTable(name = "asso_post_publishing_department", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_publishing_department"))
    private List<PublishingDepartment> departments;

    @ManyToMany
    @JoinTable(name = "asso_post_tag", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_tag"))
    @IndexedField
    private List<Tag> postTags;

    @ManyToMany
    @JoinTable(name = "asso_post_serie", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_serie"))
    private List<Serie> postSerie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    @IndexedField
    private Media media;

    @ManyToMany
    @JoinTable(name = "asso_post_related", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_post_related"))
    private List<Post> relatedPosts;

    private Boolean isPremium;

    private Integer premiumPercentage;

    private String podcastUrl;

    private String videoUrl;

    private Integer mediaTimeLength;

    private Boolean isCancelled;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private List<PostView> postViews;

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

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getExcerptText() {
        return excerptText;
    }

    public void setExcerptText(String excerptText) {
        this.excerptText = excerptText;
    }

    public List<MyJssCategory> getMyJssCategories() {
        return myJssCategories;
    }

    public void setMyJssCategories(List<MyJssCategory> myJssCategories) {
        this.myJssCategories = myJssCategories;
    }

    public List<PublishingDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<PublishingDepartment> departments) {
        this.departments = departments;
    }

    public List<Tag> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<Tag> postTags) {
        this.postTags = postTags;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Integer[] getCategories() {
        return categories;
    }

    public void setCategories(Integer[] categories) {
        this.categories = categories;
    }

    public List<Category> getPostCategories() {
        return postCategories;
    }

    public void setPostCategories(List<Category> postCategories) {
        this.postCategories = postCategories;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer[] getSerie() {
        return serie;
    }

    public void setSerie(Integer[] serie) {
        this.serie = serie;
    }

    public List<Serie> getPostSerie() {
        return postSerie;
    }

    public void setPostSerie(List<Serie> postSerie) {
        this.postSerie = postSerie;
    }

    public List<Post> getRelatedPosts() {
        return relatedPosts;
    }

    public void setRelatedPosts(List<Post> relatedPosts) {
        this.relatedPosts = relatedPosts;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public List<PostView> getPostViews() {
        return postViews;
    }

    public void setPostViews(List<PostView> postViews) {
        this.postViews = postViews;
    }

    public Integer getMediaTimeLength() {
        return mediaTimeLength;
    }

    public void setMediaTimeLength(Integer mediaTimeLength) {
        this.mediaTimeLength = mediaTimeLength;
    }

    public Integer getPremiumPercentage() {
        return premiumPercentage;
    }

    public void setPremiumPercentage(Integer premiumPercentage) {
        this.premiumPercentage = premiumPercentage;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

}
