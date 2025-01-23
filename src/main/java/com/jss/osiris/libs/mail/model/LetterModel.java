package com.jss.osiris.libs.mail.model;

import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;

public class LetterModel {
    private String customerReference;
    private String commandNumber;
    private CustomerOrder customerOrder;
    private InvoiceLabelResult invoiceLabelResult;
    private String affaireLabel;
    private String eventLabel;
    private String signatureLabel;

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getCommandNumber() {
        return commandNumber;
    }

    public void setCommandNumber(String commandNumber) {
        this.commandNumber = commandNumber;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public InvoiceLabelResult getInvoiceLabelResult() {
        return invoiceLabelResult;
    }

    public void setInvoiceLabelResult(InvoiceLabelResult invoiceLabelResult) {
        this.invoiceLabelResult = invoiceLabelResult;
    }

    public String getAffaireLabel() {
        return affaireLabel;
    }

    public void setAffaireLabel(String affaireLabel) {
        this.affaireLabel = affaireLabel;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public String getSignatureLabel() {
        return signatureLabel;
    }

    public void setSignatureLabel(String signatureLabel) {
        this.signatureLabel = signatureLabel;
    }

}
