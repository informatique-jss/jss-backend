package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;

public interface AnnouncementStatusService {
    public List<AnnouncementStatus> getAnnouncementStatus();

    public AnnouncementStatus getAnnouncementStatus(Integer id);

    public AnnouncementStatus addOrUpdateAnnouncementStatus(AnnouncementStatus announcementStatus);

    public AnnouncementStatus getAnnouncementStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;
}
