package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoAnnouncementNoticeTemplateAnnouncementFragment;

public interface AssoAnnouncementNoticeTemplateFragmentRepository
                extends QueryCacheCrudRepository<AssoAnnouncementNoticeTemplateAnnouncementFragment, Integer> {

        List<AssoAnnouncementNoticeTemplateAnnouncementFragment> findByAnnouncementNoticeTemplate_Id(
                        Integer idAnnouncementNoticeTemplate);
}