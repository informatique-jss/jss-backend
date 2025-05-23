package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;

public class InvoicingSummary {
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BigDecimal totalPrice;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BigDecimal discountTotal;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BigDecimal preTaxPriceTotal;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BigDecimal vatTotal;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BigDecimal remainingToPay;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private BillingLabelType billingLabelType;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }

    public BigDecimal getPreTaxPriceTotal() {
        return preTaxPriceTotal;
    }

    public void setPreTaxPriceTotal(BigDecimal preTaxPriceTotal) {
        this.preTaxPriceTotal = preTaxPriceTotal;
    }

    public BigDecimal getVatTotal() {
        return vatTotal;
    }

    public void setVatTotal(BigDecimal vatTotal) {
        this.vatTotal = vatTotal;
    }

    public BillingLabelType getBillingLabelType() {
        return billingLabelType;
    }

    public void setBillingLabelType(BillingLabelType billingLabelType) {
        this.billingLabelType = billingLabelType;
    }

    public BigDecimal getRemainingToPay() {
        return remainingToPay;
    }

    public void setRemainingToPay(BigDecimal remainingToPay) {
        this.remainingToPay = remainingToPay;
    }

}