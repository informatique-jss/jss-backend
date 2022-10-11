package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.repository.AnnouncementRepository;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getAnnouncements() {
        return IterableUtils.toList(announcementRepository.findAll());
    }

    @Override
    public Announcement getAnnouncement(Integer id) {
        Optional<Announcement> announcement = announcementRepository.findById(id);
        if (announcement.isPresent())
            return announcement.get();
        return null;
    }
}
