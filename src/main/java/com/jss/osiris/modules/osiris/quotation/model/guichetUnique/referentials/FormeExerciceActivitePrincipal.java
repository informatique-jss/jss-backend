package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
public class FormeExerciceActivitePrincipal implements Serializable, ICode {
    public FormeExerciceActivitePrincipal(String code) {
        this.code = code;
    }

    public FormeExerciceActivitePrincipal() {
    }

    @Id
    @JsonView(JacksonViews.MyJssView.class)
    private String code;

    @Column(columnDefinition = "TEXT")
    @JsonView(JacksonViews.MyJssView.class)
    private String label;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
