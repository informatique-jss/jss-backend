package com.jss.osiris.modules.quotation.model.guichetUnique;

public class RneCompany {
    private String id;
    private String updatedAt;

    private Formality formality;

    private String siren;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Formality getFormality() {
        return formality;
    }

    public void setFormality(Formality formality) {
        this.formality = formality;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

}
