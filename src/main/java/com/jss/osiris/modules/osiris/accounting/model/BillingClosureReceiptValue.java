package com.jss.osiris.modules.osiris.accounting.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillingClosureReceiptValue {
    private LocalDateTime eventDateTime;
    private String eventDateString;
    private String eventDescription;
    private String eventCbLink;
    private Float creditAmount;
    private Float debitAmount;
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

    public Float getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Float creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Float getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Float debitAmount) {
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
