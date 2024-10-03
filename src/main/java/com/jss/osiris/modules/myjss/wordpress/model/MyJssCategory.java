package com.jss.osiris.modules.myjss.wordpress.model;

public class MyJssCategory {
    private Integer id;
    private String name;
    private String slug;
    private String color;
    private Media picture;
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

}
