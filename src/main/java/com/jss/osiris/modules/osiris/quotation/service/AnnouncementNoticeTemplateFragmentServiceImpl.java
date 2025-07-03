package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.quotation.model.AnnouncementNoticeTemplateFragment;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementNoticeTemplateFragmentRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementNoticeTemplateFragmentServiceImpl implements AnnouncementNoticeTemplateFragmentService {

    @Autowired
    AnnouncementNoticeTemplateFragmentRepository announcementNoticeTemplateFragmentRepository;

    @Override
    public List<AnnouncementNoticeTemplateFragment> getAnnouncementNoticeTemplateFragments() {
        return IterableUtils.toList(announcementNoticeTemplateFragmentRepository.findAll());
    }

    @Override
    public AnnouncementNoticeTemplateFragment getAnnouncementNoticeTemplateFragment(Integer id) {
        Optional<AnnouncementNoticeTemplateFragment> announcementNoticeTemplateFragment = announcementNoticeTemplateFragmentRepository.findById(id);
        if (announcementNoticeTemplateFragment.isPresent())
            return announcementNoticeTemplateFragment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementNoticeTemplateFragment addOrUpdateAnnouncementNoticeTemplateFragment(
            AnnouncementNoticeTemplateFragment announcementNoticeTemplateFragment) {
        return announcementNoticeTemplateFragmentRepository.save(announcementNoticeTemplateFragment);
    }
}
