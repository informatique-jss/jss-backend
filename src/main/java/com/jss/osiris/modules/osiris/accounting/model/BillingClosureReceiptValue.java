package com.jss.osiris.modules.osiris.accounting.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;

public class BillingClosureReceiptValue {
    private LocalDateTime eventDateTime;
    private String eventDateString;
    private String eventDescription;
    private String eventCbLink;

    @Column(scale = 2)
    private Double creditAmount;
    @Column(scale = 2)
    private Double debitAmount;

    private boolean displayBottomBorder;
    private LocalDate directDebitTransfertDate;

    public BillingClosureReceiptValue() {
    }

    public BillingClosureReceiptValue(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventCbLink() {
        return eventCbLink;
    }

    public void setEventCbLink(String eventCbLink) {
        this.eventCbLink = eventCbLink;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Double getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Double debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getEventDateString() {
        return eventDateString;
    }

    public void setEventDateString(String eventDateString) {
        this.eventDateString = eventDateString;
    }

    public boolean isDisplayBottomBorder() {
        return displayBottomBorder;
    }

    public void setDisplayBottomBorder(boolean displayBottomBorder) {
        this.displayBottomBorder = displayBottomBorder;
    }

    public LocalDate getDirectDebitTransfertDate() {
        return directDebitTransfertDate;
    }

    public void setDirectDebitTransfertDateTime(LocalDate transfertDateTime) {
        this.directDebitTransfertDate = transfertDateTime;
    }
}
