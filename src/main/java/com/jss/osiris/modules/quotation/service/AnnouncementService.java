package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;

public interface AnnouncementService {
    public List<Announcement> getAnnouncements();

    public Announcement getAnnouncement(Integer id);

    public void generatePublicationProof(Announcement announcement) throws OsirisException;

    public List<Announcement> getAnnouncementWaitingForPublicationProof() throws OsirisException;

    public Announcement addOrUpdateAnnouncement(Announcement announcement) throws OsirisException;

    public List<AnnouncementSearchResult> searchAnnouncements(AnnouncementSearch announcementSearch)
            throws OsirisException;

    public void publishAnnouncementsToActuLegale() throws OsirisException;
}
