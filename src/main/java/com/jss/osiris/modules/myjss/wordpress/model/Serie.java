package com.jss.osiris.modules.myjss.wordpress.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Serie {
    @Id
    private Integer id;
    private String name;
    private String slug;
    private Integer count;
    private Integer serieOrder;

    @Transient
    private AcfSerie acf;

    @Column(columnDefinition = "TEXT")
    private String titleText;

    @Column(columnDefinition = "TEXT")
    private String excerptText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    private Media picture;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public AcfSerie getAcf() {
        return acf;
    }

    public void setAcf(AcfSerie acf) {
        this.acf = acf;
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

    public Media getPicture() {
        return picture;
    }

    public void setPicture(Media picture) {
        this.picture = picture;
    }

    public Integer getSerieOrder() {
        return serieOrder;
    }

    public void setSerieOrder(Integer serieOrder) {
        this.serieOrder = serieOrder;
    }

}
