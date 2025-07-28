package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.AnnouncementNoticeTemplateFragment;

public interface AnnouncementNoticeTemplateFragmentService {
    public List<AnnouncementNoticeTemplateFragment> getAnnouncementNoticeTemplateFragments();

    public AnnouncementNoticeTemplateFragment getAnnouncementNoticeTemplateFragment(Integer id);

    public AnnouncementNoticeTemplateFragment addOrUpdateAnnouncementNoticeTemplateFragment(AnnouncementNoticeTemplateFragment announcementNoticeTemplateFragment);
}
