package com.jss.osiris.modules.osiris.quotation.dto;

import java.time.LocalDate;
import java.util.List;

public class GuichetUniqueDepositInfoDto {

    private LocalDate depositDate;
    private String waitingForValidationPartnerCenterName;
    private LocalDate waitingForValidationFromDate;
    private List<LocalDate> askingMissingDocumentDates;
    private LocalDate validationDate;

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public String getWaitingForValidationPartnerCenterName() {
        return waitingForValidationPartnerCenterName;
    }

    public void setWaitingForValidationPartnerCenterName(String waitingForValidationPartnerCenterName) {
        this.waitingForValidationPartnerCenterName = waitingForValidationPartnerCenterName;
    }

    public LocalDate getWaitingForValidationFromDate() {
        return waitingForValidationFromDate;
    }

    public void setWaitingForValidationFromDate(LocalDate waitingForValidationFromDate) {
        this.waitingForValidationFromDate = waitingForValidationFromDate;
    }

    public List<LocalDate> getAskingMissingDocumentDates() {
        return askingMissingDocumentDates;
    }

    public void setAskingMissingDocumentDates(List<LocalDate> askingMissingDocumentDates) {
        this.askingMissingDocumentDates = askingMissingDocumentDates;
    }

    public LocalDate getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDate validationDate) {
        this.validationDate = validationDate;
    }
}
