package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.jss.osiris.libs.PictureHelper;
import com.jss.osiris.libs.WordGenerationHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementListSearch;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementRepository;

@org.springframework.stereotype.Service
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

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    BatchService batchService;

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
    public Announcement addOrUpdateAnnouncement(Announcement announcement) throws OsirisException {
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement updateComplexAnnouncementNotice(Announcement announcement, Provision provision,
            Boolean isFromUser)
            throws OsirisException {
        // Get announcement PDF
        File complexePdf = null;
        if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
            for (Attachment attachment : provision.getAttachments())
                if (attachment.getAttachmentType().getId()
                        .equals(constantService.getAttachmentTypeComplexAnnouncement().getId())) {
                    complexePdf = new File(attachment.getUploadedFile().getPath());
                    break;
                }

        if (complexePdf == null)
            if (!isFromUser)
                return announcement;
            else
                throw new OsirisException(null, "No announncement PDF found");

        PdfReader reader;
        FileInputStream in;
        try {
            in = new FileInputStream(complexePdf.getAbsolutePath());
            reader = new PdfReader(in);
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to open PDF file");
        }
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        String announcementNotice = "";
        TextExtractionStrategy strategy;
        try {
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                announcementNotice += strategy.getResultantText();
            }
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to parse PDF file");
        }
        reader.close();

        announcement.setNoticeHeader("");
        announcement.setNotice(announcementNotice);
        return addOrUpdateAnnouncement(announcement);
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
                batchService.declareNewBatch(Batch.PUBLISH_ANNOUNCEMENT_TO_ACTU_LEGALE, announcement.getId());
            }
    }

    @Override
    @Transactional
    public void publishAnnouncementToActuLegale(Announcement announcement) throws OsirisException {
        if (announcement != null) {
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        // Get provision
        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null && provision.getAnnouncement().getId() != null
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        if (announcement.getConfrere() != null
                && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {
            File publicationReceiptPdf = generatePdfDelegate.generatePublicationForAnnouncement(announcement,
                    currentProvision, false, true, false);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(), null,
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypePublicationReceipt(),
                                "Publication_receipt_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Attestation de parution n°" + announcement.getId(), null, null, null));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }
    }

    @Override
    public void generateStoreAndSendPublicationFlag(CustomerOrder customerOrder, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        // Get provision
        Provision currentProvision = null;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null && provision.getAnnouncement().getId() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                            break;
                        }

        if (announcement.getPublicationDate().isBefore(LocalDate.now())
                || announcement.getPublicationDate().isEqual(LocalDate.now())) {
            if ((announcement.getIsPublicationFlagAlreadySent() == null
                    || !announcement.getIsPublicationFlagAlreadySent()) && announcement.getNotice() != null) {

                if (announcement.getConfrere() != null
                        && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId()))
                    generateAndStorePublicationFlag(announcement, currentProvision);
                mailHelper.sendPublicationFlagToCustomer(customerOrder, false, announcement);
                announcement.setIsPublicationFlagAlreadySent(true);
                addOrUpdateAnnouncement(announcement);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateAndStorePublicationFlag(Announcement announcement, Provision currentProvision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // To avoid no session error
        announcement = getAnnouncement(announcement.getId());
        currentProvision = provisionService.getProvision(currentProvision.getId());
        if (announcement.getConfrere() != null
                && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {
            File publicationReceiptPdf = generatePdfDelegate.generatePublicationForAnnouncement(announcement,
                    currentProvision, true, false, false);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(), null,
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypePublicationFlag(),
                                "Publication_flag_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Témoin de publication n°" + announcement.getId(), null, null, null));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPublicationFlagsNotSent() throws OsirisException {
        List<Announcement> announcements = announcementRepository.getAnnouncementForPublicationFlagBatch(
                announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE),
                LocalDate.now());

        if (announcements != null && announcements.size() > 0)
            for (Announcement announcement : announcements) {
                batchService.declareNewBatch(Batch.SEND_PUBLICATION_FLAG, announcement.getId());
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPublicationFlagNotSent(Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (announcement != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
            if (customerOrder == null)
                throw new OsirisException(null,
                        "Impossible to find Customer Order for Announcement n°" + announcement.getId());

            if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                announcement.setIsPublicationFlagAlreadySent(true);
                addOrUpdateAnnouncement(announcement);
            } else
                try {
                    generateStoreAndSendPublicationFlag(
                            customerOrderService.getCustomerOrderForAnnouncement(announcement),
                            announcement);
                } catch (OsirisClientMessageException e) {
                } // Do nothing, it's when publication flag not upload from user
        }
    }

    @Override
    public void generateStoreAndSendProofReading(Announcement announcement, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Get provision
        Provision currentProvision = null;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null && provision.getAnnouncement().getId() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                            break;
                        }

        if (currentProvision == null)
            return;

        if (announcement.getNotice() != null) {
            File publicationReceiptPdf = generatePdfDelegate.generatePublicationForAnnouncement(announcement,
                    currentProvision, false, false, true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                currentProvision.setAttachments(
                        attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                currentProvision.getId(), null,
                                Provision.class.getSimpleName(),
                                constantService.getAttachmentTypeProofReading(),
                                "Proof_reading_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                false, "Bon à tirer n°" + announcement.getId(), null, null, null));
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                publicationReceiptPdf.delete();
            }
        }

        mailHelper.sendProofReadingToCustomer(customerOrder, false, announcement, false);
        announcement.setFirstClientReviewSentMailDateTime(LocalDateTime.now());

        addOrUpdateAnnouncement(announcement);
    }

    @Override
    public void generateAndStoreAnnouncementWordFile(CustomerOrder customerOrder, AssoAffaireOrder asso,
            Provision provision, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (announcement.getIsAnnouncementAlreadySentToConfrere() == null
                || !announcement.getIsAnnouncementAlreadySentToConfrere()
                || announcement.getIsAnnouncementErratumAlreadySentToConfrere() == null
                || !announcement.getIsAnnouncementErratumAlreadySentToConfrere()) {
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
                    htmlContent += announcement.getNoticeHeader() + "<br/>";

                if (announcement.getNotice() != null)
                    htmlContent += announcement.getNotice();

                File wordFile = wordGenerationHelper.generateWordFromHtml(htmlContent);
                try {
                    provision.setAttachments(
                            attachmentService.addAttachment(new FileInputStream(wordFile),
                                    provision.getId(), null,
                                    Provision.class.getSimpleName(), constantService.getAttachmentTypeAnnouncement(),
                                    "announcement_" + announcement.getId()
                                            + DateTimeFormatter.ofPattern("yyyyMMdd HHmm").format(LocalDateTime.now())
                                            + ".docx",
                                    false, "Annonce n°" + announcement.getId(), null, null, null));
                } catch (FileNotFoundException e) {
                    throw new OsirisException(e, "Impossible to read announcement Word temp file");
                } finally {
                    wordFile.delete();
                }

                if (announcement.getIsAnnouncementAlreadySentToConfrere() == null
                        || !announcement.getIsAnnouncementAlreadySentToConfrere()) {
                    mailHelper.sendAnnouncementRequestToConfrere(
                            customerOrderService.getCustomerOrder(customerOrder.getId()), asso,
                            false, provision, announcement, false);
                    announcement.setIsAnnouncementAlreadySentToConfrere(true);
                    announcement.setFirstConfrereSentMailDateTime(LocalDateTime.now());
                } else if (announcement.getIsAnnouncementAlreadySentToConfrere() != null
                        && announcement.getIsAnnouncementAlreadySentToConfrere() &&
                        announcement.getFirstConfrereSentMailDateTime() != null) {
                    mailHelper.sendAnnouncementErratumToConfrere(
                            customerOrderService.getCustomerOrder(customerOrder.getId()), asso,
                            false, provision, announcement);
                    announcement.setIsAnnouncementErratumAlreadySentToConfrere(true);
                }

                addOrUpdateAnnouncement(announcement);
            }
        }
    }

    @Override
    @Transactional
    public void sendRemindersToConfrereForAnnouncement() throws OsirisException {
        List<Announcement> announcements = announcementRepository
                .getAnnouncementForConfrereReminder(announcementStatusService
                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE));

        if (announcements != null && announcements.size() > 0) {
            for (Announcement announcement : announcements) {
                if (announcement.getId() != null) {
                    CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
                    if (customerOrder.getCustomerOrderStatus() != null
                            && !customerOrder.getCustomerOrderStatus().getCode()
                                    .equals(CustomerOrderStatus.ABANDONED))
                        batchService.declareNewBatch(Batch.SEND_REMINDER_TO_CONFRERE_FOR_ANNOUNCEMENTS,
                                announcement.getId());
                }
            }
        }
    }

    @Override
    @Transactional
    public void sendRemindersToConfrereForProviderInvoice() throws OsirisException {
        List<Announcement> announcements = announcementRepository
                .getAnnouncementForConfrereReminderProviderInvoice(announcementStatusService
                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE),
                        constantService.getConfrereJssSpel());

        if (announcements != null && announcements.size() > 0) {
            for (Announcement announcement : announcements) {
                if (announcement.getId() != null) {
                    CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
                    if (customerOrder.getCustomerOrderStatus() != null
                            && !customerOrder.getCustomerOrderStatus().getCode()
                                    .equals(CustomerOrderStatus.ABANDONED))
                        batchService.declareNewBatch(Batch.SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE,
                                announcement.getId());
                }
            }
        }
    }

    @Override
    @Transactional
    public void sendReminderToConfrereForProviderInvoice(Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (announcement != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);

            if (announcement.getConfrere().getProvider().getIsRemindProviderInvoice() == null
                    || announcement.getConfrere().getProvider().getIsRemindProviderInvoice() == false)
                return;

            // Get provision
            Provision currentProvision = null;
            AssoAffaireOrder currentAsso = null;
            if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null) {
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                    for (Service service : asso.getServices())
                        for (Provision provision : service.getProvisions())
                            if (provision.getAnnouncement() != null
                                    && provision.getAnnouncement().getId().equals(announcement.getId())) {
                                currentProvision = provision;
                                currentAsso = asso;
                                break;
                            }

                boolean toSend = false;
                if (customerOrder.getCustomerOrderStatus() != null
                        && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                    if (announcement.getPublicationDate().isBefore(LocalDate.now().minusDays(1 * 7))) {
                        toSend = true;
                        announcement.setFirstConfrereReminderProviderInvoiceDateTime(LocalDateTime.now());
                    } else if (!toSend && announcement.getFirstConfrereReminderDateTime() != null) {
                        if (announcement.getFirstConfrereReminderDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                            toSend = true;
                            announcement.setSecondReminderProviderInvoiceDateTime(LocalDateTime.now());
                        }
                    } else if (!toSend && announcement.getSecondReminderProviderInvoiceDateTime() != null
                            && announcement.getThirdReminderProviderInvoiceDateTime() == null) {
                        if (announcement.getSecondReminderProviderInvoiceDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                            toSend = true;
                            announcement.setThirdConfrereReminderDateTime(LocalDateTime.now());
                        }
                    }

                    if (toSend) {
                        mailHelper.sendConfrereReminderProviderInvoice(
                                customerOrderService.getCustomerOrder(customerOrder.getId()), currentAsso,
                                false, currentProvision, announcement);
                        addOrUpdateAnnouncement(announcement);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void sendReminderToConfrereForAnnouncement(Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (announcement != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);

            // Get provision
            Provision currentProvision = null;
            AssoAffaireOrder currentAsso = null;
            if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null) {
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                    for (Service service : asso.getServices())
                        for (Provision provision : service.getProvisions())
                            if (provision.getAnnouncement() != null
                                    && provision.getAnnouncement().getId().equals(announcement.getId())) {
                                currentProvision = provision;
                                currentAsso = asso;
                                break;
                            }

                boolean toSend = false;
                if (customerOrder.getCustomerOrderStatus() != null
                        && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                    if (announcement.getFirstConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime() != null
                                && announcement.getFirstConfrereSentMailDateTime()
                                        .isBefore(LocalDateTime.now().minusDays(1 * 3))) {
                            toSend = true;
                            announcement.setFirstConfrereReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getSecondConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime() != null
                                && announcement.getFirstConfrereSentMailDateTime()
                                        .isBefore(LocalDateTime.now().minusDays(1 * 4))) {
                            toSend = true;
                            announcement.setSecondConfrereReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getThirdConfrereReminderDateTime() == null) {
                        if (announcement.getFirstConfrereSentMailDateTime() != null
                                && announcement.getFirstConfrereSentMailDateTime()
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

    @Override
    @Transactional
    public void sendRemindersToCustomerForProofReading() throws OsirisException, OsirisClientMessageException {

        List<Announcement> announcements = announcementRepository
                .getAnnouncementForCustomerProofReadingReminder(announcementStatusService
                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER));

        if (announcements != null && announcements.size() > 0) {
            for (Announcement announcement : announcements) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);

                boolean toSend = false;
                if (customerOrder.getCustomerOrderStatus() != null
                        && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                    if (announcement.getFirstClientReviewSentMailDateTime() != null) {
                        if (announcement.getFirstClientReviewReminderDateTime() == null) {
                            if (announcement.getFirstClientReviewSentMailDateTime()
                                    .isBefore(LocalDateTime.now().minusDays(1 * 2))) {
                                toSend = true;
                                announcement.setFirstClientReviewReminderDateTime(LocalDateTime.now());
                            }
                        } else if (announcement.getSecondClientReviewReminderDateTime() == null) {
                            if (announcement.getFirstClientReviewSentMailDateTime()
                                    .isBefore(LocalDateTime.now().minusDays(1 * 4))) {
                                toSend = true;
                                announcement.setSecondClientReviewReminderDateTime(LocalDateTime.now());
                            }
                        } else if (announcement.getThirdClientReviewReminderDateTime() == null) {
                            if (announcement.getFirstClientReviewSentMailDateTime()
                                    .isBefore(LocalDateTime.now().minusDays(1 * 6))) {
                                toSend = true;
                                announcement.setThirdClientReviewReminderDateTime(LocalDateTime.now());
                            }
                        }

                        if (toSend) {
                            mailHelper.sendProofReadingToCustomer(
                                    customerOrderService.getCustomerOrder(customerOrder.getId()), false, announcement,
                                    true);
                            addOrUpdateAnnouncement(announcement);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void sendReminderToCustomerForProofReading(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {

        if (announcement != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);

            boolean toSend = false;
            if (customerOrder.getCustomerOrderStatus() != null
                    && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                if (announcement.getFirstClientReviewSentMailDateTime() != null) {
                    if (announcement.getFirstClientReviewReminderDateTime() == null) {
                        if (announcement.getFirstClientReviewSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 2))) {
                            toSend = true;
                            announcement.setFirstClientReviewReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getSecondClientReviewReminderDateTime() == null) {
                        if (announcement.getFirstClientReviewSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 4))) {
                            toSend = true;
                            announcement.setSecondClientReviewReminderDateTime(LocalDateTime.now());
                        }
                    } else if (announcement.getThirdClientReviewReminderDateTime() == null) {
                        if (announcement.getFirstClientReviewSentMailDateTime()
                                .isBefore(LocalDateTime.now().minusDays(1 * 6))) {
                            toSend = true;
                            announcement.setThirdClientReviewReminderDateTime(LocalDateTime.now());
                        }
                    }

                    if (toSend) {
                        mailHelper.sendProofReadingToCustomer(
                                customerOrderService.getCustomerOrder(customerOrder.getId()), false, announcement,
                                true);
                        addOrUpdateAnnouncement(announcement);
                    }
                }
            }
        }
    }

    @Override
    public Confrere getConfrereForAnnouncement(Integer idAnnouncement) {
        Announcement announcement = getAnnouncement(idAnnouncement);
        if (announcement != null)
            return announcement.getConfrere();
        return null;
    }

    @Override
    public void completeAnnouncementWithAffaire(AssoAffaireOrder assoAffaireOrder)
            throws OsirisException, OsirisClientMessageException {

        Affaire affaire = assoAffaireOrder.getAffaire();

        for (Service service : assoAffaireOrder.getServices())
            for (Provision provision : service.getProvisions())
                if (provision.getAnnouncement() != null && provision.getAnnouncement().getNotice() != null) {
                    Announcement announcement = provision.getAnnouncement();

                    if (affaire.getIsIndividual()) {
                        if (affaire.getFirstname() != null)
                            announcement.setNotice(
                                    announcement.getNotice().replaceAll("\\{prenom\\}", affaire.getFirstname()));

                        if (affaire.getLastname() != null)
                            announcement
                                    .setNotice(announcement.getNotice().replaceAll("\\{nom\\}", affaire.getLastname()));

                        if (affaire.getCivility() != null)
                            announcement.setNotice(announcement.getNotice().replaceAll("\\{civilite\\}",
                                    affaire.getCivility().getLabel()));
                    } else if (affaire.getDenomination() != null) {
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{denomination\\}", affaire.getDenomination()));
                    }

                    if (affaire.getAcronym() != null)
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{sigle\\}", affaire.getAcronym()));

                    if (affaire.getAddress() != null)
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{adresse\\}", affaire.getAddress()));

                    if (affaire.getCity() != null)
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{ville\\}", affaire.getCity().getLabel()));

                    if (affaire.getCity() != null && affaire.getCity().getDepartment() != null)
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{departement\\}",
                                        affaire.getCity().getDepartment().getLabel()));

                    if (affaire.getPostalCode() != null)
                        announcement.setNotice(
                                announcement.getNotice().replaceAll("\\{codePostal\\}", affaire.getPostalCode()));

                    if (affaire.getMainActivity() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{activitePrincipale\\}",
                                affaire.getMainActivity().getLabel()));

                    if (affaire.getShareCapital() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{capitalSocial\\}",
                                affaire.getShareCapital().toString()));

                    if (affaire.getLegalForm() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{formeJuridique\\}",
                                affaire.getLegalForm().getLabel()));

                    if (affaire.getSiren() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{siren\\}", affaire.getSiren()));

                    if (affaire.getSiret() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{siret\\}", affaire.getSiret()));

                    if (affaire.getRna() != null)
                        announcement.setNotice(announcement.getNotice().replaceAll("\\{rna\\}", affaire.getRna()));

                    if (affaire.getCompetentAuthority() != null) {
                        announcement
                                .setNotice(announcement.getNotice().replaceAll("\\{autoriteCompetenteDenomination\\}",
                                        affaire.getCompetentAuthority().getLabel()));

                        if (affaire.getCompetentAuthority().getCity() != null)
                            announcement.setNotice(announcement.getNotice().replaceAll("\\{autoriteCompetenteVille\\}",
                                    affaire.getCompetentAuthority().getCity().getLabel()));
                    }
                }
    }

    @Override
    @Transactional
    public void sendRemindersToCustomerForBilanPublication() throws OsirisException {
        List<Announcement> announcements = announcementRepository
                .getAnnouncementForBilanPublicationReminder(announcementStatusService
                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE).getId(),
                        this.constantService.getProvisionTypeBilanPublication().getId());

        if (announcements != null && announcements.size() > 0) {
            for (Announcement announcement : announcements) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
                if (customerOrder.getCustomerOrderStatus() != null
                        && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
                    batchService.declareNewBatch(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_BILAN_PUBLICATION,
                            announcement.getId());
            }
        }
    }

    @Override
    @Transactional
    public void sendReminderToCustomerForBilanPublication(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {
        announcement = getAnnouncement(announcement.getId());

        CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
        if (customerOrder.getCustomerOrderStatus() != null
                && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
            if ((announcement.getIsBilanPublicationReminderIsSent() == null
                    || announcement.getIsBilanPublicationReminderIsSent() == false)
                    && announcement.getPublicationDate().plusYears(1).minusDays(30).isBefore(LocalDate.now())) {
                mailHelper.sendReminderToCustomerForBilanPublication(announcement,
                        customerOrderService.getCustomerOrderForAnnouncement(announcement));
                announcement.setIsBilanPublicationReminderIsSent(true);
                addOrUpdateAnnouncement(announcement);
            }
        }
    }

    @Override
    public List<Announcement> getAnnouncementForWebSite(Integer page, String denomination, String siren,
            String noticeSearch) throws OsirisException {
        Order order = new Order(Direction.DESC, "publicationDate");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);

        List<CustomerOrderStatus> customerOrderStatusExcluded = new ArrayList<CustomerOrderStatus>();
        customerOrderStatusExcluded
                .add(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED));

        List<AnnouncementStatus> announcementStatus = new ArrayList<AnnouncementStatus>();
        announcementStatus
                .add(announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED));
        announcementStatus
                .add(announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE));

        return populateAffaireLabel(
                announcementRepository.searchAnnouncementForWebSite(denomination, siren, noticeSearch,
                        customerOrderStatusExcluded, announcementStatus, constantService.getConfrereJssSpel(),
                        pageableRequest));
    }

    @Override
    public List<Announcement> getTopAnnouncementForWebSite(Integer page) throws OsirisException {
        Order order = new Order(Direction.DESC, "publicationDate");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);

        List<CustomerOrderStatus> customerOrderStatusExcluded = new ArrayList<CustomerOrderStatus>();
        customerOrderStatusExcluded
                .add(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED));

        List<AnnouncementStatus> announcementStatus = new ArrayList<AnnouncementStatus>();
        announcementStatus
                .add(announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED));
        announcementStatus
                .add(announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE));

        return populateAffaireLabel(
                announcementRepository.searchAnnouncementTopForWebSite(customerOrderStatusExcluded, announcementStatus,
                        constantService.getConfrereJssSpel(), LocalDate.now().minusMonths(3),
                        pageableRequest));
    }

    @Override
    public Announcement getAnnouncementForWebSite(Announcement announcement) throws OsirisException {
        announcement = getAnnouncement(announcement.getId());
        return populateAffaireLabel(Arrays.asList(announcement)).get(0);
    }

    private List<Announcement> populateAffaireLabel(List<Announcement> announcements) {
        if (announcements != null)
            for (Announcement announcement : announcements) {
                if (announcement.getProvisions() != null && announcement.getProvisions().size() > 0)
                    if (announcement.getProvisions().get(0).getService() != null)
                        if (announcement.getProvisions().get(0).getService().getAssoAffaireOrder() != null)
                            if (announcement.getProvisions().get(0).getService().getAssoAffaireOrder()
                                    .getAffaire() != null) {
                                announcement.setAffaireLabel(announcement.getProvisions().get(0).getService()
                                        .getAssoAffaireOrder().getAffaire().getDenomination() != null
                                                ? announcement.getProvisions().get(0).getService().getAssoAffaireOrder()
                                                        .getAffaire().getDenomination()
                                                : (announcement.getProvisions().get(0).getService()
                                                        .getAssoAffaireOrder().getAffaire().getFirstname() + ' '
                                                        + announcement.getProvisions().get(0).getService()
                                                                .getAssoAffaireOrder().getAffaire().getLastname()));
                                if (announcement.getProvisions().get(0).getService()
                                        .getAssoAffaireOrder().getAffaire().getSiren() != null)
                                    announcement.setAffaireSiren(announcement.getProvisions().get(0).getService()
                                            .getAssoAffaireOrder().getAffaire().getSiren());
                            }
            }
        return announcements;
    }
}
