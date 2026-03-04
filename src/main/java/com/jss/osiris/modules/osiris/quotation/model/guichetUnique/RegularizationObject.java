package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DoNotAudit
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegularizationObject implements Serializable {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regularization_request")
    private RegularizationRequest regularizationRequest;

    private String type;
    private String observation;
    private String fieldName;
    private String fileName;
    private Double totalAmountExpected;
    private Integer regularizationFeeAmount;
    private String created;
    private String updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getTotalAmountExpected() {
        return totalAmountExpected;
    }

    public void setTotalAmountExpected(Double totalAmountExpected) {
        this.totalAmountExpected = totalAmountExpected;
    }

    public Integer getRegularizationFeeAmount() {
        return regularizationFeeAmount;
    }

    public void setRegularizationFeeAmount(Integer regularizationFeeAmount) {
        this.regularizationFeeAmount = regularizationFeeAmount;
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

    public RegularizationRequest getRegularizationRequest() {
        return regularizationRequest;
    }

    public void setRegularizationRequest(RegularizationRequest regularizationRequest) {
        this.regularizationRequest = regularizationRequest;
    }

}