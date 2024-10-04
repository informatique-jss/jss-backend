package com.jss.osiris.modules.osiris.quotation.model.centralPay;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeGmtDeserializer;

public class CentralPayBreakdown {
    public String paymentRequestBreakdownId;
    public String customerId;
    @JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
    public LocalDateTime lastEnteringDate;
    @JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
    public LocalDateTime lastPaymentAttempt;
    public Float amount;
    public boolean initiator;
    public String endpoint;
    public String email;
    public String phone;
    public String firstName;
    public String lastName;
    public boolean entered;
    public boolean paymentAttempted;
    public boolean paid;
    public int view;
    public List<CentralPayPayment> payments;

    public String getPaymentRequestBreakdownId() {
        return paymentRequestBreakdownId;
    }

    public void setPaymentRequestBreakdownId(String paymentRequestBreakdownId) {
        this.paymentRequestBreakdownId = paymentRequestBreakdownId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getLastEnteringDate() {
        return lastEnteringDate;
    }

    public void setLastEnteringDate(LocalDateTime lastEnteringDate) {
        this.lastEnteringDate = lastEnteringDate;
    }

    public LocalDateTime getLastPaymentAttempt() {
        return lastPaymentAttempt;
    }

    public void setLastPaymentAttempt(LocalDateTime lastPaymentAttempt) {
        this.lastPaymentAttempt = lastPaymentAttempt;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEntered() {
        return entered;
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
    }

    public boolean isPaymentAttempted() {
        return paymentAttempted;
    }

    public void setPaymentAttempted(boolean paymentAttempted) {
        this.paymentAttempted = paymentAttempted;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public List<CentralPayPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<CentralPayPayment> payments) {
        this.payments = payments;
    }

}
