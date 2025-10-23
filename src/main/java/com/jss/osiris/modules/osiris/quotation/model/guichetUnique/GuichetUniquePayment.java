package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuichetUniquePayment {

    private String login;
    private String password;
    private String paymentType;
    private String formality;
    private String annualAccount;
    private String acteDeposit;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getFormality() {
        return formality;
    }

    public void setFormality(String formality) {
        this.formality = formality;
    }

    public String getAnnualAccount() {
        return annualAccount;
    }

    public void setAnnualAccount(String annualAccount) {
        this.annualAccount = annualAccount;
    }

    public String getActeDeposit() {
        return acteDeposit;
    }

    public void setActeDeposit(String acteDeposit) {
        this.acteDeposit = acteDeposit;
    }

}
