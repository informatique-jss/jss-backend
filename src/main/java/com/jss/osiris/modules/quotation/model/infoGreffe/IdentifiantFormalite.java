package com.jss.osiris.modules.quotation.model.infoGreffe;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class IdentifiantFormalite {
    @Id
    private String formaliteNumero;
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

    public String getFormaliteNumero() {
        return formaliteNumero;
    }

    public void setFormaliteNumero(String value) {
        this.formaliteNumero = value;
    }
}
