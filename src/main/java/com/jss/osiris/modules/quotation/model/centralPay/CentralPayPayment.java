package com.jss.osiris.modules.quotation.model.centralPay;

public class CentralPayPayment {
    private String uuid;
    private String paymentMethod;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
