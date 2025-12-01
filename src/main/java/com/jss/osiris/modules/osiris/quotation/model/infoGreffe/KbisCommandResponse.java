package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "commandeResponse", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KbisCommandResponse {

    @JacksonXmlProperty(localName = "url")
    private String urlTelechargement;

    @JacksonXmlProperty(localName = "montantFacturation")
    private MontantFacturation montantFacturation;

    public String getUrlTelechargement() {
        return urlTelechargement;
    }

    public void setUrlTelechargement(String urlTelechargement) {
        this.urlTelechargement = urlTelechargement;
    }

    public MontantFacturation getMontantFacturation() {
        return montantFacturation;
    }

    public void setMontantFacturation(MontantFacturation montantFacturation) {
        this.montantFacturation = montantFacturation;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class MontantFacturation {
    @JacksonXmlProperty(localName = "prixUnitaireHT")
    private String prixUnitaireHT;

    @JacksonXmlProperty(localName = "devise")
    private String devise;

    public String getPrixUnitaireHT() {
        return prixUnitaireHT;
    }

    public void setPrixUnitaireHT(String prixUnitaireHT) {
        this.prixUnitaireHT = prixUnitaireHT;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

}
