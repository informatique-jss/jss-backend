package com.jss.osiris.modules.myjss.wordpress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Author {

    @Id
    private Integer id;

    @Transient
    private Avatar avatar_urls;
    private String name;
    private String slug;
    private String description;

    private String avatar_url_size_24;
    private String avatar_url_size_48;
    private String avatar_url_size_96;

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

    public String getAvatar_url_size_24() {
        return avatar_url_size_24;
    }

    public void setAvatar_url_size_24(String avatar_url_size_24) {
        this.avatar_url_size_24 = avatar_url_size_24;
    }

    public String getAvatar_url_size_48() {
        return avatar_url_size_48;
    }

    public void setAvatar_url_size_48(String avatar_url_size_48) {
        this.avatar_url_size_48 = avatar_url_size_48;
    }

    public String getAvatar_url_size_96() {
        return avatar_url_size_96;
    }

    public void setAvatar_url_size_96(String avatar_url_size_96) {
        this.avatar_url_size_96 = avatar_url_size_96;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
