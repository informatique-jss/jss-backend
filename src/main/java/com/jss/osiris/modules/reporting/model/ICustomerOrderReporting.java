package com.jss.osiris.modules.reporting.model;

public interface ICustomerOrderReporting {
    public Integer getAffaireId();

    public String getAffaireSiren();

    public String getAffaireSiret();

    public String getWaitedCompetentAuthorityLabel();

    public String getAffaireLabel();

    public Integer getCustomerOrderId();

    public Integer getProvisionId();

    public String getProvisionTypeLabel();

    public String getProvisionFamilyTypeLabel();

    public String getCustomerOrderLabel();

    public String getTiersLabel();

    public Float getPreTaxPrice();

    public Float getPreTaxPriceWithoutDebour();

    public Float getTaxedPrice();

    public String getProvisionStatus();

    public String getProvisionAssignedToLabel();

    public String getPublicationDateMonth();

    public String getSalesEmployeeLabel();

    public String getCustomerOrderStatusLabel();

    public String getCustomerOrderCreatedDateMonth();

    public String getInvoiceDateMonth();

    public String getInvoiceDateDay();

    public String getInvoiceCreator();

    public Integer getCharacterNumber();

    public String getPublicationDateWeek();

    public String getConfrereAnnouncementLabel();

    public String getNoticeTypeFamilyLabel();

    public String getNoticeTypeLabel();

    public String getInvoiceStatusLabel();

    public String getInvoiceId();

    public String getCustomerOrderOriginLabel();

    public String getResponsableMail();

    public String getResponsablePhone();
}
