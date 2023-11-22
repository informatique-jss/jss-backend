package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementListSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;

public interface AnnouncementService {
        public List<Announcement> getAnnouncements();

        public Announcement getAnnouncement(Integer id);

        public Announcement addOrUpdateAnnouncement(Announcement announcement) throws OsirisException;

        public Announcement updateComplexAnnouncementNotice(Announcement announcement, Provision provision,
                        Boolean isFromUser)
                        throws OsirisException;

        public List<AnnouncementSearchResult> searchAnnouncementsForWebSite(AnnouncementSearch announcementSearch)
                        throws OsirisException;

        public void publishAnnouncementsToActuLegale() throws OsirisException;

        public void generateStoreAndSendPublicationReceipt(CustomerOrder customerOrder, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateAndStorePublicationReceipt(Announcement announcement,
                        Provision currentProvision)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateStoreAndSendPublicationFlag(CustomerOrder customerOrder, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateAndStorePublicationFlag(Announcement announcement,
                        Provision currentProvision)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateStoreAndSendProofReading(Announcement announcement, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendPublicationFlagNotSent()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Announcement> searchAnnouncements(AnnouncementListSearch announcementSearch)
                        throws OsirisException;

        public void generateAndStoreAnnouncementWordFile(CustomerOrder customerOrder, AssoAffaireOrder asso,
                        Provision provision, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersToConfrereForAnnouncement()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersToCustomerForProofReading() throws OsirisException, OsirisClientMessageException;

        public Confrere getConfrereForAnnouncement(Integer idAnnouncement);

}
