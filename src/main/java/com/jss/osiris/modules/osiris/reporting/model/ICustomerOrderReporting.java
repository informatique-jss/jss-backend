package com.jss.osiris.modules.osiris.reporting.model;

public interface ICustomerOrderReporting {

    Integer getNbrCustomerOrder();

    String getCustomerOrderStatusLabel();

    String getAggregateProvisionTypeLabel();

    String getLastReminderDate();

    String getCustomerOrderCreator();

    String getCustomerOrderCreatedDateYear();

    String getCustomerOrderCreatedDateMonth();

    String getCustomerOrderCreatedDateDay();

    String getCustomerOrderCreatedDateWeek();
}
