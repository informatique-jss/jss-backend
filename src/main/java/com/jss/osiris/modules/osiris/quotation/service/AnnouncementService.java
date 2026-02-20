package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementListSearch;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

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

        public void publishAnnouncementToActuLegale(Announcement announcement) throws OsirisException;

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

        public void sendPublicationFlagsNotSent() throws OsirisException;

        public void sendPublicationFlagNotSent(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Announcement> searchAnnouncements(AnnouncementListSearch announcementSearch)
                        throws OsirisException;

        public void generateAndStoreAnnouncementWordFile(CustomerOrder customerOrder, AssoAffaireOrder asso,
                        Provision provision, Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersToConfrereForAnnouncement() throws OsirisException;

        public void sendReminderToConfrereForAnnouncement(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersToConfrereForProviderInvoice() throws OsirisException;

        public void sendReminderToConfrereForProviderInvoice(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersToCustomerForProofReading() throws OsirisException, OsirisClientMessageException;

        public void sendReminderToCustomerForProofReading(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException;

        public Confrere getConfrereForAnnouncement(Integer idAnnouncement);

        public void completeAnnouncementWithAffaire(AssoAffaireOrder assoAffaireOrder)
                        throws OsirisException, OsirisClientMessageException;

        public void sendRemindersToCustomerForBilanPublication() throws OsirisException;

        public void sendReminderToCustomerForBilanPublication(Announcement announcement)
                        throws OsirisException, OsirisClientMessageException;

        public Page<Announcement> getAnnouncementSearch(String searchText, LocalDate startDate,
                        Pageable pageableRequest)
                        throws OsirisException;

        public Announcement getAnnouncementForWebSite(Announcement announcement) throws OsirisException;

        public List<Announcement> getLastSevenDaysAnnouncements() throws OsirisException;

        public List<Announcement> getAllAnnouncementsForWebsite() throws OsirisException;

        public String getNoticeFromFile(MultipartFile file) throws OsirisException;

}
