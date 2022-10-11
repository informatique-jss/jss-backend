package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.AnnouncementNoticeTemplate;

public interface AnnouncementNoticeTemplateService {
    public List<AnnouncementNoticeTemplate> getAnnouncementNoticeTemplates();

    public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate(Integer id);

    public AnnouncementNoticeTemplate addOrUpdateAnnouncementNoticeTemplate(
            AnnouncementNoticeTemplate announcementNoticeTemplate);
}
