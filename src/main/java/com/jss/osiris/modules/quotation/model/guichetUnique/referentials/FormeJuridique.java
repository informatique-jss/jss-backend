package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
public class FormeJuridique implements Serializable, ICode {
    public FormeJuridique(String code) {
        this.code = code;
    }

    public FormeJuridique() {
    }

    @Id
    private String code;

    @Column(columnDefinition = "TEXT")
    private String label;

    private String labelShort;

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

    public String getLabelShort() {
        return labelShort;
    }

    public void setLabelShort(String labelShort) {
        this.labelShort = labelShort;
    }

}
