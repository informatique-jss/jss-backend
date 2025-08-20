package com.jss.osiris.modules.osiris.quotation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UniteLegaleImport {
    @Id
    private String siren;
    private boolean isUpdated;

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

}
