package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface InvoiceSearchResult {

    public Integer getInvoiceId();

    public Integer getInvoiceStatusId();

    public String getInvoiceStatus();

    public String getInvoiceStatusCode();

    public Integer getCustomerOrderId();

    public String getCustomerOrderLabel();

    public String getProviderLabel();

    public Integer getIdPaymentType();

    public String getTiersLabel();

    public Integer getResponsableId();

    public Integer getSalesEmployeeId();

    public Integer getTiersId();

    public String getResponsableLabel();

    public String getAffaireLabel();

    public String getBillingLabel();

    public LocalDateTime getCreatedDate();

    public Float getTotalPrice();

    public String getCustomerOrderDescription();

    public LocalDateTime getfirstReminderDateTime();

    public LocalDateTime getSecondReminderDateTime();

    public LocalDateTime getThirdReminderDateTime();

    public String getManualAccountingDocumentNumber();

    public String getManualAccountingDocumentDate();

    public LocalDate getDueDate();

    public LocalDate getLastFollowupDate();

    public String getPaymentId();

    public Float getRemainingToPay();

    public String getInvoiceRecipient();

    public Boolean getIsInvoiceFromProvider();

    public Boolean getIsProviderCreditNote();
}
