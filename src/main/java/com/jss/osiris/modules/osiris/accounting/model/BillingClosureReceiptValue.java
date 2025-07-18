package com.jss.osiris.modules.osiris.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public class BillingClosureReceiptValue {
    @JsonView(JacksonViews.MyJssListView.class)
    private LocalDateTime eventDateTime;

    @JsonView(JacksonViews.MyJssListView.class)
    private String eventDateString;

    @JsonView(JacksonViews.MyJssListView.class)
    private String eventDescription;

    @JsonView(JacksonViews.MyJssListView.class)
    private String eventCbLink;

    @JsonView(JacksonViews.MyJssListView.class)
    private BigDecimal creditAmount;

    @JsonView(JacksonViews.MyJssListView.class)
    private BigDecimal debitAmount;

    @JsonView(JacksonViews.MyJssListView.class)
    private boolean displayBottomBorder;

    @JsonView(JacksonViews.MyJssListView.class)
    private LocalDate directDebitTransfertDate;

    @JsonView(JacksonViews.MyJssListView.class)
    private Responsable responsable;

    @JsonView(JacksonViews.MyJssListView.class)
    private String affaireLists;

    @JsonView(JacksonViews.MyJssListView.class)
    private String serviceLists;

    @JsonView(JacksonViews.MyJssListView.class)
    private Integer idInvoice;

    @JsonView(JacksonViews.MyJssListView.class)
    private Integer idCustomerOrder;

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

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
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

    public void setDirectDebitTransfertDate(LocalDate directDebitTransfertDate) {
        this.directDebitTransfertDate = directDebitTransfertDate;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public String getServiceLists() {
        return serviceLists;
    }

    public void setServiceLists(String serviceLists) {
        this.serviceLists = serviceLists;
    }

    public String getAffaireLists() {
        return affaireLists;
    }

    public void setAffaireLists(String affaireLists) {
        this.affaireLists = affaireLists;
    }

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public Integer getIdCustomerOrder() {
        return idCustomerOrder;
    }

    public void setIdCustomerOrder(Integer idCustomerOrder) {
        this.idCustomerOrder = idCustomerOrder;
    }

}
