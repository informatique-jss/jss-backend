package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;

public interface AnnouncementSearchResult {
    public String getId();
    
    public String getAffaireName();

    public String getDepartment();

    public LocalDate getPublicationDate();

    public String getNoticeTypeLabels();

    public String getNotice();

}
