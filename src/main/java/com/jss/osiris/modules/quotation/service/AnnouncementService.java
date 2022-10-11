package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.Announcement;

public interface AnnouncementService {
    public List<Announcement> getAnnouncements();

    public Announcement getAnnouncement(Integer id);
}
