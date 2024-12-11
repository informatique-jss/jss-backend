package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.AffaireSearch;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.repository.AssoAffaireOrderRepository;
import com.jss.osiris.modules.quotation.service.guichetUnique.FormaliteGuichetUniqueService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.quotation.service.infoGreffe.FormaliteInfogreffeService;
import com.jss.osiris.modules.tiers.model.ITiers;

@org.springframework.stereotype.Service
public class AssoAffaireOrderServiceImpl implements AssoAffaireOrderService {

    @Autowired
    AssoAffaireOrderRepository assoAffaireOrderRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    DomiciliationStatusService domiciliationStatusService;

    @Autowired
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    ConstantService constantService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

    @Autowired
    FormaliteService formaliteService;

    @Autowired
    CharacterPriceService characterPriceService;

    @Autowired
    BatchService batchService;

    @Autowired
    FormaliteGuichetUniqueStatusService formaliteGuichetUniqueStatusService;

    @Autowired
    FormaliteInfogreffeService formaliteInfogreffeService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    ServiceService serviceService;

    @Override
    public List<AssoAffaireOrder> getAssoAffaireOrders() {
        return IterableUtils.toList(assoAffaireOrderRepository.findAll());
    }

    @Override
    public AssoAffaireOrder getAssoAffaireOrder(Integer id) {
        Optional<AssoAffaireOrder> assoAffaireOrder = assoAffaireOrderRepository.findById(id);
        if (assoAffaireOrder.isPresent())
            return assoAffaireOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoAffaireOrder addOrUpdateAssoAffaireOrderFromUser(
            AssoAffaireOrder assoAffaireOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // To avoid laizy failed of customerOrder subcollections
        assoAffaireOrder
                .setCustomerOrder(customerOrderService.getCustomerOrder(assoAffaireOrder.getCustomerOrder().getId()));
        return addOrUpdateAssoAffaireOrder(assoAffaireOrder);
    }

    @Override
    public AssoAffaireOrder addOrUpdateAssoAffaireOrder(
            AssoAffaireOrder assoAffaireOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        for (Service service : assoAffaireOrder.getServices()) {
            service.setAssoAffaireOrder(assoAffaireOrder);
            if (service.getAssoServiceDocuments() != null)
                for (AssoServiceDocument assoServiceDocument : service.getAssoServiceDocuments())
                    assoServiceDocument.setService(service);

            for (Provision provision : service.getProvisions()) {
                provision.setService(service);
            }
        }

        assoAffaireOrder = completeAssoAffaireOrder(assoAffaireOrder, assoAffaireOrder.getCustomerOrder(), true);
        assoAffaireOrder.setCustomerOrder(assoAffaireOrder.getCustomerOrder());
        AssoAffaireOrder affaireSaved = assoAffaireOrderRepository.save(assoAffaireOrder);
        if (affaireSaved.getCustomerOrder() != null)
            batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, affaireSaved.getId());
        customerOrderService.checkAllProvisionEnded(assoAffaireOrder.getCustomerOrder());
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForAsso(AssoAffaireOrder asso, Employee employee) throws OsirisException {
        asso.setAssignedTo(employee);
        assoAffaireOrderRepository.save(asso);
        if (asso.getCustomerOrder() != null)
            batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, asso.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexAffaires() throws OsirisException {
        List<AssoAffaireOrder> affaires = getAssoAffaireOrders();
        if (affaires != null)
            for (AssoAffaireOrder asso : affaires)
                if (asso.getCustomerOrder() != null)
                    batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, asso.getId());
    }

    @Override
    public AssoAffaireOrder completeAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder, IQuotation customerOrder,
            Boolean isFromUser)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Complete domiciliation end date
        int nbrAssignation = 0;
        Employee currentEmployee = null;
        Employee maxWeightEmployee = null;
        Integer maxWeight = -1000000000;

        for (Service service : assoAffaireOrder.getServices()) {

            service.setAssoAffaireOrder(assoAffaireOrder);
            if (service.getAssoServiceDocuments() != null)
                for (AssoServiceDocument assoServiceDocument : service.getAssoServiceDocuments())
                    assoServiceDocument.setService(service);

            // Complete field id_service in AssoServiceFieldType table
            if (service.getAssoServiceFieldTypes() != null)
                for (AssoServiceFieldType assoServiceFieldType : service.getAssoServiceFieldTypes())
                    assoServiceFieldType.setService(service);

            for (Provision provision : service.getProvisions()) {
                provision.setService(service);

                if (provision.getAttachments() != null)
                    for (Attachment attachment : provision.getAttachments()) {
                        attachment.setProvision(provision);
                        attachmentService.addOrUpdateAttachment(attachment);
                    }

                if (provision.getId() != null && provision.getInvoiceItems() != null)
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                        if (invoiceItem.getId() != null)
                            invoiceItem = invoiceItemService.getInvoiceItem(invoiceItem.getId());
                    }

                if (provision.getDomiciliation() != null) {
                    Domiciliation domiciliation = provision.getDomiciliation();
                    if (customerOrder.getId() == null || domiciliation.getDomiciliationStatus() == null)
                        domiciliation.setDomiciliationStatus(domiciliationStatusService
                                .getDomiciliationStatusByCode(DomiciliationStatus.DOMICILIATION_NEW));

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getMails() != null
                            && domiciliation.getMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getActivityMails() != null
                            && domiciliation.getActivityMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getActivityMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null
                            && domiciliation.getLegalGardianMails() != null
                            && domiciliation.getLegalGardianMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getLegalGardianMails());

                    if (domiciliation != null
                            && domiciliation.getLegalGardianPhones() != null
                            && domiciliation.getLegalGardianPhones().size() > 0)
                        phoneService.populatePhoneIds(domiciliation.getLegalGardianPhones());

                }

                if (provision.getFormalite() != null) {
                    Formalite formalite = provision.getFormalite();
                    if (customerOrder.getId() == null || formalite.getFormaliteStatus() == null)
                        formalite.setFormaliteStatus(
                                formaliteStatusService
                                        .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_NEW));

                    else if (formalite.getFormalitesGuichetUnique() != null) {
                        for (FormaliteGuichetUnique formaliteGuichetUnique : formalite
                                .getFormalitesGuichetUnique()) {
                            if (formaliteGuichetUnique.getStatus() == null
                                    || !formaliteGuichetUniqueStatusService
                                            .getFormaliteGuichetUniqueStatus(
                                                    formaliteGuichetUnique.getStatus().getCode())
                                            .getIsCloseState()) {
                                formaliteGuichetUnique.setFormalite(formalite);
                                formaliteGuichetUniqueService
                                        .addOrUpdateFormaliteGuichetUnique(formaliteGuichetUnique);
                            }

                            batchService.declareNewBatch(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE,
                                    formaliteGuichetUnique.getId());
                        }
                    }
                    if (formalite.getFormalitesInfogreffe() != null) {
                        for (FormaliteInfogreffe formaliteInfogreffe : formalite
                                .getFormalitesInfogreffe()) {
                            formaliteInfogreffe.setFormalite(formalite);
                            formaliteInfogreffeService.addOrUpdateFormaliteInfogreffe(formaliteInfogreffe);
                            batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE_DETAIL,
                                    formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());
                        }
                    }

                    if (formalite.getId() != null) {
                        Formalite originalFormalite = formaliteService.getFormalite(formalite.getId());
                        if (originalFormalite != null
                                && originalFormalite.getFormalitesGuichetUnique() != null
                                && originalFormalite.getFormalitesGuichetUnique().size() > 0) {
                            for (FormaliteGuichetUnique formaliteGuichetUniqueOrigin : originalFormalite
                                    .getFormalitesGuichetUnique()) {
                                Boolean found = false;
                                if (formalite.getFormalitesGuichetUnique() != null)
                                    for (FormaliteGuichetUnique formaliteGuichetUnique : formalite
                                            .getFormalitesGuichetUnique())
                                        if (formaliteGuichetUnique.getId()
                                                .equals(formaliteGuichetUniqueOrigin.getId()))
                                            found = true;
                                if (!found)
                                    formaliteGuichetUniqueOrigin.setFormalite(null);
                                formaliteGuichetUniqueService
                                        .addOrUpdateFormaliteGuichetUnique(formaliteGuichetUniqueOrigin);

                                if (formalite.getFormaliteStatus().getIsCloseState()) {
                                    if (formaliteGuichetUniqueStatusService
                                            .getFormaliteGuichetUniqueStatus(
                                                    formaliteGuichetUniqueOrigin.getStatus().getCode())
                                            .getIsCloseState() == false)
                                        throw new OsirisClientMessageException(
                                                "Impossible de terminer la formalité, le dossier GU n'est pas terminé");
                                }
                            }
                        }
                        if (originalFormalite != null
                                && originalFormalite.getFormalitesInfogreffe() != null
                                && originalFormalite.getFormalitesInfogreffe().size() > 0) {
                            for (FormaliteInfogreffe formaliteInfogreffeOrigin : originalFormalite
                                    .getFormalitesInfogreffe()) {
                                Boolean found = false;
                                if (formalite.getFormalitesInfogreffe() != null)
                                    for (FormaliteInfogreffe formaliteInfogreffe : formalite
                                            .getFormalitesInfogreffe())
                                        if (formaliteInfogreffe.getId()
                                                .equals(formaliteInfogreffeOrigin.getId()))
                                            found = true;
                                if (!found) {
                                    formaliteInfogreffeOrigin.setFormalite(null);
                                    formaliteInfogreffeService
                                            .addOrUpdateFormaliteInfogreffe(formaliteInfogreffeOrigin);
                                }
                            }
                        }
                        if (formalite.getFormalitesGuichetUnique() != null
                                && formalite.getFormalitesGuichetUnique().size() > 0)
                            for (FormaliteGuichetUnique formaliteGuichetUnique : formalite
                                    .getFormalitesGuichetUnique()) {
                                if (formaliteGuichetUnique.getFormalite() == null) {
                                    FormaliteGuichetUnique currentFormaliteGuichetUnique = formaliteGuichetUniqueService
                                            .getFormaliteGuichetUnique(formaliteGuichetUnique.getId());
                                    if (currentFormaliteGuichetUnique != null) {
                                        currentFormaliteGuichetUnique.setFormalite(formalite);
                                        formaliteGuichetUniqueService
                                                .addOrUpdateFormaliteGuichetUnique(currentFormaliteGuichetUnique);
                                    } else {
                                        formaliteGuichetUnique.setFormalite(formalite);
                                        formaliteGuichetUniqueService
                                                .addOrUpdateFormaliteGuichetUnique(formaliteGuichetUnique);
                                    }
                                }
                            }

                        if (formalite.getFormalitesInfogreffe() != null
                                && formalite.getFormalitesInfogreffe().size() > 0)
                            for (FormaliteInfogreffe formaliteInfogreffe : formalite
                                    .getFormalitesInfogreffe()) {
                                if (formaliteInfogreffe.getFormalite() == null) {
                                    formaliteInfogreffe.setFormalite(formalite);
                                    formaliteInfogreffeService
                                            .addOrUpdateFormaliteInfogreffe(formaliteInfogreffe);
                                }
                            }
                    }
                }

                if (provision.getAnnouncement() != null) {
                    Announcement announcement = provision.getAnnouncement();

                    announcementService.completeAnnouncementWithAffaire(assoAffaireOrder);

                    // If complex, extract string from PDF and put it to notice
                    if (announcement.getIsComplexAnnouncement())
                        announcement = announcementService.updateComplexAnnouncementNotice(announcement,
                                provision,
                                isFromUser);

                    announcement
                            .setCharacterNumber(characterPriceService.getCharacterNumber(provision, true));

                    if (customerOrder.getId() == null || announcement.getAnnouncementStatus() == null)
                        announcement.setAnnouncementStatus(announcementStatusService
                                .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_NEW));

                    if (announcement.getDocuments() != null)
                        for (Document document : announcement.getDocuments())
                            document.setAnnouncement(announcement);

                    boolean publicationProofFound = false;
                    if (announcement != null) {
                        if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                            for (Attachment attachment : provision.getAttachments())
                                if (attachment.getAttachmentType().getId()
                                        .equals(constantService.getAttachmentTypePublicationProof()
                                                .getId())) {
                                    publicationProofFound = true;
                                    break;
                                }
                    }

                    // Handle status change
                    if (announcement.getAnnouncementStatus() != null
                            && announcement.getConfrere() != null) {

                        if (announcement.getConfrere().getId()
                                .equals(constantService.getConfrereJssSpel().getId())
                                &&
                                announcement.getAnnouncementStatus().getCode()
                                        .equals(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED)) {
                            announcement.setAnnouncementStatus(announcementStatusService
                                    .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE));
                        }

                        // If JSS generate publication receipt if user accept
                        if (announcement.getConfrere().getId()
                                .equals(constantService.getConfrereJssSpel().getId())) {
                            if (announcement.getAnnouncementStatus().getCode()
                                    .equals(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS)) {
                                announcementService.generateStoreAndSendPublicationReceipt(
                                        (CustomerOrder) customerOrder,
                                        announcement);
                            }
                        } else {
                            // Else send publication receipt when it's available and if user accept
                            if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                                for (Attachment attachment : provision.getAttachments())
                                    if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypePublicationReceipt()
                                                    .getId())) {
                                        announcementService.generateStoreAndSendPublicationReceipt(
                                                (CustomerOrder) customerOrder,
                                                announcement);
                                        break;
                                    }
                        }

                        if (announcement.getAnnouncementStatus().getCode()
                                .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE)) {
                            boolean generateWord = true;
                            if (provision.getAnnouncement().getId() != null) {
                                Announcement currentAnnouncement = announcementService
                                        .getAnnouncement(provision.getAnnouncement().getId());

                                if (currentAnnouncement.getAnnouncementStatus().getId()
                                        .equals(announcement.getAnnouncementStatus().getId()))
                                    generateWord = !currentAnnouncement.getNotice().equals(announcement.getNotice());
                            }

                            if (generateWord)
                                announcementService.generateAndStoreAnnouncementWordFile(
                                        (CustomerOrder) customerOrder,
                                        assoAffaireOrder, provision, announcement);
                        }
                        if (publicationProofFound && announcement.getAnnouncementStatus().getCode()
                                .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED))
                            announcement.setAnnouncementStatus(announcementStatusService
                                    .getAnnouncementStatusByCode(
                                            AnnouncementStatus.ANNOUNCEMENT_PUBLISHED));

                        if (announcement.getIsProofReadingDocument()
                                && announcement.getAnnouncementStatus().getCode()
                                        .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER)) {
                            if (announcement.getFirstClientReviewSentMailDateTime() == null) {
                                announcement.setFirstClientReviewReminderDateTime(null);
                                announcement.setSecondClientReviewReminderDateTime(null);
                                announcement.setThirdClientReviewReminderDateTime(null);
                                announcementService.generateStoreAndSendProofReading(announcement,
                                        (CustomerOrder) customerOrder);
                            }
                        }

                        if (announcement.getAnnouncementStatus().getCode()
                                .equals(AnnouncementStatus.ANNOUNCEMENT_DONE)) {
                            announcementService.generateStoreAndSendPublicationFlag(
                                    (CustomerOrder) customerOrder,
                                    announcement);
                        }
                    }

                }

                if (provision.getSimpleProvision() != null) {
                    SimpleProvision simpleProvision = provision.getSimpleProvision();
                    if (customerOrder.getId() == null || simpleProvision.getSimpleProvisionStatus() == null)
                        simpleProvision.setSimpleProvisionStatus(simpleProvisionStatusService
                                .getSimpleProvisionStatusByCode(
                                        SimpleProvisionStatus.SIMPLE_PROVISION_NEW));
                }

                // Auto associate payment and provider invoice if only one match is found
                if (provision != null && provision.getId() != null) {
                    Provision originalProvision = provisionService.getProvision(provision.getId());
                    if (customerOrder instanceof CustomerOrder
                            && ((CustomerOrder) customerOrder).getCustomerOrderStatus().getCode()
                                    .equals(CustomerOrderStatus.BILLED)
                            && originalProvision.getPayments() != null
                            && originalProvision.getPayments().size() > 0
                            && originalProvision.getProviderInvoices() != null
                            && originalProvision.getProviderInvoices().size() > 0) {
                        outerloop: for (Payment payment : originalProvision.getPayments()) {
                            Invoice invoiceFound = null;
                            if (payment.getIsCancelled() == false) {
                                for (Invoice invoice : originalProvision.getProviderInvoices()) {
                                    if (invoice.getInvoiceStatus().getId()
                                            .equals(constantService.getInvoiceStatusReceived().getId())) {
                                        if (Math.round(
                                                invoice.getTotalPrice()
                                                        * 100f) == (Math
                                                                .round(-payment.getPaymentAmount()
                                                                        * 100f))) {
                                            if (invoiceFound != null)
                                                continue outerloop;
                                            invoiceFound = invoice;
                                        }
                                    }
                                }
                                if (invoiceFound != null)
                                    paymentService.manualMatchPaymentInvoicesAndCustomerOrders(payment,
                                            Arrays.asList(invoiceFound), null, null, null, null, null,
                                            null);
                            }
                        }
                    }
                }

                // Set proper assignation regarding provision item configuration
                if (provision.getAssignedTo() == null && customerOrder instanceof CustomerOrder) {
                    Employee employee = provision.getProvisionType().getDefaultEmployee();

                    if (provision.getProvisionType().getAssignationType() != null) {
                        if (provision.getProvisionType().getAssignationType().getCode()
                                .equals(AssignationType.FORMALISTE)) {
                            if (customerOrder.getConfrere() != null
                                    && customerOrder.getConfrere().getFormalisteEmployee() != null)
                                employee = customerOrder.getConfrere().getFormalisteEmployee();
                            if (customerOrder.getResponsable() != null) {
                                if (customerOrder.getResponsable().getFormalisteEmployee() != null)
                                    employee = customerOrder.getResponsable().getFormalisteEmployee();
                                else if (customerOrder.getResponsable().getTiers()
                                        .getFormalisteEmployee() != null)
                                    employee = customerOrder.getResponsable().getTiers()
                                            .getFormalisteEmployee();
                            } else if (customerOrder.getTiers() != null
                                    && customerOrder.getTiers().getFormalisteEmployee() != null)
                                employee = customerOrder.getTiers().getFormalisteEmployee();
                        }
                        if (provision.getProvisionType().getAssignationType().getCode()
                                .equals(AssignationType.PUBLICISTE)) {
                            if (customerOrder.getConfrere() != null
                                    && customerOrder.getConfrere().getInsertionEmployee() != null)
                                employee = customerOrder.getConfrere().getInsertionEmployee();
                            if (customerOrder.getResponsable() != null) {
                                if (customerOrder.getResponsable().getInsertionEmployee() != null)
                                    employee = customerOrder.getResponsable().getInsertionEmployee();
                                else if (customerOrder.getResponsable().getTiers()
                                        .getInsertionEmployee() != null)
                                    employee = customerOrder.getResponsable().getTiers()
                                            .getInsertionEmployee();
                            } else if (customerOrder.getTiers() != null
                                    && customerOrder.getTiers().getInsertionEmployee() != null)
                                employee = customerOrder.getTiers().getInsertionEmployee();
                        }
                    }
                    provision.setAssignedTo(employee);
                    if (currentEmployee == null || !currentEmployee.getId().equals(employee.getId())) {
                        currentEmployee = employee;
                        nbrAssignation++;
                    }

                    // Handle weight
                    if (provision.getProvisionType().getAssignationWeight() != null
                            && provision.getProvisionType().getAssignationWeight() > maxWeight) {
                        maxWeight = provision.getProvisionType().getAssignationWeight();
                        maxWeightEmployee = employee;
                    }
                }
            }
        }
        if (nbrAssignation == 1 && assoAffaireOrder.getAssignedTo() == null)
            assoAffaireOrder.setAssignedTo(currentEmployee);

        if (maxWeightEmployee != null)

        {
            if (assoAffaireOrder.getAssignedTo() == null)
                assoAffaireOrder.setAssignedTo(maxWeightEmployee);

            for (Service service : assoAffaireOrder.getServices())
                for (Provision provision : service.getProvisions())
                    if (provision.getId() == null)
                        provision.setAssignedTo(maxWeightEmployee);
        }

        return assoAffaireOrder;
    }

    @Override
    public ArrayList<AssoAffaireOrderSearchResult> searchForAsso(AffaireSearch affaireSearch) {
        ArrayList<Integer> statusId = null;
        ArrayList<Integer> assignedId = null;
        ArrayList<Integer> responsibleId = null;
        Integer affaireId = null;

        statusId = new ArrayList<Integer>();
        if (affaireSearch.getStatus() != null) {
            for (IWorkflowElement status : affaireSearch.getStatus())
                statusId.add(status.getId());
        }
        if (statusId.size() == 0)
            statusId.add(0);

        assignedId = new ArrayList<Integer>();
        if (affaireSearch.getAssignedTo() != null) {
            assignedId.add(affaireSearch.getAssignedTo().getId());
        } else {
            assignedId.add(0);
        }

        responsibleId = new ArrayList<Integer>();
        if (affaireSearch.getResponsible() != null) {
            responsibleId.add(affaireSearch.getResponsible().getId());
        } else {
            responsibleId.add(0);
        }

        Integer commercialId = 0;
        if (affaireSearch.getCommercial() != null) {
            commercialId = affaireSearch.getCommercial().getId();
        }

        affaireId = 0;
        if (affaireSearch.getAffaire() != null)
            affaireId = affaireSearch.getAffaire().getId();

        if (affaireSearch.getLabel() == null)
            affaireSearch.setLabel("");

        ArrayList<Integer> customerOrderId = new ArrayList<Integer>();
        if (affaireSearch.getCustomerOrders() != null && affaireSearch.getCustomerOrders().size() > 0) {
            for (ITiers tiers : affaireSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        Integer waitedCompetentAuthorityId = 0;
        if (affaireSearch.getWaitedCompetentAuthority() != null)
            waitedCompetentAuthorityId = affaireSearch.getWaitedCompetentAuthority().getId();

        String formaliteGuichetUniqueStatusCode = "0";
        if (affaireSearch.getFormaliteGuichetUniqueStatus() != null)
            formaliteGuichetUniqueStatusCode = affaireSearch.getFormaliteGuichetUniqueStatus().getCode();

        String formaliteInfogreffeStatusCode = "0";
        if (affaireSearch.getFormaliteInfogreffeStatusCode() != null)
            formaliteInfogreffeStatusCode = affaireSearch.getFormaliteInfogreffeStatusCode();

        ArrayList<String> excludedCustomerOrderStatusCode = new ArrayList<String>();
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.OPEN);
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.WAITING_DEPOSIT);
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.ABANDONED);

        if (affaireSearch.getIsMissingQueriesToManualRemind() == null)
            affaireSearch.setIsMissingQueriesToManualRemind(false);

        return assoAffaireOrderRepository.findAsso(responsibleId,
                assignedId, affaireSearch.getLabel(),
                statusId, excludedCustomerOrderStatusCode, customerOrderId, waitedCompetentAuthorityId, affaireId,
                affaireSearch.getIsMissingQueriesToManualRemind(),
                simpleProvisionStatusService
                        .getSimpleProvisionStatusByCode(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT)
                        .getId(),
                formaliteStatusService.getFormaliteStatusByCode(FormaliteStatus.FORMALITE_WAITING_DOCUMENT).getId(),
                commercialId, formaliteGuichetUniqueStatusCode, formaliteInfogreffeStatusCode);
    }
}
