package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SoapResponseBody {

    @JacksonXmlProperty(localName = "vitrineResponse", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
    private VitrineResponse vitrineResponse;

    @JacksonXmlProperty(localName = "commandeResponse", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
    private KbisCommandResponse commandeResponse;

    @JacksonXmlProperty(localName = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private InfogreffeSoapFault fault;

    public VitrineResponse getVitrineResponse() {
        return vitrineResponse;
    }

    public void setVitrineResponse(VitrineResponse vitrineResponse) {
        this.vitrineResponse = vitrineResponse;
    }

    public InfogreffeSoapFault getFault() {
        return fault;
    }

    public void setFault(InfogreffeSoapFault fault) {
        this.fault = fault;
    }

    public KbisCommandResponse getCommandeResponse() {
        return commandeResponse;
    }

    public void setCommandeResponse(KbisCommandResponse commandeResponse) {
        this.commandeResponse = commandeResponse;
    }
}
