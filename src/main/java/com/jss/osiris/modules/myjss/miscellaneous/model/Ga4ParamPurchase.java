package com.jss.osiris.modules.myjss.miscellaneous.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ga4ParamPurchase extends Ga4PageInfo {

    @JsonProperty("debug_mode")
    private Boolean debugMode;

    // E-commerce standard
    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("tax")
    private BigDecimal tax;

    @JsonProperty("shipping")
    private BigDecimal shipping;

    @JsonProperty("coupon")
    private String coupon;

    @JsonProperty("items")
    private List<Ga4Item> items;

    // Business payload
    @JsonProperty("business_type")
    private String businessType;

    @JsonProperty("business_order_id")
    private Integer businessOderId;

    @JsonProperty("business_service")
    private String businessService;

    @JsonProperty("business_amount")
    private BigDecimal businessAmount;

    @JsonProperty("business_is_draft")
    private Boolean businessIsDraft;

    public Boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(Boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public List<Ga4Item> getItems() {
        return items;
    }

    public void setItems(List<Ga4Item> items) {
        this.items = items;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getBusinessOderId() {
        return businessOderId;
    }

    public void setBusinessOderId(Integer businessOderId) {
        this.businessOderId = businessOderId;
    }

    public String getBusinessService() {
        return businessService;
    }

    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }

    public BigDecimal getBusinessAmount() {
        return businessAmount;
    }

    public void setBusinessAmount(BigDecimal businessAmount) {
        this.businessAmount = businessAmount;
    }

    public Boolean getBusinessIsDraft() {
        return businessIsDraft;
    }

    public void setBusinessIsDraft(Boolean businessIsDraft) {
        this.businessIsDraft = businessIsDraft;
    }
}
