package com.jss.osiris.modules.myjss.wordpress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class MyJssCategory {

    @Id
    private Integer id;
    private String name;
    private String slug;
    private Integer categoryOrder;

    @Transient
    private AcfCategory acf;

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

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public AcfCategory getAcf() {
        return acf;
    }

    public void setAcf(AcfCategory acf) {
        this.acf = acf;
    }
}
