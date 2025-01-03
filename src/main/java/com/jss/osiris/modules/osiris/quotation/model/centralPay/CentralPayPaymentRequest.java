package com.jss.osiris.modules.osiris.quotation.model.centralPay;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeGmtDeserializer;

import jakarta.persistence.Column;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CentralPayPaymentRequest {
    public static final String ACTIVE = "ACTIVE";
    public static final String CLOSED = "CLOSED";
    public static final String CANCELED = "CANCELED";
    public static final String PAID = "PAID";

    public String paymentRequestId;
    @JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
    public LocalDateTime creationDate;
    public String pointOfSaleId;
    public String deadline;
    public String linkExpirationDate;
    public String scenarioId;
    @JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
    public LocalDateTime scenarioStartingDate;
    public String paymentFormTemplateId;
    public String merchantPaymentRequestId;
    public String description;
    public String currency;
    @Column(scale = 2)
    public BigDecimal totalAmount;
    public String paymentRequestStatus;
    public String paymentStatus;
    public boolean createCustomer;
    public String language;
    public String redirectUrl;
    public String closeComment;
    public ArrayList<CentralPayBreakdown> breakdowns;
    public CentralPayTransaction transaction;

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getPointOfSaleId() {
        return pointOfSaleId;
    }

    public void setPointOfSaleId(String pointOfSaleId) {
        this.pointOfSaleId = pointOfSaleId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getLinkExpirationDate() {
        return linkExpirationDate;
    }

    public void setLinkExpirationDate(String linkExpirationDate) {
        this.linkExpirationDate = linkExpirationDate;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public LocalDateTime getScenarioStartingDate() {
        return scenarioStartingDate;
    }

    public void setScenarioStartingDate(LocalDateTime scenarioStartingDate) {
        this.scenarioStartingDate = scenarioStartingDate;
    }

    public String getPaymentFormTemplateId() {
        return paymentFormTemplateId;
    }

    public void setPaymentFormTemplateId(String paymentFormTemplateId) {
        this.paymentFormTemplateId = paymentFormTemplateId;
    }

    public String getMerchantPaymentRequestId() {
        return merchantPaymentRequestId;
    }

    public void setMerchantPaymentRequestId(String merchantPaymentRequestId) {
        this.merchantPaymentRequestId = merchantPaymentRequestId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentRequestStatus() {
        return paymentRequestStatus;
    }

    public void setPaymentRequestStatus(String paymentRequestStatus) {
        this.paymentRequestStatus = paymentRequestStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isCreateCustomer() {
        return createCustomer;
    }

    public void setCreateCustomer(boolean createCustomer) {
        this.createCustomer = createCustomer;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getCloseComment() {
        return closeComment;
    }

    public void setCloseComment(String closeComment) {
        this.closeComment = closeComment;
    }

    public ArrayList<CentralPayBreakdown> getBreakdowns() {
        return breakdowns;
    }

    public void setBreakdowns(ArrayList<CentralPayBreakdown> breakdowns) {
        this.breakdowns = breakdowns;
    }

    public static String getActive() {
        return ACTIVE;
    }

    public static String getClosed() {
        return CLOSED;
    }

    public static String getPaid() {
        return PAID;
    }

    public CentralPayTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(CentralPayTransaction transaction) {
        this.transaction = transaction;
    }

}
