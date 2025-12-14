package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisResult {

    @JacksonXmlProperty(localName = "code_retour")
    private String codeRetour;

    @JacksonXmlProperty(localName = "libelle_retour")
    private String libelleRetour;

    @JacksonXmlProperty(localName = "num_commande_infogreffe")
    private String numCommandeInfogreffe;

    @JacksonXmlProperty(localName = "url_acces")
    private String urlAcces;

    public boolean isOrderInProgress() {
        return "016".equals(codeRetour);
    }

    public String getCodeRetour() {
        return codeRetour;
    }

    public void setCodeRetour(String codeRetour) {
        this.codeRetour = codeRetour;
    }

    public String getLibelleRetour() {
        return libelleRetour;
    }

    public void setLibelleRetour(String libelleRetour) {
        this.libelleRetour = libelleRetour;
    }

    public String getNumCommandeInfogreffe() {
        return numCommandeInfogreffe;
    }

    public void setNumCommandeInfogreffe(String numCommandeInfogreffe) {
        this.numCommandeInfogreffe = numCommandeInfogreffe;
    }

    public String getUrlAcces() {
        return urlAcces;
    }

    public void setUrlAcces(String urlAcces) {
        this.urlAcces = urlAcces;
    }

}