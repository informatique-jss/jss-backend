package com.jss.osiris.modules.osiris.reporting.model;

public interface IAnnouncementReporting {
    String getAnnouncementCreatedDateYear();

    String getAnnouncementCreatedDateMonth();

    String getAnnouncementCreatedDateWeek();

    String getAnnouncementCreatedDateDay();

    String getAnnouncementStatus();

    String getConfrereAnnouncementLabel();

    Integer getCharacterNumber();

    String getAnnouncementDepartment();

    Float getPreTaxPrice();

    String getNoticeTypeLabel();

    String getNoticeTypeFamilyLabel();

    String getJournalTypeLabel();

    String getProvisionTypeLabel();
}
