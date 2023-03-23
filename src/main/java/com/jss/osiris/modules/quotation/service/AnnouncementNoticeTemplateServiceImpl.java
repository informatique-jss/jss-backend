package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.AnnouncementNoticeTemplate;
import com.jss.osiris.modules.quotation.repository.AnnouncementNoticeTemplateRepository;

@Service
public class AnnouncementNoticeTemplateServiceImpl implements AnnouncementNoticeTemplateService {

    @Autowired
    AnnouncementNoticeTemplateRepository announcementNoticeTemplateRepository;

    @Override
    @Cacheable(value = "announcementNoticeTemplateList", key = "#root.methodName")
    public List<AnnouncementNoticeTemplate> getAnnouncementNoticeTemplates() {
        return IterableUtils.toList(announcementNoticeTemplateRepository.findAll());
    }

    @Override
    @Cacheable(value = "announcementNoticeTemplate", key = "#id")
    public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate(Integer id) {
        Optional<AnnouncementNoticeTemplate> announcementNoticeTemplate = announcementNoticeTemplateRepository
                .findById(id);
        if (announcementNoticeTemplate.isPresent())
            return announcementNoticeTemplate.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "announcementNoticeTemplateList", allEntries = true),
            @CacheEvict(value = "announcementNoticeTemplate", key = "#announcementNoticeTemplate.id")
    })
    public AnnouncementNoticeTemplate addOrUpdateAnnouncementNoticeTemplate(
            AnnouncementNoticeTemplate announcementNoticeTemplate) {
        return announcementNoticeTemplateRepository.save(announcementNoticeTemplate);
    }
}
