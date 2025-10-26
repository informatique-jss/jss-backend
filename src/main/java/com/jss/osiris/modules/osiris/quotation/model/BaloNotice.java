package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateDeserializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = { @Index(name = "idx_balo_notice_siren", columnList = "siren") })
public class BaloNotice {

    @Column(length = 100)
    @Id
    @JsonProperty("id_annonce")
    private String idAnnonce;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    private LocalDate dateparution;

    @Transient
    @JsonProperty("siren")
    private List<String> sirenIn;

    @JsonProperty("sirenOut")
    private String siren;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("facette_categorie_libelle")
    private String facetteCategorieLibelle;

    public String getIdAnnonce() {
        return idAnnonce;
    }

    public void setIdAnnonce(String idAnnonce) {
        this.idAnnonce = idAnnonce;
    }

    public LocalDate getDateparution() {
        return dateparution;
    }

    public void setDateparution(LocalDate dateparution) {
        this.dateparution = dateparution;
    }

    public List<String> getSirenIn() {
        return sirenIn;
    }

    public void setSirenIn(List<String> sirenIn) {
        this.sirenIn = sirenIn;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getFacetteCategorieLibelle() {
        return facetteCategorieLibelle;
    }

    public void setFacetteCategorieLibelle(String facetteCategorieLibelle) {
        this.facetteCategorieLibelle = facetteCategorieLibelle;
    }

}