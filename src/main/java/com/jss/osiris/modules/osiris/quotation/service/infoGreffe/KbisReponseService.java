package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisReponseService {

    @JacksonXmlProperty(localName = "resultat")
    private KbisResult resultat;

    public KbisResult getResultat() {
        return resultat;
    }

    public void setResultat(KbisResult resultat) {
        this.resultat = resultat;
    }

}