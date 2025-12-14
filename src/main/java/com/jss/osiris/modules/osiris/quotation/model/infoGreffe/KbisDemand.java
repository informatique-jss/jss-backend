package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "demande")
public class KbisDemand {

    @JacksonXmlProperty(localName = "emetteur")
    private KbisEmiter emetteur;

    @JacksonXmlProperty(localName = "commande")
    private KbisOrder commande;

    public KbisDemand() {
    }

    public KbisDemand(KbisEmiter emetteur, KbisOrder commande) {
        this.emetteur = emetteur;
        this.commande = commande;
    }

    public KbisEmiter getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(KbisEmiter emetteur) {
        this.emetteur = emetteur;
    }

    public KbisOrder getCommande() {
        return commande;
    }

    public void setCommande(KbisOrder commande) {
        this.commande = commande;
    }

}