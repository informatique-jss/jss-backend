package com.jss.osiris.modules.osiris.quotation.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

public class ServiceTypeChosen {
    @JsonView(JacksonViews.MyJssView.class)
    private ServiceType service;

    @JsonView(JacksonViews.MyJssView.class)
    private Affaire affaire;

    @JsonView(JacksonViews.MyJssView.class)
    private Integer temporaryId;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal preTaxPrice;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal discountedAmount;

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public Affaire getAffaire() {
        return affaire;
    }

    public void setAffaire(Affaire affaire) {
        this.affaire = affaire;
    }

    public Integer getTemporaryId() {
        return temporaryId;
    }

    public void setTemporaryId(Integer temporaryId) {
        this.temporaryId = temporaryId;
    }

    public BigDecimal getPreTaxPrice() {
        return preTaxPrice;
    }

    public void setPreTaxPrice(BigDecimal preTaxPrice) {
        this.preTaxPrice = preTaxPrice;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(BigDecimal discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

}