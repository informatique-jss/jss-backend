package com.jss.osiris.modules.reporting.model;

public interface ITurnoverReporting {

    String getInvoiceDateYear();

    String getInvoiceDateMonth();

    String getInvoiceDateWeek();

    String getInvoiceDateDay();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();

    Integer getnbrCustomerOrder();

    Integer getnbrInvoices();

    String gettiersLabel();

    String gettiersCategory();

    String getinvoiceCreator();

    String getsalesEmployeeLabel();

    String getinvoiceStatusLabel();
}
