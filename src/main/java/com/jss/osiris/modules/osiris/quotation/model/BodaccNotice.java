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
@Table(indexes = { @Index(name = "idx_bodacc_notice_siren", columnList = "siren") })
public class BodaccNotice {

    @Column(length = 15)
    @Id
    private String id;

    @Column(length = 1)
    private String publicationavis;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    private LocalDate dateparution;

    private Integer numeroannonce;
    private String typeavis;

    @JsonProperty("typeavis_lib")
    private String typeavisLib;
    private String familleavis;

    @JsonProperty("familleavis_lib")
    private String familleavisLib;

    @Column(length = 3)
    private String numerodepartement;
    private String departementNomOfficiel;

    private Integer regionCode;
    private String regionNomOfficiel;
    private String tribunal;

    @Column(length = 1500)
    private String commercant;
    private String ville;

    @Transient
    private List<String> registre;

    private String siren;

    private String cp;

    @JsonProperty("pdf_parution_subfolder")
    private Integer pdfParutionSubfolder;

    @JsonProperty("ispdf_unitaire")
    private String isPdfUnitaire;

    @Column(columnDefinition = "TEXT")
    private String listepersonnes;

    @Column(columnDefinition = "TEXT")
    private String listeetablissements;

    @Column(columnDefinition = "TEXT")
    private String jugement;

    @Column(columnDefinition = "TEXT")
    private String acteradiationaurcs;

    @Column(columnDefinition = "TEXT")
    private String acte;

    @Column(columnDefinition = "TEXT")
    private String modificationsgenerales;

    @Column(columnDefinition = "TEXT")
    private String depot;

    @Column(columnDefinition = "TEXT")
    private String listeprecedentexploitant;

    @Column(columnDefinition = "TEXT")
    private String listeprecedentproprietaire;

    @Column(columnDefinition = "TEXT")
    private String divers;

    @Column(columnDefinition = "TEXT")
    private String parutionavisprecedent;

    @JsonProperty("url_complete")
    private String urlComplete;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicationavis() {
        return publicationavis;
    }

    public void setPublicationavis(String publicationavis) {
        this.publicationavis = publicationavis;
    }

    public Integer getNumeroannonce() {
        return numeroannonce;
    }

    public void setNumeroannonce(Integer numeroannonce) {
        this.numeroannonce = numeroannonce;
    }

    public String getTypeavis() {
        return typeavis;
    }

    public void setTypeavis(String typeavis) {
        this.typeavis = typeavis;
    }

    public String getTypeavisLib() {
        return typeavisLib;
    }

    public void setTypeavisLib(String typeavisLib) {
        this.typeavisLib = typeavisLib;
    }

    public String getFamilleavis() {
        return familleavis;
    }

    public void setFamilleavis(String familleavis) {
        this.familleavis = familleavis;
    }

    public String getFamilleavisLib() {
        return familleavisLib;
    }

    public void setFamilleavisLib(String familleavisLib) {
        this.familleavisLib = familleavisLib;
    }

    public String getNumerodepartement() {
        return numerodepartement;
    }

    public void setNumerodepartement(String numerodepartement) {
        this.numerodepartement = numerodepartement;
    }

    public String getDepartementNomOfficiel() {
        return departementNomOfficiel;
    }

    public void setDepartementNomOfficiel(String departementNomOfficiel) {
        this.departementNomOfficiel = departementNomOfficiel;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Integer regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionNomOfficiel() {
        return regionNomOfficiel;
    }

    public void setRegionNomOfficiel(String regionNomOfficiel) {
        this.regionNomOfficiel = regionNomOfficiel;
    }

    public String getTribunal() {
        return tribunal;
    }

    public void setTribunal(String tribunal) {
        this.tribunal = tribunal;
    }

    public String getCommercant() {
        return commercant;
    }

    public void setCommercant(String commercant) {
        this.commercant = commercant;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public List<String> getRegistre() {
        return registre;
    }

    public void setRegistre(List<String> registre) {
        this.registre = registre;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public Integer getPdfParutionSubfolder() {
        return pdfParutionSubfolder;
    }

    public void setPdfParutionSubfolder(Integer pdfParutionSubfolder) {
        this.pdfParutionSubfolder = pdfParutionSubfolder;
    }

    public String getListepersonnes() {
        return listepersonnes;
    }

    public void setListepersonnes(String listepersonnes) {
        this.listepersonnes = listepersonnes;
    }

    public String getListeetablissements() {
        return listeetablissements;
    }

    public void setListeetablissements(String listeetablissements) {
        this.listeetablissements = listeetablissements;
    }

    public String getJugement() {
        return jugement;
    }

    public void setJugement(String jugement) {
        this.jugement = jugement;
    }

    public String getActeradiationaurcs() {
        return acteradiationaurcs;
    }

    public void setActeradiationaurcs(String acteradiationaurcs) {
        this.acteradiationaurcs = acteradiationaurcs;
    }

    public String getModificationsgenerales() {
        return modificationsgenerales;
    }

    public void setModificationsgenerales(String modificationsgenerales) {
        this.modificationsgenerales = modificationsgenerales;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getListeprecedentexploitant() {
        return listeprecedentexploitant;
    }

    public void setListeprecedentexploitant(String listeprecedentexploitant) {
        this.listeprecedentexploitant = listeprecedentexploitant;
    }

    public String getListeprecedentproprietaire() {
        return listeprecedentproprietaire;
    }

    public void setListeprecedentproprietaire(String listeprecedentproprietaire) {
        this.listeprecedentproprietaire = listeprecedentproprietaire;
    }

    public String getDivers() {
        return divers;
    }

    public void setDivers(String divers) {
        this.divers = divers;
    }

    public String getParutionavisprecedent() {
        return parutionavisprecedent;
    }

    public void setParutionavisprecedent(String parutionavisprecedent) {
        this.parutionavisprecedent = parutionavisprecedent;
    }

    public String getUrlComplete() {
        return urlComplete;
    }

    public void setUrlComplete(String urlComplete) {
        this.urlComplete = urlComplete;
    }

    public LocalDate getDateparution() {
        return dateparution;
    }

    public void setDateparution(LocalDate dateparution) {
        this.dateparution = dateparution;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siret) {
        this.siren = siret;
    }

    public String getActe() {
        return acte;
    }

    public void setActe(String acte) {
        this.acte = acte;
    }

    public String getIsPdfUnitaire() {
        return isPdfUnitaire;
    }

    public void setIsPdfUnitaire(String isPdfUnitaire) {
        this.isPdfUnitaire = isPdfUnitaire;
    }

}