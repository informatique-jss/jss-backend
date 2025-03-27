package com.jss.osiris.modules.osiris.accounting.model;

import java.util.Date;

public interface SuspiciousInvoiceResult {

    public Integer getIdTiers();

    public String getTiers();

    public Integer getIdCommercial();

    public Float getHtAmount();

    public Float getFinalAmount();

    public Integer getNbrInvoice();

    public String getAffaire();

    public Integer getIdInvoice();

    public Float getAppliableRate();

    public Float getSuspiciousMarkup();

    public Integer getDueDaysNumber();

    public Date getCreatedDate();
}
