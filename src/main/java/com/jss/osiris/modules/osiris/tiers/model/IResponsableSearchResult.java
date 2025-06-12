package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;

public interface IResponsableSearchResult {
    String getTiersLabel();

    String getTiersCategory();

    Integer getTiersId();

    Integer getResponsableId();

    String getResponsableLabel();

    String getConfrere();

    String getResponsableCategory();

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