package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope", namespace = "soap")
public class KbisSoapEnveloppe {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:soap")
    private final String soapNamespace = "http://schemas.xmlsoap.org/soap/envelope/";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:ns2")
    private final String serviceNamespace = "http://infogreffe.fr/ws";

    @JacksonXmlProperty(localName = "Header", namespace = "soap")
    private final Object soapHeader = null;

    @JacksonXmlProperty(localName = "Body", namespace = "soap")
    private KbisSoapBody body;

    public KbisSoapEnveloppe(KbisSoapBody body) {
        this.body = body;
    }

    public String getSoapNamespace() {
        return soapNamespace;
    }

    public String getServiceNamespace() {
        return serviceNamespace;
    }

    public Object getSoapHeader() {
        return soapHeader;
    }

    public KbisSoapBody getBody() {
        return body;
    }

    public void setBody(KbisSoapBody body) {
        this.body = body;
    }

}