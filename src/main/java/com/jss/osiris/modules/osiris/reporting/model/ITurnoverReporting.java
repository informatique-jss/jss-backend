package com.jss.osiris.modules.osiris.reporting.model;

public interface ITurnoverReporting {

    String getInvoiceDateYear();

    String getInvoiceDateMonth();

    String getInvoiceDateWeek();

    String getInvoiceDateDay();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();

    Integer getNbrCustomerOrder();

    Integer getNbrInvoices();

    Integer getNbrCreditNote();

    String getTiersLabel();

    String getConfrereLabel();

    String getTiersCategory();

    String getInvoiceCreator();

    String getSalesEmployeeLabel();

    String getInvoiceStatusLabel();

    Integer getNbrAnnouncement();

    String getAnnouncementDepartment();

    String getReminderType();
}
