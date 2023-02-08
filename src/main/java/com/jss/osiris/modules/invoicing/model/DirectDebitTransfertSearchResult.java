package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface DirectDebitTransfertSearchResult {

    public Integer getId();

    public LocalDateTime getTransfertDate();

    public Float getTransfertAmount();

    public String getTransfertLabel();

    public String getTransfertIban();

    public Boolean getIsAlreadyExported();
}
