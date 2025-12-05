package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.AssoAnnouncementNoticeTemplateAnnouncementFragment;

public interface AssoAnnouncementNoticeTemplateFragmentService {

    public List<AssoAnnouncementNoticeTemplateAnnouncementFragment> getAssoAnnouncementNoticeTemplateFragmentByNoticeTemplate(
            Integer idNoticeTemplateAnnouncement);
}
