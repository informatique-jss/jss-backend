package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class TypeOrigine implements Serializable, ICode {
    public TypeOrigine(String code) {
        this.code = code;
    }

    public TypeOrigine() {
    }

    @Id
    private String code;

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
