package com.jss.osiris.modules.osiris.invoicing.model;

import java.util.List;

public class AzureReceiptInvoiceStatus {

    List<Invoice> invoices;
    Boolean invoicesStatus;

    List<AzureInvoice> azureInvoices;
    Boolean azureInvoicesStatus;

    Boolean paymentStatus;
    Boolean customerInvoicedStatus;

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Boolean getInvoicesStatus() {
        return invoicesStatus;
    }

    public void setInvoicesStatus(Boolean invoicesStatus) {
        this.invoicesStatus = invoicesStatus;
    }

    public List<AzureInvoice> getAzureInvoices() {
        return azureInvoices;
    }

    public void setAzureInvoices(List<AzureInvoice> azureInvoices) {
        this.azureInvoices = azureInvoices;
    }

    public Boolean getAzureInvoicesStatus() {
        return azureInvoicesStatus;
    }

    public void setAzureInvoicesStatus(Boolean azureInvoicesStatus) {
        this.azureInvoicesStatus = azureInvoicesStatus;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Boolean getCustomerInvoicedStatus() {
        return customerInvoicedStatus;
    }

    public void setCustomerInvoicedStatus(Boolean customerInvoicedStatus) {
        this.customerInvoicedStatus = customerInvoicedStatus;
    }

}
