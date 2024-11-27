package com.jss.osiris.modules.osiris.reporting.model;

public interface IQuotationReporting {
    public Integer getAffaireId();

    public String getAffaireSiren();

    public String getAffaireSiret();

    public String getWaitedCompetentAuthorityLabel();

    public String getAffaireLabel();

    public Integer getQuotationId();

    public Integer getProvisionId();

    public String getProvisionTypeLabel();

    public String getProvisionFamilyTypeLabel();

    public String getCustomerOrderLabel();

    public String getTiersLabel();

    public String getProvisionStatus();

    public String getProvisionAssignedToLabel();

    public String getThirdReminderDateTime();

    public String getPublicationDateMonth();

    public String getSalesEmployeeLabel();

    public String getQuotationStatusLabel();

    public String getQuotationCreatedDateMonth();

    public String getQuotationCreatedDateDay();

    public Integer getCharacterNumber();

    public String getPublicationDateWeek();

    public String getConfrereAnnouncementLabel();

    public String getNoticeTypeFamilyLabel();

    public String getNoticeTypeLabel();

    public String getQuotationCreator();

    public Float getPreTaxPriceWithoutDebour();

    public String getCustomerOrderOriginLabel();
}
