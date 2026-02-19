package com.jss.osiris.modules.osiris.invoicing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    private Integer id;
    private Integer originPayment;
    private LocalDateTime paymentDate;
    private BigDecimal paymentAmount;
    private String paymentType;
    private String label;
    private Boolean isAssociated;
    private Boolean isCancelled;
    private Boolean isAppoint;
    private Integer invoiceId;
    private Integer customerOrderId;
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOriginPayment() {
        return originPayment;
    }

    public void setOriginPayment(Integer originPayment) {
        this.originPayment = originPayment;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(Integer customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
