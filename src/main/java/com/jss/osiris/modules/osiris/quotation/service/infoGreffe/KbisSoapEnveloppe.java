package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "soapenv:Envelope")
public class KbisSoapEnveloppe {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:soapenv")
    private final String soapenv = "http://schemas.xmlsoap.org/soap/envelope/";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:urn")
    private final String urn = "urn:WebServicesProduits";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    private final String xsi = "http://www.w3.org/2001/XMLSchema-instance";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsd")
    private final String xsd = "http://www.w3.org/2001/XMLSchema";

    @JacksonXmlProperty(localName = "soapenv:Header")
    private String header = "";

    @JacksonXmlProperty(localName = "soapenv:Body")
    private KbisSoapBody body;

    public KbisSoapEnveloppe(KbisSoapBody body) {
        this.body = body;
    }

    public String getSoapenv() {
        return soapenv;
    }

    public String getUrn() {
        return urn;
    }

    public String getXsi() {
        return xsi;
    }

    public String getXsd() {
        return xsd;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public KbisSoapBody getBody() {
        return body;
    }

    public void setBody(KbisSoapBody body) {
        this.body = body;
    }

}