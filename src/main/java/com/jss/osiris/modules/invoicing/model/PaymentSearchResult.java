package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface PaymentSearchResult {

    public Integer getId();

    public String getPaymentWayLabel();

    public String getPaymentWayId();

    public LocalDateTime getPaymentDate();

    public Float getPaymentAmount();

    public String getPaymentLabel();

    public Boolean getIsExternallyAssociated();

    public Boolean getIsAssociated();

    public Boolean getIsCancelled();

    public Integer getInvoiceId();

}
