package com.jss.osiris.modules.osiris.quotation.model;

public class ToOrderStatistics {
    private Integer orderToOrder;
    private Integer orderAssigned;
    private Integer orderBlocked;

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

    public Integer getOrderToOrder() {
        return orderToOrder;
    }

    public void setOrderToOrder(Integer orderToOrder) {
        this.orderToOrder = orderToOrder;
    }

}
