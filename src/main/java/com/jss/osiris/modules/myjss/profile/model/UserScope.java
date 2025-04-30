package com.jss.osiris.modules.myjss.profile.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_user_scope_responsable", columnList = "id_responsable") })
public class UserScope implements Serializable, IId {
    @Id
    @SequenceGenerator(name = "user_scope_sequence", sequenceName = "user_scope_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_scope_sequence")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Responsable responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable_viewed")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Responsable responsableViewed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
