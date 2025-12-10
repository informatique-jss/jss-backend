package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.AssoAnnouncementNoticeTemplateAnnouncementFragment;
import com.jss.osiris.modules.osiris.quotation.repository.AssoAnnouncementNoticeTemplateFragmentRepository;

@Service
public class AssoAnnouncementNoticeTemplateFragmentServiceImpl
        implements AssoAnnouncementNoticeTemplateFragmentService {

    @Autowired
    AssoAnnouncementNoticeTemplateFragmentRepository assoAnnouncementNoticeTemplateFragmentRepository;

    @Override
    public List<AssoAnnouncementNoticeTemplateAnnouncementFragment> getAssoAnnouncementNoticeTemplateFragmentByNoticeTemplate(
            Integer idNoticeTemplateAnnouncement) {
        return assoAnnouncementNoticeTemplateFragmentRepository
                .findByAnnouncementNoticeTemplate_Id(idNoticeTemplateAnnouncement);
    }

    @Override
    public List<AssoAnnouncementNoticeTemplateAnnouncementFragment> addOrUpdateAssosAnnouncementNoticeTemplateFragments(
            List<AssoAnnouncementNoticeTemplateAnnouncementFragment> assosAnnouncementNoticeTemplateAnnouncementFragments) {
        return IterableUtils.toList(assoAnnouncementNoticeTemplateFragmentRepository
                .saveAll(assosAnnouncementNoticeTemplateAnnouncementFragments));
    }

}
