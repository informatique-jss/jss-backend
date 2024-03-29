package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface BankTransfertSearchResult {

    public Integer getId();

    public LocalDateTime getTransfertDate();

    public Float getTransfertAmount();

    public String getTransfertLabel();

    public String getTransfertIban();

    public Boolean getIsAlreadyExported();

    public Boolean getIsSelectedForExport();

    public String getInvoiceBillingLabel();

    public String getCompetentAuthorityLabel();

    public String getAffaireLabel();

    public String getComment();
}
