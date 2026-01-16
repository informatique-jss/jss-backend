package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.osiris.tiers.model.dto.ResponsableDto;
import com.jss.osiris.modules.osiris.tiers.model.dto.TiersDto;

public class PaymentSearch {

    private LocalDate startDate;
    private LocalDate endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    @JsonProperty("isHideAssociatedPayments")
    private boolean isHideAssociatedPayments;
    @JsonProperty("isHideCancelledPayments")
    private boolean isHideCancelledPayments;
    @JsonProperty("isHideAppoint")
    private boolean isHideAppoint;
    @JsonProperty("isHideNoOfx")
    private boolean isHideNoOfx;
    private TiersDto tiers;
    private ResponsableDto responsable;
    private Boolean isAssociated;
    private Boolean isCancelled;
    private Boolean isAppoint;

    private Integer idPayment;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isHideAssociatedPayments() {
        return isHideAssociatedPayments;
    }

    public void setHideAssociatedPayments(boolean isHideAssociatedPayments) {
        this.isHideAssociatedPayments = isHideAssociatedPayments;
    }

    public boolean isHideCancelledPayments() {
        return isHideCancelledPayments;
    }

    public void setHideCancelledPayments(boolean isHideCancelledPayments) {
        this.isHideCancelledPayments = isHideCancelledPayments;
    }

    public boolean isHideAppoint() {
        return isHideAppoint;
    }

    public void setHideAppoint(boolean isHideAppoint) {
        this.isHideAppoint = isHideAppoint;
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }

    public boolean isHideNoOfx() {
        return isHideNoOfx;
    }

    public void setHideNoOfx(boolean isHideNoOfx) {
        this.isHideNoOfx = isHideNoOfx;
    }

    public TiersDto getTiers() {
        return tiers;
    }

    public void setTiers(TiersDto tiers) {
        this.tiers = tiers;
    }

    public ResponsableDto getResponsable() {
        return responsable;
    }

    public void setResponsable(ResponsableDto responsable) {
        this.responsable = responsable;
    }

    public Boolean getIsAssociated() {
        return isAssociated;
    }

    public void setIsAssociated(Boolean isAssociated) {
        this.isAssociated = isAssociated;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean getIsAppoint() {
        return isAppoint;
    }

    public void setIsAppoint(Boolean isAppoint) {
        this.isAppoint = isAppoint;
    }
}
