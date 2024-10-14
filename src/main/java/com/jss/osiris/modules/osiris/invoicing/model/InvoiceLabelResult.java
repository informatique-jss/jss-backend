package com.jss.osiris.modules.osiris.invoicing.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;

public class InvoiceLabelResult {
    @JsonView(JacksonViews.MyJssView.class)
    private String billingLabel;

    @JsonView(JacksonViews.MyJssView.class)
    private String billingLabelAddress;

    @JsonView(JacksonViews.MyJssView.class)
    private String cedexComplement;

    @JsonView(JacksonViews.MyJssView.class)
    private String billingLabelPostalCode;

    @JsonView(JacksonViews.MyJssView.class)
    private String billingLabelComplementCedex;

    @JsonView(JacksonViews.MyJssView.class)
    private City billingLabelCity;

    @JsonView(JacksonViews.MyJssView.class)
    private Country billingLabelCountry;

    private BillingLabelType billingLabelType;
    private Boolean isResponsableOnBilling;
    private String billingLabelIntercommunityVat;
    private Boolean isCommandNumberMandatory;

    @JsonView(JacksonViews.MyJssView.class)
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
