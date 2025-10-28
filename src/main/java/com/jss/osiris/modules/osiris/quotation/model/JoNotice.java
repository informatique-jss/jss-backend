package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateDeserializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = { @Index(name = "idx_jo_notice_titre", columnList = "titre") })
public class JoNotice {

    @Id
    @Column(length = 150)
    private String id;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    private LocalDate dateparution;

    @JsonProperty("association_type_libelle")
    @Column(length = 500)
    private String associationTypeLibelle;

    @JsonProperty("annonce_type_facette")
    @Column(length = 500)
    private String annonce_type_facette;

    @JsonProperty("typeavis")
    private String typeavis;

    @JsonProperty("numero_rna")
    private String numero_rna;

    @JsonProperty("titre")
    @Column(length = 2000)
    private String titre;

    public LocalDate getDateparution() {
        return dateparution;
    }

    public void setDateparution(LocalDate dateparution) {
        this.dateparution = dateparution;
    }

    public String getAssociationTypeLibelle() {
        return associationTypeLibelle;
    }

    public void setAssociationTypeLibelle(String associationTypeLibelle) {
        this.associationTypeLibelle = associationTypeLibelle;
    }

    public String getTypeavis() {
        return typeavis;
    }

    public void setTypeavis(String typeavis) {
        this.typeavis = typeavis;
    }

    public String getNumero_rna() {
        return numero_rna;
    }

    public void setNumero_rna(String numero_rna) {
        this.numero_rna = numero_rna;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnnonce_type_facette() {
        return annonce_type_facette;
    }

    public void setAnnonce_type_facette(String annonce_type_facette) {
        this.annonce_type_facette = annonce_type_facette;
    }

}