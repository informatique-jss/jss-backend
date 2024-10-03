package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDateTime;

public interface PaymentSearchResult {

    public Integer getId();

    public LocalDateTime getPaymentDate();

    public Float getPaymentAmount();

    public String getPaymentLabel();

    public Boolean getIsExternallyAssociated();

    public Boolean getIsAssociated();

    public Boolean getIsCancelled();

    public Boolean getIsAppoint();

    public Integer getInvoiceId();

    public String getPaymentTypeLabel();

    public String getComment();

    public String getOriginPaymentId();

}
