package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class OrganismeAssuranceMaladieActue implements Serializable, ICode {

    @Id
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
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
