package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope", namespace = "soap")
public class KbisSoapResponseEnvelope {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:soap")
    private String soapNamespace;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:ns2")
    private String serviceNamespace;

    @JacksonXmlProperty(localName = "Header", namespace = "soap")
    private Object soapHeader;

    @JacksonXmlProperty(localName = "Body", namespace = "soap")
    private KbisSoapResponseBody body;

    public String getSoapNamespace() {
        return soapNamespace;
    }

    public void setSoapNamespace(String soapNamespace) {
        this.soapNamespace = soapNamespace;
    }

    public String getServiceNamespace() {
        return serviceNamespace;
    }

    public void setServiceNamespace(String serviceNamespace) {
        this.serviceNamespace = serviceNamespace;
    }

    public Object getSoapHeader() {
        return soapHeader;
    }

    public void setSoapHeader(Object soapHeader) {
        this.soapHeader = soapHeader;
    }

    public KbisSoapResponseBody getBody() {
        return body;
    }

    public void setBody(KbisSoapResponseBody body) {
        this.body = body;
    }

}