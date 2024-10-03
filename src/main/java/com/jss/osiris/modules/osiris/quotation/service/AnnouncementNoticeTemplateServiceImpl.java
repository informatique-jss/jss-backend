package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.AnnouncementNoticeTemplate;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementNoticeTemplateRepository;

@Service
public class AnnouncementNoticeTemplateServiceImpl implements AnnouncementNoticeTemplateService {

    @Autowired
    AnnouncementNoticeTemplateRepository announcementNoticeTemplateRepository;

    @Override
    public List<AnnouncementNoticeTemplate> getAnnouncementNoticeTemplates() {
        return IterableUtils.toList(announcementNoticeTemplateRepository.findAll());
    }

    @Override
    public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate(Integer id) {
        Optional<AnnouncementNoticeTemplate> announcementNoticeTemplate = announcementNoticeTemplateRepository
                .findById(id);
        if (announcementNoticeTemplate.isPresent())
            return announcementNoticeTemplate.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementNoticeTemplate addOrUpdateAnnouncementNoticeTemplate(
            AnnouncementNoticeTemplate announcementNoticeTemplate) {
        return announcementNoticeTemplateRepository.save(announcementNoticeTemplate);
    }
}
