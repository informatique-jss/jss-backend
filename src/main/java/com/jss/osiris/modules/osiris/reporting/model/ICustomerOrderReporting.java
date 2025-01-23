package com.jss.osiris.modules.osiris.reporting.model;

public interface ICustomerOrderReporting {

    Integer getNbrCustomerOrder();

    String getCustomerOrderStatusLabel();

    String getCustomerOrderAssignedEmployee();

    String getAggregateProvisionTypeLabel();

    String getLastReminderDate();

    String getCustomerOrderCreator();
}
