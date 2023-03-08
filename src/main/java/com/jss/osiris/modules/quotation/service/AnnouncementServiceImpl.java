package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.PictureHelper;
import com.jss.osiris.libs.WordGenerationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementListSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.AnnouncementRepository;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ActuLegaleDelegate actuLegaleDelegate;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    WordGenerationHelper wordGenerationHelper;

    @Autowired
    PictureHelper pictureHelper;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

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

    @Override
    public Announcement addOrUpdateAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public List<AnnouncementSearchResult> searchAnnouncementsForWebSite(AnnouncementSearch announcementSearch)
            throws OsirisException {
        if (announcementSearch.getAffaireName() == null)
            announcementSearch.setAffaireName("");
        else
            announcementSearch.setAffaireName(announcementSearch.getAffaireName().trim());

        if (announcementSearch.getIsStricNameSearch() == null)
            announcementSearch.setIsStricNameSearch(false);

        Integer departementId = 0;
        if (announcementSearch.getDepartment() != null)
            departementId = announcementSearch.getDepartment().getId();

        Integer noticeTypeId = 0;
        if (announcementSearch.getNoticeType() != null)
            noticeTypeId = announcementSearch.getNoticeType().getId();

        if (announcementSearch.getStartDate() == null)
            announcementSearch.setStartDate(LocalDate.now().minusYears(100));

        if (announcementSearch.getEndDate() == null)
            announcementSearch.setEndDate(LocalDate.now().plusYears(100));

        // Keep only finalized announcement
        ArrayList<Integer> announcementStautsId = new ArrayList<Integer>();
        AnnouncementStatus closeStatus = announcementStatusService
                .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE);
        announcementStautsId.add(closeStatus.getId());

        CustomerOrderStatus customerOrderStatusExcluded = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED);

        return announcementRepository.searchAnnouncements(announcementSearch.getAffaireName(),
                announcementSearch.getIsStricNameSearch(), departementId,
                announcementSearch.getStartDate(),
                announcementSearch.getEndDate(), announcementStautsId, constantService.getConfrereJssSpel().getId(),
                noticeTypeId, customerOrderStatusExcluded.getId());
    }

    @Override
    public List<Announcement> searchAnnouncements(AnnouncementListSearch announcementSearch)
            throws OsirisException {

        if (announcementSearch.getStartDate() == null)
            announcementSearch.setStartDate(LocalDate.now().minusYears(100));

        if (announcementSearch.getEndDate() == null)
            announcementSearch.setEndDate(LocalDate.now().plusYears(100));

        return announcementRepository.getAnnouncementByStatusPublicationDateAndConfrere(
                announcementSearch.getAnnouncementStatus(),
                announcementSearch.getStartDate(),
                announcementSearch.getEndDate(), announcementSearch.getConfrere());
    }

    @Override
    @Transactional
    public void publishAnnouncementsToActuLegale() throws OsirisException {
        List<Announcement> announcements = announcementRepository.getAnnouncementByStatusAndPublicationDateMin(
                announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE),
                LocalDate.now().minusDays(3), constantService.getConfrereJssSpel());
        if (announcements != null && announcements.size() > 0)
            for (Announcement announcement : announcements) {
                Integer affaire = announcementRepository.getAffaireForAnnouncement(announcement.getId());
                if (affaire == null)
                    throw new OsirisException(null,
                            "Impossible to find affaire for announcement n°" + announcement.getId());

                ActuLegaleAnnouncement actuLegaleAnnouncement = actuLegaleDelegate.publishAnnouncement(announcement,
                        affaireService.getAffaire(affaire));

                if (actuLegaleAnnouncement == null || actuLegaleAnnouncement.getId() == null)
                    throw new OsirisException(null, "Impossible to publish announcement n°" + announcement.getId());

                announcement.setActuLegaleId(actuLegaleAnnouncement.getId());
                addOrUpdateAnnouncement(announcement);
            }
    }

    @Override
    public void generateStoreAndSendPublicationReceipt(CustomerOrder customerOrder, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        // Get provision
        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                            break;
                        }

        if (announcement.getIsPublicationReciptAlreadySent() == null
                || !announcement.getIsPublicationReciptAlreadySent()) {
            generateAndStorePublicationReceipt(announcement, currentProvision);

            // Try to send whereas it was JSS or not
            mailHelper.sendPublicationReceiptToCustomer(
                    customerOrderService.getCustomerOrder(customerOrder.getId()),
                    false, announcement);

            announcement.setIsPublicationReciptAlreadySent(true);
            addOrUpdateAnnouncement(announcement);
        }

    }

    @Override
    public void generateAndStorePublicationReceipt(Announcement announcement, Provision currentProvision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (announcement.getConfrere() != null
                && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {
            File publicationReceiptPdf = mailHelper.generatePublicationReceiptPdf(announcement, true,
                    currentProvision);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(),
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypePublicationReceipt(),
                                "Publication_receipt_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Attestation de parution n°" + announcement.getId()));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }
    }

    @Override
    public void generateStoreAndSendPublicationFlag(CustomerOrder customerOrder, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        // Get provision
        Provision currentProvision = null;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                            break;
                        }

        if (announcement.getPublicationDate().isBefore(LocalDate.now())
                || announcement.getPublicationDate().isEqual(LocalDate.now())) {
            if ((announcement.getIsPublicationFlagAlreadySent() == null
                    || !announcement.getIsPublicationFlagAlreadySent()) && announcement.getNotice() != null) {
                generateAndStorePublicationFlag(announcement, currentProvision);
                mailHelper.sendPublicationFlagToCustomer(customerOrder, false, announcement);
                announcement.setIsPublicationFlagAlreadySent(true);
                addOrUpdateAnnouncement(announcement);
            }
        }
    }

    @Override
    public void generateAndStorePublicationFlag(Announcement announcement, Provision currentProvision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (announcement.getConfrere() != null
                && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {
            File publicationReceiptPdf = mailHelper.generatePublicationFlagPdf(announcement, currentProvision);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(),
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypePublicationFlag(),
                                "Publication_flag_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Témoin de publication n°" + announcement.getId()));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPublicationFlagNotSent()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Announcement> announcements = announcementRepository.getAnnouncementForPublicationFlagBatch(
                announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE),
                LocalDate.now());

        if (announcements != null && announcements.size() > 0)
            for (Announcement announcement : announcements) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
                if (customerOrder == null)
                    throw new OsirisException(null,
                            "Impossible to find Customer Order for Announcement n°" + announcement.getId());

                generateStoreAndSendPublicationFlag(customerOrderService.getCustomerOrderForAnnouncement(announcement),
                        announcement);
            }
    }

    @Override
    public void generateStoreAndSendProofReading(Announcement announcement, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Get provision
        Provision currentProvision = null;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                            break;
                        }

        if (currentProvision == null)
            return;

        // Check if publication receipt already exists
        boolean proofReading = false;
        if (currentProvision.getAttachments() != null)
            for (Attachment attachment : currentProvision.getAttachments())
                if (attachment.getAttachmentType().getId()
                        .equals(constantService.getAttachmentTypeProofReading().getId()))
                    proofReading = true;

        if (!proofReading && announcement.getNotice() != null) {
            File publicationReceiptPdf = mailHelper.generatePublicationReceiptPdf(announcement, false,
                    currentProvision);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(),
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypeProofReading(),
                                "Proof_reading_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Bon à tirer n°" + announcement.getId()));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }

        mailHelper.sendProofReadingToCustomer(customerOrder, false, announcement);
    }

    @Override
    public void generateAndStoreAnnouncementWordFile(CustomerOrder customerOrder, AssoAffaireOrder asso,
            Provision provision, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (announcement.getIsAnnouncementAlreadySentToConfrere() == null
                || !announcement.getIsAnnouncementAlreadySentToConfrere()) {
            if (announcement.getConfrere() != null
                    && !announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {

                // Get and set logo
                String htmlContent = "";
                if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                    for (Attachment attachment : provision.getAttachments())
                        if (attachment.getAttachmentType().getId()
                                .equals(constantService.getAttachmentTypeLogo().getId())) {
                            htmlContent += "<div class=\"alignment\" align=\"center\"><img style=\"max-width:500px\" src=\"data:image/jpeg;base64,"
                                    + pictureHelper.getPictureFileAsBase64String(
                                            new File(attachment.getUploadedFile().getPath()))
                                    + "\" /></div><br/>";
                            break;
                        }

                if (announcement.getNoticeHeader() != null)
                    htmlContent += announcement.getNoticeHeader() + "</br>";

                if (announcement.getNotice() != null)
                    htmlContent += announcement.getNotice();

                File wordFile = wordGenerationHelper.generateWordFromHtml(htmlContent);
                try {
                    provision.setAttachments(
                            attachmentService.addAttachment(new FileInputStream(wordFile),
                                    provision.getId(),
                                    Provision.class.getSimpleName(), constantService.getAttachmentTypeAnnouncement(),
                                    "announcement_" + announcement.getId()
                                            + DateTimeFormatter.ofPattern("yyyyMMdd HHmm").format(LocalDateTime.now())
                                            + ".docx",
                                    false, "Annonce n°" + announcement.getId()));
                } catch (FileNotFoundException e) {
                    throw new OsirisException(e, "Impossible to read announcement Word temp file");
                } finally {
                    wordFile.delete();
                }

                // Try to send whereas it was JSS or not
                mailHelper.sendAnnouncementRequestToConfrere(
                        customerOrderService.getCustomerOrder(customerOrder.getId()), asso,
                        false, provision, announcement, false);

                announcement.setIsAnnouncementAlreadySentToConfrere(true);
                announcement.setFirstConfrereSentMailDateTime(LocalDateTime.now());
                addOrUpdateAnnouncement(announcement);
            }
        }
    }

    @Override
    @Transactional
    public void sendRemindersToConfrereForAnnouncement() throws OsirisException, OsirisClientMessageException {
        List<Announcement> announcements = announcementRepository
                .getAnnouncementForConfrereReminder(announcementStatusService
                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE));

        if (announcements != null && announcements.size() > 0) {
            for (Announcement announcement : announcements) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);

                // Get provision
                Provision currentProvision = null;
                AssoAffaireOrder currentAsso = null;
                if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null) {
                    for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                        if (asso.getProvisions() != null)
                            for (Provision provision : asso.getProvisions())
                                if (provision.getAnnouncement() != null
                                        && provision.getAnnouncement().getId().equals(announcement.getId())) {
                                    currentProvision = provision;
                                    currentAsso = asso;
                                    break;
                                }

                    boolean toSend = false;
                    if (announcement.getFirstConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 3))) {
                            toSend = true;
                            announcement.setFirstConfrereReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getSecondConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 4))) {
                            toSend = true;
                            announcement.setSecondConfrereReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getThirdConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                            toSend = true;
                            announcement.setThirdConfrereReminderDateTime(LocalDateTime.now());
                        }
                    }

                    if (toSend) {
                        mailHelper.sendAnnouncementRequestToConfrere(
                                customerOrderService.getCustomerOrder(customerOrder.getId()), currentAsso,
                                false, currentProvision, announcement, true);
                        addOrUpdateAnnouncement(announcement);
                    }
                }
            }
        }
    }

}
