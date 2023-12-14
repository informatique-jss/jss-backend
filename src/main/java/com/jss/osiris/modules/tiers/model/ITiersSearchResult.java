package com.jss.osiris.modules.tiers.model;

import java.time.LocalDate;

public interface ITiersSearchResult {
    String getTiersLabel();

    String getTiersCategory();

    Integer getTiersId();

    String getSalesEmployeeLabel();

    String getSalesEmployeeId();

    LocalDate getFirstOrderDay();

    LocalDate getLastOrderDay();

    LocalDate getCreatedDateDay();

    LocalDate getLastResponsableFollowupDate();

    Integer getAnnouncementJssNbr();

    Integer getAnnouncementConfrereNbr();

    Integer getAnnouncementNbr();

    Integer getFormalityNbr();

    String getBillingLabelType();

    Float getTurnoverAmountWithoutTax();

    Float getTurnoverAmountWithTax();

    Float getTurnoverAmountWithoutDebourWithoutTax();

    Float getTurnoverAmountWithoutDebourWithTax();

}