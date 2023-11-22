package com.jss.osiris.modules.reporting.model;

public interface ITurnoverVatReporting {

    String getInvoiceDateYear();

    String getInvoiceDateMonth();

    String getInvoiceDateWeek();

    String getInvoiceDateDay();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();

    String getTiersLabel();

    String getTiersCategory();

    String getInvoiceCreator();

    String getSalesEmployeeLabel();

    String getInvoiceStatusLabel();

    String getVatLabel();

    Integer getIsProviderInvoice();

}
