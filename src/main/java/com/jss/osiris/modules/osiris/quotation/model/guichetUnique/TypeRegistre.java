package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeRegistre {
    private boolean estPresent;
    private String dateImmatriculation;

    public boolean getEstPresent() {
        return estPresent;
    }

    public void setEstPresent(boolean value) {
        this.estPresent = value;
    }

    public String getDateImmatriculation() {
        return dateImmatriculation;
    }

    public void setDateImmatriculation(String value) {
        this.dateImmatriculation = value;
    }
}
