package com.jss.osiris.modules.myjss.profile.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public class UserScope implements Serializable {
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Responsable responsable;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Responsable responsableViewed;

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public Responsable getResponsableViewed() {
        return responsableViewed;
    }

    public void setResponsableViewed(Responsable responsableViewed) {
        this.responsableViewed = responsableViewed;
    }

}
