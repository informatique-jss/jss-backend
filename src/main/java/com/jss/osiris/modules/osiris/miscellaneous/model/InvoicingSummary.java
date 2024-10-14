package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.math.BigDecimal;

public class InvoicingSummary {
    private BigDecimal totalPrice;
    private BigDecimal discountTotal;
    private BigDecimal preTaxPriceTotal;
    private BigDecimal vatTotal;

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

}