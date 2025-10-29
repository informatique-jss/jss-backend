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
@Table(indexes = { @Index(name = "idx_bodacc_notice_siren", columnList = "siren"),
        @Index(name = "idx_bodacc_notice_date", columnList = "dateparution"),
        @Index(name = "idx_bodacc_notice_familleavis", columnList = "familleavis_lib,siren") })
public class BodaccNotice {

    @Column(length = 15)
    @Id
    private String id;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    private LocalDate dateparution;

    @JsonProperty("familleavis_lib")
    private String familleavisLib;

    @JsonProperty("typeavis_lib")
    private String typeavisLib;

    private String tribunal;

    @Column(length = 1)
    private String publicationavis;

    @Transient
    private List<String> registre;

    @Column(columnDefinition = "TEXT")
    private String modificationsgenerales;

    @Column(columnDefinition = "TEXT")
    private String jugement;

    @Column(columnDefinition = "TEXT")
    private String acte;

    @Column(columnDefinition = "TEXT")
    private String depot;

    @JsonProperty("url_complete")
    private String urlComplete;

    private Integer numeroannonce;

    @Column(length = 3)
    private String numerodepartement;

    @Column(length = 2000)
    private String commercant;

    private String siren;

    @JsonProperty("pdf_parution_subfolder")
    private Integer pdfParutionSubfolder;

    @Column(columnDefinition = "TEXT")
    private String acteradiationaurcs;

    @Column(columnDefinition = "TEXT")
    private String divers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateparution() {
        return dateparution;
    }

    public void setDateparution(LocalDate dateparution) {
        this.dateparution = dateparution;
    }

    public String getFamilleavisLib() {
        return familleavisLib;
    }

    public void setFamilleavisLib(String familleavisLib) {
        this.familleavisLib = familleavisLib;
    }

    public String getTypeavisLib() {
        return typeavisLib;
    }

    public void setTypeavisLib(String typeavisLib) {
        this.typeavisLib = typeavisLib;
    }

    public String getTribunal() {
        return tribunal;
    }

    public void setTribunal(String tribunal) {
        this.tribunal = tribunal;
    }

    public String getPublicationavis() {
        return publicationavis;
    }

    public void setPublicationavis(String publicationavis) {
        this.publicationavis = publicationavis;
    }

    public String getModificationsgenerales() {
        return modificationsgenerales;
    }

    public void setModificationsgenerales(String modificationsgenerales) {
        this.modificationsgenerales = modificationsgenerales;
    }

    public String getJugement() {
        return jugement;
    }

    public void setJugement(String jugement) {
        this.jugement = jugement;
    }

    public String getActe() {
        return acte;
    }

    public void setActe(String acte) {
        this.acte = acte;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getUrlComplete() {
        return urlComplete;
    }

    public void setUrlComplete(String urlComplete) {
        this.urlComplete = urlComplete;
    }

    public Integer getNumeroannonce() {
        return numeroannonce;
    }

    public void setNumeroannonce(Integer numeroannonce) {
        this.numeroannonce = numeroannonce;
    }

    public String getNumerodepartement() {
        return numerodepartement;
    }

    public void setNumerodepartement(String numerodepartement) {
        this.numerodepartement = numerodepartement;
    }

    public String getCommercant() {
        return commercant;
    }

    public void setCommercant(String commercant) {
        this.commercant = commercant;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public Integer getPdfParutionSubfolder() {
        return pdfParutionSubfolder;
    }

    public void setPdfParutionSubfolder(Integer pdfParutionSubfolder) {
        this.pdfParutionSubfolder = pdfParutionSubfolder;
    }

    public String getActeradiationaurcs() {
        return acteradiationaurcs;
    }

    public void setActeradiationaurcs(String acteradiationaurcs) {
        this.acteradiationaurcs = acteradiationaurcs;
    }

    public String getDivers() {
        return divers;
    }

    public void setDivers(String divers) {
        this.divers = divers;
    }

    public List<String> getRegistre() {
        return registre;
    }

    public void setRegistre(List<String> registre) {
        this.registre = registre;
    }

}