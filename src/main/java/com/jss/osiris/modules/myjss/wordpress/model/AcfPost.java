package com.jss.osiris.modules.myjss.wordpress.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.EmptyStringAsEmptyListDeserializer;

public class AcfPost {
    private boolean premium;
    private Integer premium_percentage;

    @JsonDeserialize(using = EmptyStringAsEmptyListDeserializer.class)
    private List<Integer> associated_post;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public Integer getPremium_percentage() {
        return premium_percentage;
    }

    public void setPremium_percentage(Integer premium_percentage) {
        this.premium_percentage = premium_percentage;
    }

    public List<Integer> getAssociated_post() {
        return associated_post;
    }

    public void setAssociated_post(List<Integer> associated_post) {
        this.associated_post = associated_post;
    }
}
