package com.jss.osiris.modules.quotation.model.guichetUnique;

public class Historique {
    private String dateIntegration;
    private String codeEvenement;
    private String libelleEvenement;
    private String numeroLiasse;
    private String patchID;

    public String getDateIntegration() {
        return dateIntegration;
    }

    public void setDateIntegration(String value) {
        this.dateIntegration = value;
    }

    public String getCodeEvenement() {
        return codeEvenement;
    }

    public void setCodeEvenement(String value) {
        this.codeEvenement = value;
    }

    public String getLibelleEvenement() {
        return libelleEvenement;
    }

    public void setLibelleEvenement(String value) {
        this.libelleEvenement = value;
    }

    public String getNumeroLiasse() {
        return numeroLiasse;
    }

    public void setNumeroLiasse(String value) {
        this.numeroLiasse = value;
    }

    public String getPatchID() {
        return patchID;
    }

    public void setPatchID(String value) {
        this.patchID = value;
    }
}
