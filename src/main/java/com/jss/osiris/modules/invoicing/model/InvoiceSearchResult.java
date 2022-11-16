package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface InvoiceSearchResult {

    public Integer getInvoiceId();

    public String getInvoiceStatus();

    public Integer getCustomerOrderId();

    public String getCustomerOrderLabel();

    public Integer getConfrereId();

    public Integer getResponsableId();

    public Integer getTiersId();

    public String getResponsableLabel();

    public String getAffaireLabel();

    public String getBillingLabel();

    public LocalDateTime getCreatedDate();

    public Float getTotalPrice();

    public String getCstomerOrderDescription();

    public String getPaymentId();
}
