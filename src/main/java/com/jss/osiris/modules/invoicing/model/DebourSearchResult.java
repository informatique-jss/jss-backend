package com.jss.osiris.modules.invoicing.model;

import java.util.Date;

public interface DebourSearchResult {
    public Integer getId();

    public String getComments();

    public Integer getCustomerOrderId();

    public String getBillingTypeLabel();

    public String getCompetentAuthorityLabel();

    public Float getDebourAmount();

    public String getPaymentTypeLabel();

    public Date getPaymentDateTime();

    public Integer getPaymentId();

    public Integer getInvoiceId();

    public String getCheckNumber();

    public Boolean getIsAssociated();

    public Boolean getIsCompetentAuthorityDirectCharge();
}
