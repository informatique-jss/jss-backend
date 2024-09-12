package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.Tiers;

public class InvoiceSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private List<InvoiceStatus> invoiceStatus;
    private Boolean showToRecover;
    private List<Tiers> customerOrders;
    private Integer invoiceId;
    private Integer customerOrderId;
    private Employee salesEmployee;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<InvoiceStatus> getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(List<InvoiceStatus> invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getShowToRecover() {
        return showToRecover;
    }

    public void setShowToRecover(Boolean showToRecover) {
        this.showToRecover = showToRecover;
    }

    public List<Tiers> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<Tiers> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(Integer customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public Employee getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(Employee salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

}
