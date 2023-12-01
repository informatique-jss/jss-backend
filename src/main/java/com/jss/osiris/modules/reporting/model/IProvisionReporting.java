package com.jss.osiris.modules.reporting.model;

public interface IProvisionReporting {
    String getCustomerOrderCreatedDateYear();

    String getCustomerOrderCreatedDateMonth();

    String getCustomerOrderCreatedDateWeek();

    String getCustomerOrderCreatedDateDay();

    String getCustomerOrderStatusLabel();

    String getProvisionAssignedToLabel();

    String getProvisionFamilyTypeLabel();

    Integer getProvisionNumber();

    String getProvisionStatus();

    String getWaitedCompetentAuthorityLabel();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();
}
