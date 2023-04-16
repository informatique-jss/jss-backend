package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
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
