package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisDemand;

public class KbisCall {

    @JacksonXmlProperty(localName = "demande")
    private KbisDemand demande;

    public KbisCall(KbisDemand demande) {
        this.demande = demande;
    }

    public KbisDemand getDemande() {
        return demande;
    }

    public void setDemande(KbisDemand demande) {
        this.demande = demande;
    }

}