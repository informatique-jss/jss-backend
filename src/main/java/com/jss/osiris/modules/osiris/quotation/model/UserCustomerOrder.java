package com.jss.osiris.modules.osiris.quotation.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public class UserCustomerOrder {
    @JsonView(JacksonViews.MyJssView.class)
    private Document billingDocument;

    @JsonView(JacksonViews.MyJssView.class)
    private Document paperDocument;

    @JsonView(JacksonViews.MyJssView.class)
    private Document digitalDocument;

    @JsonView(JacksonViews.MyJssView.class)
    private List<ServiceTypeChosen> serviceTypes;

    @JsonView(JacksonViews.MyJssView.class)
    private Boolean isCustomerOrder;

    @JsonView(JacksonViews.MyJssView.class)
    private Boolean isDraft;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal preTaxPrice;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal vatPrice;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal totalPrice;

    @JsonView(JacksonViews.MyJssView.class)
    private Responsable dummyResponsable;

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

}