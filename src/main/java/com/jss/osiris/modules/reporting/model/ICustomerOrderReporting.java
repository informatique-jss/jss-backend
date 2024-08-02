package com.jss.osiris.modules.reporting.model;

public interface ICustomerOrderReporting {

    Integer getNbrCustomerOrder();

    String getCustomerOrderStatusLabel();

    String getCustomerOrderAssignedEmployee();

    String getAggregateProvisionTypeLabel();

    String getLastReminderDate();

    String getCustomerOrderCreator();
}
