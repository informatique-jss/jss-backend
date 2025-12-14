package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisSoapResponseBody {

    @JacksonXmlProperty(localName = "AppelServiceResponse", namespace = "ns2")
    private KbisReponseService reponseService;

    public KbisReponseService getReponseService() {
        return reponseService;
    }

    public void setReponseService(KbisReponseService reponseService) {
        this.reponseService = reponseService;
    }

}