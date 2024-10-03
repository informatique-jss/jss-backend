package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;

public class BankTransfertSearch {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    @JsonProperty("isHideExportedBankTransfert")
    private boolean isHideExportedBankTransfert;
    @JsonProperty("isDisplaySelectedForExportBankTransfert")
    private boolean isDisplaySelectedForExportBankTransfert;

    private Integer idBankTransfert;

    private Provider provider;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
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

    public boolean isHideExportedBankTransfert() {
        return isHideExportedBankTransfert;
    }

    public void setHideExportedBankTransfert(boolean isHideExportedBankTransfert) {
        this.isHideExportedBankTransfert = isHideExportedBankTransfert;
    }

    public boolean isDisplaySelectedForExportBankTransfert() {
        return isDisplaySelectedForExportBankTransfert;
    }

    public void setDisplaySelectedForExportBankTransfert(boolean isDisplaySelectedForExportBankTransfert) {
        this.isDisplaySelectedForExportBankTransfert = isDisplaySelectedForExportBankTransfert;
    }

    public Integer getIdBankTransfert() {
        return idBankTransfert;
    }

    public void setIdBankTransfert(Integer idBankTransfert) {
        this.idBankTransfert = idBankTransfert;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
