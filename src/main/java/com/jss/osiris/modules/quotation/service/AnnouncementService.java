package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface AnnouncementService {
        public List<Announcement> getAnnouncements();

        public Announcement getAnnouncement(Integer id);

        public void generatePublicationProof(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException;

        public List<Announcement> getAnnouncementWaitingForPublicationProof() throws OsirisException;

        public Announcement addOrUpdateAnnouncement(Announcement announcement) throws OsirisException;

        public List<AnnouncementSearchResult> searchAnnouncements(AnnouncementSearch announcementSearch)
                        throws OsirisException;

        public void publishAnnouncementsToActuLegale() throws OsirisException;

        public void generateStoreAndSendPublicationReceipt(CustomerOrder customerOrder, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException;

        public void generateStoreAndSendPublicationFlag(CustomerOrder customerOrder, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException;

        public void generateStoreAndSendProofReading(Announcement announcement, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public void sendPublicationFlagNotSent() throws OsirisException, OsirisClientMessageException;

}
