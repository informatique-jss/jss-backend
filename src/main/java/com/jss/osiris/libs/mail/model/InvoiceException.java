package com.jss.osiris.libs.mail.model;

import com.jss.osiris.modules.osiris.invoicing.model.Invoice;

public class InvoiceException extends Exception {

    private Invoice invoice;

    public InvoiceException(Invoice invoice) {
        super("invoice");
        this.invoice = invoice;
    }

    public Invoice getInvoice() {
        return invoice;
    }
}