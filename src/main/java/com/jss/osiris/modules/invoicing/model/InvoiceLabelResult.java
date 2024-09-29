package com.jss.osiris.modules.invoicing.model;

import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.tiers.model.BillingLabelType;

public class InvoiceLabelResult {
    private String billingLabel;
    private String billingLabelAddress;
    private String cedexComplement;
    private String billingLabelPostalCode;
    private String billingLabelComplementCedex;
    private City billingLabelCity;
    private Country billingLabelCountry;
    private BillingLabelType billingLabelType;
    private Boolean isResponsableOnBilling;
    private String billingLabelIntercommunityVat;
    private Boolean isCommandNumberMandatory;
    private String commandNumber;
    private String labelOrigin;

    public String getBillingLabel() {
        return billingLabel;
    }

    public void setBillingLabel(String billingLabel) {
        this.billingLabel = billingLabel;
    }

    public String getBillingLabelAddress() {
        return billingLabelAddress;
    }

    public void setBillingLabelAddress(String billingLabelAddress) {
        this.billingLabelAddress = billingLabelAddress;
    }

    public String getBillingLabelPostalCode() {
        return billingLabelPostalCode;
    }

    public void setBillingLabelPostalCode(String billingLabelPostalCode) {
        this.billingLabelPostalCode = billingLabelPostalCode;
    }

    public City getBillingLabelCity() {
        return billingLabelCity;
    }

    public void setBillingLabelCity(City billingLabelCity) {
        this.billingLabelCity = billingLabelCity;
    }

    public Country getBillingLabelCountry() {
        return billingLabelCountry;
    }

    public void setBillingLabelCountry(Country billingLabelCountry) {
        this.billingLabelCountry = billingLabelCountry;
    }

    public BillingLabelType getBillingLabelType() {
        return billingLabelType;
    }

    public void setBillingLabelType(BillingLabelType billingLabelType) {
        this.billingLabelType = billingLabelType;
    }

    public Boolean getIsResponsableOnBilling() {
        return isResponsableOnBilling;
    }

    public void setIsResponsableOnBilling(Boolean isResponsableOnBilling) {
        this.isResponsableOnBilling = isResponsableOnBilling;
    }

    public Boolean getIsCommandNumberMandatory() {
        return isCommandNumberMandatory;
    }

    public void setIsCommandNumberMandatory(Boolean isCommandNumberMandatory) {
        this.isCommandNumberMandatory = isCommandNumberMandatory;
    }

    public String getCommandNumber() {
        return commandNumber;
    }

    public void setCommandNumber(String commandNumber) {
        this.commandNumber = commandNumber;
    }

    public String getLabelOrigin() {
        return labelOrigin;
    }

    public void setLabelOrigin(String labelOrigin) {
        this.labelOrigin = labelOrigin;
    }

    public String getCedexComplement() {
        return cedexComplement;
    }

    public void setCedexComplement(String cedexComplement) {
        this.cedexComplement = cedexComplement;
    }

    public String getBillingLabelComplementCedex() {
        return billingLabelComplementCedex;
    }

    public void setBillingLabelComplementCedex(String billingLabelComplementCedex) {
        this.billingLabelComplementCedex = billingLabelComplementCedex;
    }

    public String getBillingLabelIntercommunityVat() {
        return billingLabelIntercommunityVat;
    }

    public void setBillingLabelIntercommunityVat(String billingLabelIntercommunityVat) {
        this.billingLabelIntercommunityVat = billingLabelIntercommunityVat;
    }
}
