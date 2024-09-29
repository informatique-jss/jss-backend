package com.jss.osiris.modules.reporting.model;

public interface ITiersReporting {
    String getTiersLabel();

    String getTiersCategory();

    String getResponsableLabel();

    String getResponsableCategory();

    String getSalesEmployeeLabel();

    String getFirstOrderYear();

    String getFirstOrderMonth();

    String getFirstOrderWeek();

    String getFirstOrderDay();

    String getLastOrderYear();

    String getLastOrderMonth();

    String getLastOrderWeek();

    String getLastOrderDay();

    String getCreatedDateYear();

    String getCreatedDateMonth();

    String getCreatedDateWeek();

    String getCreatedDateDay();

    String getLastTiersFollowupDate();

    String getLastResponsableFollowupDate();

    Integer getNbrCustomerOrder();
}
