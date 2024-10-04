package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonTimestampMillisecondDeserializer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@IdClass(EvenementInfogreffeEntityKey.class)
@Table(indexes = { @Index(name = "idx_evenement_infogreffe_formalite", columnList = "createdDate, codeEtat") })
public class EvenementInfogreffe {

    @Transient
    @JsonDeserialize(using = JacksonTimestampMillisecondDeserializer.class)
    private LocalDateTime date;

    @Id
    private String codeEtat;

    @Id
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "evenementInfogreffe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "evenementInfogreffe" }, allowSetters = true)
    private List<DocumentAssocieInfogreffe> documentsAssocies;

    @Column(columnDefinition = "TEXT")
    private String evtCommentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_infogreffe")
    @JsonIgnoreProperties(value = { "evenements" }, allowSetters = true)
    private FormaliteInfogreffe formaliteInfogreffe;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCodeEtat() {
        return codeEtat;
    }

    public void setCodeEtat(String codeEtat) {
        this.codeEtat = codeEtat;
    }

    public String getEvtCommentaire() {
        return evtCommentaire;
    }

    public void setEvtCommentaire(String evtCommentaire) {
        this.evtCommentaire = evtCommentaire;
    }

    public FormaliteInfogreffe getFormaliteInfogreffe() {
        return formaliteInfogreffe;
    }

    public void setFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe) {
        this.formaliteInfogreffe = formaliteInfogreffe;
    }

    public List<DocumentAssocieInfogreffe> getDocumentsAssocies() {
        return documentsAssocies;
    }

    public void setDocumentsAssocies(List<DocumentAssocieInfogreffe> documentsAssocies) {
        this.documentsAssocies = documentsAssocies;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

}