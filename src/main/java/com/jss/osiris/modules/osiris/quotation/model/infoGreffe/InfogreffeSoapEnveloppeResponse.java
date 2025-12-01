package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfogreffeSoapEnveloppeResponse {

    @JacksonXmlProperty(localName = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private SoapResponseBody body;

    public SoapResponseBody getBody() {
        return body;
    }

    public void setBody(SoapResponseBody body) {
        this.body = body;
    }
}
