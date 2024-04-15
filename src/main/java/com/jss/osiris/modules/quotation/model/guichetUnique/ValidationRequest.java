package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RejectionReason;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ValidationsRequestStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@DoNotAudit
@JsonIgnoreProperties
@Table(indexes = {
        @Index(name = "idx_validation_request_formalite", columnList = "id_formalite_guichet_unique") })
public class ValidationRequest {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "validationsRequests" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    private Integer validationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_validation_request")
    private ValidationsRequestStatus status;

    @ManyToMany
    @JoinTable(name = "asso_validation_request_rejection", joinColumns = @JoinColumn(name = "id_validation_request"), inverseJoinColumns = @JoinColumn(name = "code_rejection_reason"))
    private List<RejectionReason> rejectionReasons;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "id_partenaire")
    private Partenaire partner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "id_partner_center")
    private PartnerCenter partnerCenter;

    private String closestRegularizationRequestExpirationDate;

    private String lastInternalObservation;
    private String statusDate;
    private String created;
    private String updated;

    @Column(columnDefinition = "TEXT")
    private String validationObservation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FormaliteGuichetUnique getFormaliteGuichetUnique() {
        return formaliteGuichetUnique;
    }

    public void setFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique) {
        this.formaliteGuichetUnique = formaliteGuichetUnique;
    }

    public Integer getValidationNumber() {
        return validationNumber;
    }

    public void setValidationNumber(Integer validationNumber) {
        this.validationNumber = validationNumber;
    }

    public ValidationsRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationsRequestStatus status) {
        this.status = status;
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<RejectionReason> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public Partenaire getPartner() {
        return partner;
    }

    public void setPartner(Partenaire partner) {
        this.partner = partner;
    }

    public PartnerCenter getPartnerCenter() {
        return partnerCenter;
    }

    public void setPartnerCenter(PartnerCenter partnerCenter) {
        this.partnerCenter = partnerCenter;
    }

    public String getClosestRegularizationRequestExpirationDate() {
        return closestRegularizationRequestExpirationDate;
    }

    public void setClosestRegularizationRequestExpirationDate(String closestRegularizationRequestExpirationDate) {
        this.closestRegularizationRequestExpirationDate = closestRegularizationRequestExpirationDate;
    }

    public String getLastInternalObservation() {
        return lastInternalObservation;
    }

    public void setLastInternalObservation(String lastInternalObservation) {
        this.lastInternalObservation = lastInternalObservation;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
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

    public String getValidationObservation() {
        return validationObservation;
    }

    public void setValidationObservation(String validationObservation) {
        this.validationObservation = validationObservation;
    }

}