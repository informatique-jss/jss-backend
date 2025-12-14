package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisSoapBody {

    @JacksonXmlProperty(localName = "AppelService", namespace = "ns2")
    private KbisCall appelService;

    public KbisSoapBody(KbisCall appelService) {
        this.appelService = appelService;
    }

    public KbisCall getAppelService() {
        return appelService;
    }

    public void setAppelService(KbisCall appelService) {
        this.appelService = appelService;
    }

}