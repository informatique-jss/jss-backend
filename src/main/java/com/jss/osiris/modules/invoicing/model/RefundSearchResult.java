package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface RefundSearchResult {

    public Integer getId();

    public LocalDateTime getRefundDate();

    public Float getRefundAmount();

    public String getRefundLabel();

    public String getRefundIban();

    public Boolean getIsMatched();

    public Boolean getIsAlreadyExported();

    public Integer getPaymentId();

    public String getRefundTiersLabel();
}
