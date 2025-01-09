package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDateTime;

public interface DirectDebitTransfertSearchResult {

    public Integer getId();

    public String getCustomerOrderLabel();

    public LocalDateTime getTransfertDate();

    public Float getTransfertAmount();

    public String getTransfertLabel();

    public String getTransfertIban();

    public String getTransfertBic();

    public Boolean getIsAlreadyExported();

    public Boolean getIsMatched();

    public String getInvoiceStatus();
}
