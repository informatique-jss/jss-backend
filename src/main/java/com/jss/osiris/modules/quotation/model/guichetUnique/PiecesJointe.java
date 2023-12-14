package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DocumentExtension;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TaciteReconduction;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;

@Entity
@Table(indexes = {
        @Index(name = "idx_piece_jointe_content", columnList = "id_content"),
        @Index(name = "idx_piece_jointe_id", columnList = "attachmentId") })
public class PiecesJointe implements Serializable {

    @Column(length = 255)
    private String nomDocument;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_content")
    @JsonIgnoreProperties(value = { "piecesJointes" }, allowSetters = true)
    Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_document")
    TypeDocument typeDocument;

    @Column(length = 255)
    private String langueDocument;

    @Column(length = 255)
    private String numeroPiece;

    @Column()
    private LocalDate debutValidite;

    @Column()
    private LocalDate finValidite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tacite_reconduction")
    TaciteReconduction taciteReconduction;

    @Column(length = 255)
    private String autoriteDelivrance;

    @Column(length = 255)
    private String paysLieuDelivrance;

    @Column(length = 255)
    private String communeLieuDelivrance;

    @Column(length = 255)
    private String observations;

    @Column(length = 255)
    private String documentBase64;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_document_extension")
    DocumentExtension documentExtension;

    @Column(length = 255)
    private String sousTypeDocument;

    @Column(length = 255)
    private String path;

    @Id
    @JsonAlias({ "id" })
    private String attachmentId;

    @Column(length = 255)
    private String codeInseeCommuneLieuDelivrance;

    @Column(length = 255)
    private String codePostalLieuDelivrance;

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getLangueDocument() {
        return langueDocument;
    }

    public void setLangueDocument(String langueDocument) {
        this.langueDocument = langueDocument;
    }

    public String getNumeroPiece() {
        return numeroPiece;
    }

    public void setNumeroPiece(String numeroPiece) {
        this.numeroPiece = numeroPiece;
    }

    public LocalDate getDebutValidite() {
        return debutValidite;
    }

    public void setDebutValidite(LocalDate debutValidite) {
        this.debutValidite = debutValidite;
    }

    public LocalDate getFinValidite() {
        return finValidite;
    }

    public void setFinValidite(LocalDate finValidite) {
        this.finValidite = finValidite;
    }

    public TaciteReconduction getTaciteReconduction() {
        return taciteReconduction;
    }

    public void setTaciteReconduction(TaciteReconduction taciteReconduction) {
        this.taciteReconduction = taciteReconduction;
    }

    public String getAutoriteDelivrance() {
        return autoriteDelivrance;
    }

    public void setAutoriteDelivrance(String autoriteDelivrance) {
        this.autoriteDelivrance = autoriteDelivrance;
    }

    public String getPaysLieuDelivrance() {
        return paysLieuDelivrance;
    }

    public void setPaysLieuDelivrance(String paysLieuDelivrance) {
        this.paysLieuDelivrance = paysLieuDelivrance;
    }

    public String getCommuneLieuDelivrance() {
        return communeLieuDelivrance;
    }

    public void setCommuneLieuDelivrance(String communeLieuDelivrance) {
        this.communeLieuDelivrance = communeLieuDelivrance;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getDocumentBase64() {
        return documentBase64;
    }

    public void setDocumentBase64(String documentBase64) {
        this.documentBase64 = documentBase64;
    }

    public DocumentExtension getDocumentExtension() {
        return documentExtension;
    }

    public void setDocumentExtension(DocumentExtension documentExtension) {
        this.documentExtension = documentExtension;
    }

    public String getSousTypeDocument() {
        return sousTypeDocument;
    }

    public void setSousTypeDocument(String sousTypeDocument) {
        this.sousTypeDocument = sousTypeDocument;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getCodeInseeCommuneLieuDelivrance() {
        return codeInseeCommuneLieuDelivrance;
    }

    public void setCodeInseeCommuneLieuDelivrance(String codeInseeCommuneLieuDelivrance) {
        this.codeInseeCommuneLieuDelivrance = codeInseeCommuneLieuDelivrance;
    }

    public String getCodePostalLieuDelivrance() {
        return codePostalLieuDelivrance;
    }

    public void setCodePostalLieuDelivrance(String codePostalLieuDelivrance) {
        this.codePostalLieuDelivrance = codePostalLieuDelivrance;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
