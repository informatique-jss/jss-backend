package com.jss.osiris.modules.myjss.wordpress.model;

public class Author {
    private Integer id;
    private Avatar avatar_urls;
    private String name;
    private String slug;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Avatar getAvatar_urls() {
        return avatar_urls;
    }

    public void setAvatar_urls(Avatar avatar_urls) {
        this.avatar_urls = avatar_urls;
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

}
