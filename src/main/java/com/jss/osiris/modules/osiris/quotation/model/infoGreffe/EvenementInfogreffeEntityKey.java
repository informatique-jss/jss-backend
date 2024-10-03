package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EvenementInfogreffeEntityKey implements Serializable {

    public LocalDateTime createdDate;
    public String codeEtat;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCodeEtat() {
        return codeEtat;
    }

    public void setCodeEtat(String codeEtat) {
        this.codeEtat = codeEtat;
    }

}
