package com.jss.osiris.modules.quotation.model.infoGreffe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DocumentAssocieInfogreffe {
    @Id
    private String urlTelechargement;
    private String typeDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evenement_infogreffe")
    @JsonIgnoreProperties(value = { "documentAssocies" }, allowSetters = true)
    private EvenementInfogreffe evenementInfogreffe;

    public String getUrlTelechargement() {
        return urlTelechargement;
    }

    public void setUrlTelechargement(String urlTelechargement) {
        this.urlTelechargement = urlTelechargement;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public EvenementInfogreffe getEvenementInfogreffe() {
        return evenementInfogreffe;
    }

    public void setEvenementInfogreffe(EvenementInfogreffe evenementInfogreffe) {
        this.evenementInfogreffe = evenementInfogreffe;
    }

}
