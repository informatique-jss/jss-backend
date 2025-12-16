package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisGetProduitsWebServicesXML {
    @JacksonXmlProperty(isAttribute = true, localName = "soapenv:encodingStyle")
    private final String encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";

    @JacksonXmlProperty(localName = "arg0")
    private KbisArg arg0;

    public KbisGetProduitsWebServicesXML(KbisArg arg0) {
        this.arg0 = arg0;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public KbisArg getArg0() {
        return arg0;
    }

    public void setArg0(KbisArg arg0) {
        this.arg0 = arg0;
    }

}