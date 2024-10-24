package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDateTime;

public interface OutboundCheckSearchResult {

    public String getOutboundCheckNumber();

    public Integer getPaymentNumber();

    public LocalDateTime getOutboundCheckDate();

    public Float getOutboundCheckAmount();

    public String getOutboundCheckLabel();

    public Integer getInvoiceAssociated();

    public Boolean getIsMatched();

}
