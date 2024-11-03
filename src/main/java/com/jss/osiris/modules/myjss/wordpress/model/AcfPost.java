package com.jss.osiris.modules.myjss.wordpress.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonIntegerArrayDeserializer;

public class AcfPost {
    private boolean premium;

    @JsonDeserialize(using = JacksonIntegerArrayDeserializer.class)
    @JsonIgnore // TODO
    private Integer[] associated_post;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public Integer[] getAssociated_post() {
        return associated_post;
    }

    public void setAssociated_post(Integer[] associated_post) {
        this.associated_post = associated_post;
    }
}
