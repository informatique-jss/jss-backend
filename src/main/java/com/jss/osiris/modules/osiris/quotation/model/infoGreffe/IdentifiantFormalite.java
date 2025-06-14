package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class IdentifiantFormalite implements Serializable {
    @Id
    @IndexedField
    private Integer formaliteNumero;
    private String formaliteType;
    private String emetteurCodePart;

    public String getFormaliteType() {
        return formaliteType;
    }

    public void setFormaliteType(String value) {
        this.formaliteType = value;
    }

    public String getEmetteurCodePart() {
        return emetteurCodePart;
    }

    public void setEmetteurCodePart(String value) {
        this.emetteurCodePart = value;
    }

    public Integer getFormaliteNumero() {
        return formaliteNumero;
    }

    public void setFormaliteNumero(Integer formaliteNumero) {
        this.formaliteNumero = formaliteNumero;
    }

}
