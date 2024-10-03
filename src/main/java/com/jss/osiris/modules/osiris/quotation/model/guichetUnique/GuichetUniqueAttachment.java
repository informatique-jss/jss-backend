package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DoNotAudit
public class GuichetUniqueAttachment {
    @Id
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    private Double size;
    private boolean regularisable;
    private String status;
    private boolean hasValidSignature;
    private String depositor;
    private String compliance;
    private String replacedBy;
    private String replacing;
    private Integer fromAttachmentID;
    private String formatPDF;
    private boolean invalidated;
    private String invalidatedDate;
    private String invalidatedNameCode;
    private String invalidatedReason;
    private String invalidatedName;
    private String nomDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_document")
    private TypeDocument typeDocument;

    private String langueDocument;
    private String numeroPiece;
    private String debutValidite;
    private String documentDate;
    private String finValidite;
    private String taciteReconduction;
    private String autoriteDelivrance;
    private String paysLieuDelivrance;
    private String communeLieuDelivrance;
    private String observations;
    private String documentBase64;
    private String documentExtension;
    private String sousTypeDocument;
    private String path;
    private String attachmentID;
    private String codeInseeCommuneLieuDelivrance;
    private String codePostalLieuDelivrance;
    private boolean confidentiel;
    private String created;
    private String updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public boolean isRegularisable() {
        return regularisable;
    }

    public void setRegularisable(boolean regularisable) {
        this.regularisable = regularisable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHasValidSignature() {
        return hasValidSignature;
    }

    public void setHasValidSignature(boolean hasValidSignature) {
        this.hasValidSignature = hasValidSignature;
    }

    public String getDepositor() {
        return depositor;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    public String getCompliance() {
        return compliance;
    }

    public void setCompliance(String compliance) {
        this.compliance = compliance;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    public String getReplacing() {
        return replacing;
    }

    public void setReplacing(String replacing) {
        this.replacing = replacing;
    }

    public Integer getFromAttachmentID() {
        return fromAttachmentID;
    }

    public void setFromAttachmentID(Integer fromAttachmentID) {
        this.fromAttachmentID = fromAttachmentID;
    }

    public String getFormatPDF() {
        return formatPDF;
    }

    public void setFormatPDF(String formatPDF) {
        this.formatPDF = formatPDF;
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }

    public String getInvalidatedDate() {
        return invalidatedDate;
    }

    public void setInvalidatedDate(String invalidatedDate) {
        this.invalidatedDate = invalidatedDate;
    }

    public String getInvalidatedNameCode() {
        return invalidatedNameCode;
    }

    public void setInvalidatedNameCode(String invalidatedNameCode) {
        this.invalidatedNameCode = invalidatedNameCode;
    }

    public String getInvalidatedReason() {
        return invalidatedReason;
    }

    public void setInvalidatedReason(String invalidatedReason) {
        this.invalidatedReason = invalidatedReason;
    }

    public String getInvalidatedName() {
        return invalidatedName;
    }

    public void setInvalidatedName(String invalidatedName) {
        this.invalidatedName = invalidatedName;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
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

    public String getDebutValidite() {
        return debutValidite;
    }

    public void setDebutValidite(String debutValidite) {
        this.debutValidite = debutValidite;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getFinValidite() {
        return finValidite;
    }

    public void setFinValidite(String finValidite) {
        this.finValidite = finValidite;
    }

    public String getTaciteReconduction() {
        return taciteReconduction;
    }

    public void setTaciteReconduction(String taciteReconduction) {
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

    public String getDocumentExtension() {
        return documentExtension;
    }

    public void setDocumentExtension(String documentExtension) {
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

    public String getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
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

    public boolean isConfidentiel() {
        return confidentiel;
    }

    public void setConfidentiel(boolean confidentiel) {
        this.confidentiel = confidentiel;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public FormaliteGuichetUnique getFormaliteGuichetUnique() {
        return formaliteGuichetUnique;
    }

    public void setFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique) {
        this.formaliteGuichetUnique = formaliteGuichetUnique;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

}
