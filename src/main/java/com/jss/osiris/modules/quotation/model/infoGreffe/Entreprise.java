package com.jss.osiris.modules.quotation.model.infoGreffe;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "InfogreffeEntreprise")
public class Entreprise {

    @Id
    private String siren;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_greffe_infogreffe")
    private GreffeInfogreffe greffeInfogreffe;

    private String typeLiasseEDI;
    private String denomination;

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String value) {
        this.denomination = value;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String value) {
        this.siren = value;
    }

    public GreffeInfogreffe getGreffeInfogreffe() {
        return greffeInfogreffe;
    }

    public void setGreffeInfogreffe(GreffeInfogreffe value) {
        this.greffeInfogreffe = value;
    }

    public String getTypeLiasseEDI() {
        return typeLiasseEDI;
    }

    public void setTypeLiasseEDI(String value) {
        this.typeLiasseEDI = value;
    }
}
