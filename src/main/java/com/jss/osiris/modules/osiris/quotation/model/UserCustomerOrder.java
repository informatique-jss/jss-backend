package com.jss.osiris.modules.osiris.quotation.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public class UserCustomerOrder {
    @JsonView(JacksonViews.MyJssListView.class)
    private Document billingDocument;

    @JsonView(JacksonViews.MyJssListView.class)
    private Document paperDocument;

    @JsonView(JacksonViews.MyJssListView.class)
    private Document digitalDocument;

    @JsonView(JacksonViews.MyJssListView.class)
    private List<ServiceTypeChosen> serviceTypes;

    @JsonView(JacksonViews.MyJssListView.class)
    private Boolean isCustomerOrder;

    @JsonView(JacksonViews.MyJssListView.class)
    private Boolean isDraft;

    @JsonView(JacksonViews.MyJssListView.class)
    private BigDecimal preTaxPrice;

    @JsonView(JacksonViews.MyJssListView.class)
    private BigDecimal vatPrice;

    @JsonView(JacksonViews.MyJssListView.class)
    private BigDecimal totalPrice;

    @JsonView(JacksonViews.MyJssListView.class)
    private Responsable dummyResponsable;

    @JsonView(JacksonViews.MyJssListView.class)
    private Boolean isEmergency;

    @JsonView(JacksonViews.MyJssListView.class)
    private Integer orderId;

    @JsonView(JacksonViews.MyJssListView.class)
    private Boolean customerIsIndividual;

    @JsonView(JacksonViews.MyJssListView.class)
    private String customerDenomination;

    @JsonView(JacksonViews.MyJssListView.class)
    private String customerAddress;

    @JsonView(JacksonViews.MyJssListView.class)
    private String customerPostalCode;

    @JsonView(JacksonViews.MyJssListView.class)
    private City customerCity;

    @JsonView(JacksonViews.MyJssListView.class)
    private Country customerCountry;

    @JsonView(JacksonViews.MyJssListView.class)
    private String customerSiret;

    @JsonView(JacksonViews.MyJssListView.class)
    private Civility responsableCivility;

    @JsonView(JacksonViews.MyJssListView.class)
    private String responsableFirstname;

    @JsonView(JacksonViews.MyJssListView.class)
    private String responsableLastname;

    @JsonView(JacksonViews.MyJssListView.class)
    private String responsableMail;

    @JsonView(JacksonViews.MyJssListView.class)
    private String responsablePhone;

    @JsonView(JacksonViews.MyJssListView.class)
    private String siret;

    public Document getBillingDocument() {
        return billingDocument;
    }

    public void setBillingDocument(Document billingDocument) {
        this.billingDocument = billingDocument;
    }

    public Document getPaperDocument() {
        return paperDocument;
    }

    public void setPaperDocument(Document paperDocument) {
        this.paperDocument = paperDocument;
    }

    public Document getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(Document digitalDocument) {
        this.digitalDocument = digitalDocument;
    }

    public List<ServiceTypeChosen> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceTypeChosen> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public Boolean getIsCustomerOrder() {
        return isCustomerOrder;
    }

    public void setIsCustomerOrder(Boolean isCustomerOrder) {
        this.isCustomerOrder = isCustomerOrder;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public BigDecimal getPreTaxPrice() {
        return preTaxPrice;
    }

    public void setPreTaxPrice(BigDecimal preTaxPrice) {
        this.preTaxPrice = preTaxPrice;
    }

    public BigDecimal getVatPrice() {
        return vatPrice;
    }

    public void setVatPrice(BigDecimal vatPrice) {
        this.vatPrice = vatPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Responsable getDummyResponsable() {
        return dummyResponsable;
    }

    public void setDummyResponsable(Responsable dummyResponsable) {
        this.dummyResponsable = dummyResponsable;
    }

    public Boolean getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Boolean getCustomerIsIndividual() {
        return customerIsIndividual;
    }

    public void setCustomerIsIndividual(Boolean customerIsIndividual) {
        this.customerIsIndividual = customerIsIndividual;
    }

    public String getCustomerDenomination() {
        return customerDenomination;
    }

    public void setCustomerDenomination(String customerDenomination) {
        this.customerDenomination = customerDenomination;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    public void setCustomerPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
    }

    public City getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(City customerCity) {
        this.customerCity = customerCity;
    }

    public Country getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(Country customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getCustomerSiret() {
        return customerSiret;
    }

    public void setCustomerSiret(String customerSiret) {
        this.customerSiret = customerSiret;
    }

    public Civility getResponsableCivility() {
        return responsableCivility;
    }

    public void setResponsableCivility(Civility responsableCivility) {
        this.responsableCivility = responsableCivility;
    }

    public String getResponsableFirstname() {
        return responsableFirstname;
    }

    public void setResponsableFirstname(String responsableFirstname) {
        this.responsableFirstname = responsableFirstname;
    }

    public String getResponsableLastname() {
        return responsableLastname;
    }

    public void setResponsableLastname(String responsableLastname) {
        this.responsableLastname = responsableLastname;
    }

    public String getResponsableMail() {
        return responsableMail;
    }

    public void setResponsableMail(String responsableMail) {
        this.responsableMail = responsableMail;
    }

    public String getResponsablePhone() {
        return responsablePhone;
    }

    public void setResponsablePhone(String responsablePhone) {
        this.responsablePhone = responsablePhone;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

}