package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeDeStatuts implements Serializable, ICode {
    public TypeDeStatuts(String code) {
        this.code = code;
    }

    public TypeDeStatuts() {
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
