package com.jss.osiris.modules.osiris.quotation.model.centralPay;

public class CentralPayTransaction {
    private String paymentRequestTransactionId;
    private Integer fee;
    private Integer amount;
    private Integer payoutAmount;
    private Integer commission;
    private Integer totalAmount;
    private String captureStatus;
    private String transactionStatus;
    private String description;

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(Integer payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentRequestTransactionId() {
        return paymentRequestTransactionId;
    }

    public void setPaymentRequestTransactionId(String paymentRequestTransactionId) {
        this.paymentRequestTransactionId = paymentRequestTransactionId;
    }

    public String getCaptureStatus() {
        return captureStatus;
    }

    public void setCaptureStatus(String captureStatus) {
        this.captureStatus = captureStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}
