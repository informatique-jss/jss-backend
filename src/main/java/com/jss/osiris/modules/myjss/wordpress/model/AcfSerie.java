package com.jss.osiris.modules.myjss.wordpress.model;

public class AcfSerie {
    private String excerpt;
    private Integer picture;
    private Boolean is_stay_on_top;
    private String title;

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getPicture() {
        return picture;
    }

    public void setPicture(Integer picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIs_stay_on_top() {
        return is_stay_on_top;
    }

    public void setIs_stay_on_top(Boolean is_stay_on_top) {
        this.is_stay_on_top = is_stay_on_top;
    }

}
