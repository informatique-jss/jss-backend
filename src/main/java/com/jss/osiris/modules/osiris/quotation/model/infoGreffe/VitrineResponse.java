package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

// 1. L'objet métier contenu dans le Body de la réponse
@JacksonXmlRootElement(localName = "vitrineResponse", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VitrineResponse {

    @JacksonXmlProperty(localName = "entreprise")
    private EntrepriseKbis entreprise;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bilan")
    private List<Bilan> bilans;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "document")
    private List<InfogreffeDocument> documents;

    public EntrepriseKbis getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(EntrepriseKbis entreprise) {
        this.entreprise = entreprise;
    }

    public List<Bilan> getBilans() {
        return bilans;
    }

    public void setBilans(List<Bilan> bilans) {
        this.bilans = bilans;
    }

    public List<InfogreffeDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<InfogreffeDocument> documents) {
        this.documents = documents;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class EntrepriseKbis {
    @JacksonXmlProperty(localName = "denomination")
    private String denomination;

    @JacksonXmlProperty(localName = "siren")
    private String siren;

    @JacksonXmlProperty(localName = "statut")
    private String statut;

    @JacksonXmlProperty(localName = "formeJuridique")
    private FormeJuridique formeJuridique;

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

}

class FormeJuridique {
    @JacksonXmlProperty(localName = "code")
    private String code;
    @JacksonXmlProperty(localName = "libelle")
    private String libelle;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

}

class ModesDiffusion {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "mode")
    private List<String> mode;

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Bilan {
    @JacksonXmlProperty(localName = "dateCloture")
    private String dateCloture;

    @JacksonXmlProperty(localName = "numeroDepot")
    private String numeroDepot;

    @JacksonXmlProperty(localName = "document")
    private DocumentRef documentRef;

    public String getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(String dateCloture) {
        this.dateCloture = dateCloture;
    }

    public String getNumeroDepot() {
        return numeroDepot;
    }

    public void setNumeroDepot(String numeroDepot) {
        this.numeroDepot = numeroDepot;
    }

    public DocumentRef getDocumentRef() {
        return documentRef;
    }

    public void setDocumentRef(DocumentRef documentRef) {
        this.documentRef = documentRef;
    }

}

class DocumentRef {
    @JacksonXmlProperty(isAttribute = true, localName = "ref")
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

}