package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.quotation.model.RejectionCause;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@DoNotAudit
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = {
        @Index(name = "idx_regularization_request_formalite", columnList = "id_formalite_guichet_unique") })
public class RegularizationRequest implements Serializable {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "regularizationRequests" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    private String status;

    @OneToMany(mappedBy = "regularizationRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "regularizationRequest" }, allowSetters = true)
    private List<RegularizationObject> regularizationObjects;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_validation_request")
    @JsonIgnoreProperties(value = { "formaliteGuichetUnique" }, allowSetters = true)
    @JsonProperty("validationsRequest")
    @JsonAlias({ "annualAccountValidationRequest", "acteDepositValidationRequest", "associationValidationRequest" })
    private ValidationRequest validationRequest;

    private String deadline;
    private Boolean isActivityRegularization;
    private String mandatoryPartner;
    private String forbiddenPartner;
    private Boolean isRegularizedFromIhm;
    private String created;
    private String updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rejection_cause")
    private RejectionCause rejectionCause;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RegularizationObject> getRegularizationObjects() {
        return regularizationObjects;
    }

    public void setRegularizationObjects(List<RegularizationObject> regularizationObjects) {
        this.regularizationObjects = regularizationObjects;
    }

    public ValidationRequest getValidationRequest() {
        return validationRequest;
    }

    public void setValidationRequest(ValidationRequest validationRequest) {
        this.validationRequest = validationRequest;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsActivityRegularization() {
        return isActivityRegularization;
    }

    public void setIsActivityRegularization(Boolean isActivityRegularization) {
        this.isActivityRegularization = isActivityRegularization;
    }

    public String getMandatoryPartner() {
        return mandatoryPartner;
    }

    public void setMandatoryPartner(String mandatoryPartner) {
        this.mandatoryPartner = mandatoryPartner;
    }

    public String getForbiddenPartner() {
        return forbiddenPartner;
    }

    public void setForbiddenPartner(String forbiddenPartner) {
        this.forbiddenPartner = forbiddenPartner;
    }

    public Boolean getIsRegularizedFromIhm() {
        return isRegularizedFromIhm;
    }

    public void setIsRegularizedFromIhm(Boolean isRegularizedFromIhm) {
        this.isRegularizedFromIhm = isRegularizedFromIhm;
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

    public RejectionCause getRejectionCause() {
        return rejectionCause;
    }

    public void setRejectionCause(RejectionCause rejectionCause) {
        this.rejectionCause = rejectionCause;
    }

}