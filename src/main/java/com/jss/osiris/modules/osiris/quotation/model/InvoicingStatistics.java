package com.jss.osiris.modules.osiris.quotation.model;

public class InvoicingStatistics {
    private Integer orderToInvoiced;
    private Integer orderAssigned;
    private Integer orderBlocked;

    public Integer getOrderToInvoiced() {
        return orderToInvoiced;
    }

    public void setOrderToInvoiced(Integer orderToInvoiced) {
        this.orderToInvoiced = orderToInvoiced;
    }

    public Integer getOrderAssigned() {
        return orderAssigned;
    }

    public void setOrderAssigned(Integer orderAssigned) {
        this.orderAssigned = orderAssigned;
    }

    public Integer getOrderBlocked() {
        return orderBlocked;
    }

    public void setOrderBlocked(Integer orderBlocked) {
        this.orderBlocked = orderBlocked;
    }

}
