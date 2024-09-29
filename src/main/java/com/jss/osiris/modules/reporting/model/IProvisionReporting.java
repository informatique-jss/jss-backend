package com.jss.osiris.modules.reporting.model;

public interface IProvisionReporting {
    String getCustomerOrderCreatedDateYear();

    String getInvoiceDateMonth();

    String getCustomerOrderCreatedDateMonth();

    String getCustomerOrderCreatedDateWeek();

    String getCustomerOrderCreatedDateDay();

    String getCustomerOrderStatusLabel();

    String getProvisionAssignedToLabel();

    String getProvisionFamilyTypeLabel();

    String getProvisionTypeLabel();

    String getAggregateProvisionTypeLabel();

    Integer getProvisionNumber();

    String getProvisionStatus();

    String getWaitedCompetentAuthorityLabel();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();
}
