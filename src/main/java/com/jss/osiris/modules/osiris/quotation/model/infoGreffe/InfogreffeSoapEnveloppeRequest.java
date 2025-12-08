package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class InfogreffeSoapEnveloppeRequest {

    @JacksonXmlProperty(localName = "Header", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private String header = "";

    @JacksonXmlProperty(localName = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private InfogreffeSoapBody body;

    public InfogreffeSoapEnveloppeRequest(VitrineRequest request) {
        this.body = new InfogreffeSoapBody(request);
    }

    public InfogreffeSoapEnveloppeRequest(KbisCommandRequest request) {
        this.body = new InfogreffeSoapBody(request);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public InfogreffeSoapBody getBody() {
        return body;
    }

    public void setBody(InfogreffeSoapBody body) {
        this.body = body;
    }

}

class InfogreffeSoapBody {

    @JacksonXmlProperty(localName = "vitrineRequest", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
    private VitrineRequest vitrineRequest;

    @JacksonXmlProperty(localName = "commandeRequest", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
    private KbisCommandRequest commandeRequest;

    public InfogreffeSoapBody(VitrineRequest vitrineRequest) {
        this.vitrineRequest = vitrineRequest;
    }

    public InfogreffeSoapBody(KbisCommandRequest commandeRequest) {
        this.commandeRequest = commandeRequest;
    }

    public VitrineRequest getVitrineRequest() {
        return vitrineRequest;
    }

    public void setVitrineRequest(VitrineRequest vitrineRequest) {
        this.vitrineRequest = vitrineRequest;
    }

    public KbisCommandRequest getCommandeRequest() {
        return commandeRequest;
    }

    public void setCommandeRequest(KbisCommandRequest commandeRequest) {
        this.commandeRequest = commandeRequest;
    }

}