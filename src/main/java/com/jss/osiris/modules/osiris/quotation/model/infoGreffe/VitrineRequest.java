package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "vitrineRequest", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
public class VitrineRequest {

    @JacksonXmlProperty(localName = "siren")
    private String siren;

    public VitrineRequest() {
    }

    public VitrineRequest(String siren) {
        this.siren = siren;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }
}