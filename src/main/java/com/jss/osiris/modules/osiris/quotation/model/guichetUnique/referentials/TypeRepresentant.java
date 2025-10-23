package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Id;

@JsonIgnoreProperties
public class TypeRepresentant implements Serializable, ICode {
    public TypeRepresentant(String code) {
        this.code = code;
    }

    public TypeRepresentant() {
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
