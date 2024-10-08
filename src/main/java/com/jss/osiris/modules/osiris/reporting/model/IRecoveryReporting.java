package com.jss.osiris.modules.osiris.reporting.model;

public interface IRecoveryReporting {
    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();

    Integer getNbrInvoices();

    String getPayedPeriod();

    String getReminderType();
}
