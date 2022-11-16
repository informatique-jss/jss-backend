package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

public interface PaymentSearchResult {

    public Integer getId();

    public String getPaymentWayLabel();

    public LocalDateTime getPaymentDate();

    public Float getPaymentAmount();

    public String getPaymentLabel();

    public Boolean getIsExternallyAssociated();

    public Integer getInvoiceId();

    public Integer getCustomerOrderId();
}
