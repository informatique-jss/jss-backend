package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GuichetUniqueSignature {
    private String formality;
    private String annualAccount;
    private String acteDeposit;
    private String association;
    private String signedDocument;
    private String beSignedDocument;

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

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getSignedDocument() {
        return signedDocument;
    }

    public void setSignedDocument(String signedDocument) {
        this.signedDocument = signedDocument;
    }

    public String getBeSignedDocument() {
        return beSignedDocument;
    }

    public void setBeSignedDocument(String beSignedDocument) {
        this.beSignedDocument = beSignedDocument;
    }

}
