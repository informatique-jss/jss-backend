package com.jss.osiris.modules.reporting.model;

public interface IQuotationReporting {
    public Integer getAffaireId();

    public String getAffaireLabel();

    public Integer getCustomerOrderId();

    public Integer getProvisionId();

    public String getProvisionTypeLabel();

    public String getProvisionFamilyTypeLabel();

    public String getCustomerOrderLabel();

    public String getTiersLabel();

    public Float getPreTaxPrice();

    public Float getTaxedPrice();
}
