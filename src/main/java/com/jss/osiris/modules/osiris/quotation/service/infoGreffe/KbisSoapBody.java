package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisSoapBody {

    @JacksonXmlProperty(localName = "urn:getProduitsWebServicesXML")
    private KbisGetProduitsWebServicesXML operation;

    public KbisSoapBody(KbisGetProduitsWebServicesXML op) {
        this.operation = op;
    }

    public KbisGetProduitsWebServicesXML getOperation() {
        return operation;
    }

    public void setOperation(KbisGetProduitsWebServicesXML operation) {
        this.operation = operation;
    }

}