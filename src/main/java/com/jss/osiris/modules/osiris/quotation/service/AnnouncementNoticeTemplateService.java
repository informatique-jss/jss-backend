package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.AnnouncementNoticeTemplate;

public interface AnnouncementNoticeTemplateService {
    public List<AnnouncementNoticeTemplate> getAnnouncementNoticeTemplates();

    public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate(Integer id);

    public AnnouncementNoticeTemplate addOrUpdateAnnouncementNoticeTemplate(
            AnnouncementNoticeTemplate announcementNoticeTemplate);
}
