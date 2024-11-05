package com.jss.osiris.modules.myjss.wordpress.model;

import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class MyJssCategory {
    @Id
    private Integer id;
    @IndexedField
    private String name;
    private String slug;
    private String color;
    private Integer categoryOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    private Media picture;

    @Transient
    private AcfCategory acf;

    private Integer count;

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

    public AcfCategory getAcf() {
        return acf;
    }

    public void setAcf(AcfCategory acf) {
        this.acf = acf;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Media getPicture() {
        return picture;
    }

    public void setPicture(Media picture) {
        this.picture = picture;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

}
