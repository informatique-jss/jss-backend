package com.jss.osiris.modules.myjss.quotation.controller.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

public class DashboardUserStatistics {

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer customerOrderInProgress;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer quotationToValidate;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer invoiceToPay;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer invoiceAfterDueDate;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer customerOrderRequieringAttention;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer customerOrderDraft;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer quotationDraft;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer unreadCommentsForCustomerOrders;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer unreadCommentsForQuotations;

    public Integer getCustomerOrderInProgress() {
        return customerOrderInProgress;
    }

    public void setCustomerOrderInProgress(Integer customerOrderInProgress) {
        this.customerOrderInProgress = customerOrderInProgress;
    }

    public Integer getQuotationToValidate() {
        return quotationToValidate;
    }

    public void setQuotationToValidate(Integer quotationToValidate) {
        this.quotationToValidate = quotationToValidate;
    }

    public Integer getInvoiceToPay() {
        return invoiceToPay;
    }

    public void setInvoiceToPay(Integer invoiceToPay) {
        this.invoiceToPay = invoiceToPay;
    }

    public Integer getInvoiceAfterDueDate() {
        return invoiceAfterDueDate;
    }

    public void setInvoiceAfterDueDate(Integer invoiceAfterDueDate) {
        this.invoiceAfterDueDate = invoiceAfterDueDate;
    }

    public Integer getCustomerOrderRequieringAttention() {
        return customerOrderRequieringAttention;
    }

    public void setCustomerOrderRequieringAttention(Integer customerOrderRequieringAttention) {
        this.customerOrderRequieringAttention = customerOrderRequieringAttention;
    }

    public Integer getCustomerOrderDraft() {
        return customerOrderDraft;
    }

    public void setCustomerOrderDraft(Integer customerOrderDraft) {
        this.customerOrderDraft = customerOrderDraft;
    }

    public Integer getQuotationDraft() {
        return quotationDraft;
    }

    public void setQuotationDraft(Integer quotationDraft) {
        this.quotationDraft = quotationDraft;
    }

    public Integer getUnreadCommentsForCustomerOrders() {
        return unreadCommentsForCustomerOrders;
    }

    public void setUnreadCommentsForCustomerOrders(Integer unreadCommentsForCustomerOrders) {
        this.unreadCommentsForCustomerOrders = unreadCommentsForCustomerOrders;
    }

    public Integer getUnreadCommentsForQuotations() {
        return unreadCommentsForQuotations;
    }

    public void setUnreadCommentsForQuotations(Integer unreadCommentsForQuotations) {
        this.unreadCommentsForQuotations = unreadCommentsForQuotations;
    }
}
