package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisEmiter {

    @JacksonXmlProperty(localName = "code_abonne")
    private String codeAbonne;

    @JacksonXmlProperty(localName = "mot_passe")
    private String motDePasse;

    @JacksonXmlProperty(localName = "code_requete")
    private KbisRequestCode codeRequete;

    public KbisEmiter() {
    }

    public KbisEmiter(String codeAbonne, String motDePasse, KbisRequestCode codeRequete) {
        this.codeAbonne = codeAbonne;
        this.motDePasse = motDePasse;
        this.codeRequete = codeRequete;
    }

    public String getCodeAbonne() {
        return codeAbonne;
    }

    public void setCodeAbonne(String codeAbonne) {
        this.codeAbonne = codeAbonne;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public KbisRequestCode getCodeRequete() {
        return codeRequete;
    }

    public void setCodeRequete(KbisRequestCode codeRequete) {
        this.codeRequete = codeRequete;
    }

}