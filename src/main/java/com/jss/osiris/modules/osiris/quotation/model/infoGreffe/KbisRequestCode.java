package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisRequestCode {

    @JacksonXmlProperty(localName = "type_profil")
    private final String typeProfil = "A"; // Abonnés Infogreffe

    @JacksonXmlProperty(localName = "origine_emetteur")
    private final String origineEmetteur = "IC";

    @JacksonXmlProperty(localName = "nature_requete")
    private final String natureRequete = "C"; // Commande

    @JacksonXmlProperty(localName = "type_document")
    private final String typeDocument = "KB"; // Extrait RCS (KBis)

    @JacksonXmlProperty(localName = "type_requete")
    private final String typeRequete = "S";

    @JacksonXmlProperty(localName = "mode_diffusion")
    private KbisBrodcastMode modeDiffusion;

    @JacksonXmlProperty(localName = "media")
    private final String media = "WS"; // Web Services

    public KbisRequestCode() {
        this.modeDiffusion = new KbisBrodcastMode();
    }

    public String getTypeProfil() {
        return typeProfil;
    }

    public String getOrigineEmetteur() {
        return origineEmetteur;
    }

    public String getNatureRequete() {
        return natureRequete;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public String getTypeRequete() {
        return typeRequete;
    }

    public KbisBrodcastMode getModeDiffusion() {
        return modeDiffusion;
    }

    public void setModeDiffusion(KbisBrodcastMode modeDiffusion) {
        this.modeDiffusion = modeDiffusion;
    }

    public String getMedia() {
        return media;
    }

}