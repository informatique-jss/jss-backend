package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
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
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.BodaccStatus;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;
import com.jss.osiris.modules.quotation.repository.AssoAffaireOrderRepository;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class AssoAffaireOrderServiceImpl implements AssoAffaireOrderService {

    @Autowired
    AssoAffaireOrderRepository assoAffaireOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    BodaccStatusService bodaccStatusService;

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
    DebourService debourService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    PaymentService paymentService;

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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // To avoid laizy failed of customerOrder subcollections
        assoAffaireOrder
                .setCustomerOrder(customerOrderService.getCustomerOrder(assoAffaireOrder.getCustomerOrder().getId()));
        return addOrUpdateAssoAffaireOrder(assoAffaireOrder);
    }

    @Override
    public AssoAffaireOrder addOrUpdateAssoAffaireOrder(
            AssoAffaireOrder assoAffaireOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        for (Provision provision : assoAffaireOrder.getProvisions()) {
            provision.setAssoAffaireOrder(assoAffaireOrder);
        }

        assoAffaireOrder = completeAssoAffaireOrder(assoAffaireOrder, assoAffaireOrder.getCustomerOrder());
        assoAffaireOrder.setCustomerOrder(assoAffaireOrder.getCustomerOrder());
        AssoAffaireOrder affaireSaved = assoAffaireOrderRepository.save(assoAffaireOrder);
        if (affaireSaved.getCustomerOrder() != null)
            indexEntityService.indexEntity(affaireSaved, affaireSaved.getId());
        customerOrderService.checkAllProvisionEnded(assoAffaireOrder.getCustomerOrder());
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForAsso(AssoAffaireOrder asso, Employee employee) {
        asso.setAssignedTo(employee);
        assoAffaireOrderRepository.save(asso);
    }

    @Override
    public void reindexAffaires() {
        List<AssoAffaireOrder> affaires = getAssoAffaireOrders();
        if (affaires != null)
            for (AssoAffaireOrder affaire : affaires)
                indexEntityService.indexEntity(affaire, affaire.getId());
    }

    @Override
    public AssoAffaireOrder completeAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder, IQuotation customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Complete domiciliation end date
        int nbrAssignation = 0;
        Employee currentEmployee = null;
        Employee maxWeightEmployee = null;
        Integer maxWeight = -1000000000;

        for (Provision provision : assoAffaireOrder.getProvisions()) {
            provision.setAssoAffaireOrder(assoAffaireOrder);

            if (provision.getAttachments() != null)
                for (Attachment attachment : provision.getAttachments()) {
                    attachment.setProvision(provision);
                    attachmentService.addOrUpdateAttachment(attachment);
                }

            if (provision.getId() != null && provision.getInvoiceItems() != null)
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    invoiceItem.setProvision(provision);
                    invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                }

            if (provision.getId() != null && provision.getDebours() != null && customerOrder instanceof CustomerOrder)
                for (Debour debour : provision.getDebours()) {
                    if (debour.getProvision() == null)
                        debour.setProvision(provision);

                    boolean isNewDebour = debour.getId() == null;
                    if (isNewDebour)
                        debourService.addOrUpdateDebour(debour);

                    if (isNewDebour && debour.getPaymentType().getId()
                            .equals(constantService.getPaymentTypeCheques().getId())) {
                        debourService.addOrUpdateDebour(debour);
                        accountingRecordService.generateBankAccountingRecordsForOutboundDebourPayment(debour,
                                (CustomerOrder) customerOrder);
                    } else if (isNewDebour && debour.getPaymentType().getId()
                            .equals(constantService.getPaymentTypeEspeces().getId())) {
                        // Generate dummy payment on cash because it will not be declared on OFX files
                        debour.setPayment(paymentService.generateNewPaymentFromDebour(debour));
                        debourService.addOrUpdateDebour(debour);
                        debourService.setDebourAsAssociated(debour);
                        accountingRecordService.generateBankAccountingRecordsForOutboundDebourPayment(debour,
                                (CustomerOrder) customerOrder);
                    }
                }

            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (customerOrder.getId() == null || domiciliation.getDomiciliationStatus() == null)
                    domiciliation.setDomiciliationStatus(domiciliationStatusService
                            .getDomiciliationStatusByCode(DomiciliationStatus.DOMICILIATION_NEW));

                if (domiciliation.getEndDate() == null && domiciliation.getStartDate() != null)
                    domiciliation.setEndDate(domiciliation.getStartDate().plusYears(1));

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
                            formaliteStatusService.getFormaliteStatusByCode(FormaliteStatus.FORMALITE_NEW));

                if (formalite.getReferenceMandataire() == null)
                    // Play with fire ...
                    formalite.setReferenceMandataire(formalite.getId() + "");
                if (formalite.getNomDossier() == null)
                    formalite
                            .setNomDossier(assoAffaireOrder.getAffaire().getDenomination() != null
                                    ? assoAffaireOrder.getAffaire().getDenomination()
                                    : (assoAffaireOrder.getAffaire().getFirstname() + " "
                                            + assoAffaireOrder.getAffaire().getLastname()));
                if (formalite.getSignedPlace() == null)
                    formalite.setSignedPlace("8 Rue Saint-Augustin, 75002 Paris");

            }

            if (provision.getBodacc() != null) {
                Bodacc bodacc = provision.getBodacc();
                if (customerOrder.getId() == null || bodacc.getBodaccStatus() == null)
                    bodacc.setBodaccStatus(bodaccStatusService.getBodaccStatusByCode(BodaccStatus.BODACC_NEW));

            }

            if (provision.getAnnouncement() != null) {
                Announcement announcement = provision.getAnnouncement();
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
                                    .equals(constantService.getAttachmentTypePublicationProof().getId())) {
                                publicationProofFound = true;
                                break;
                            }
                }

                // Handle status change
                if (announcement.getAnnouncementStatus() != null && announcement.getConfrere() != null) {

                    if (announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId()) &&
                            announcement.getAnnouncementStatus().getCode()
                                    .equals(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED)) {
                        announcement.setAnnouncementStatus(announcementStatusService
                                .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE));
                    }

                    // If JSS generate publication receipt if user accept
                    if (announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId())) {
                        if (announcement.getAnnouncementStatus().getCode()
                                .equals(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS)) {
                            announcementService.generateStoreAndSendPublicationReceipt((CustomerOrder) customerOrder,
                                    announcement);
                        }
                    } else {
                        // Else send publication receipt when it's available and if user accept
                        if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                            for (Attachment attachment : provision.getAttachments())
                                if (attachment.getAttachmentType().getId()
                                        .equals(constantService.getAttachmentTypePublicationReceipt().getId())) {
                                    announcementService.generateStoreAndSendPublicationReceipt(
                                            (CustomerOrder) customerOrder,
                                            announcement);
                                    break;
                                }
                    }

                    if (announcement.getAnnouncementStatus().getCode()
                            .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE)) {
                        announcementService.generateAndStoreAnnouncementWordFile((CustomerOrder) customerOrder,
                                assoAffaireOrder, provision, announcement);
                    }
                    if (publicationProofFound && announcement.getAnnouncementStatus().getCode()
                            .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED))
                        announcement.setAnnouncementStatus(announcementStatusService
                                .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED));

                    if (announcement.getIsProofReadingDocument() && announcement.getAnnouncementStatus().getCode()
                            .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER)) {
                        announcementService.generateStoreAndSendProofReading(announcement,
                                (CustomerOrder) customerOrder);
                    }

                    if (announcement.getAnnouncementStatus().getCode().equals(AnnouncementStatus.ANNOUNCEMENT_DONE)) {
                        announcementService.generateStoreAndSendPublicationFlag((CustomerOrder) customerOrder,
                                announcement);
                    }
                }

            }

            if (provision.getSimpleProvision() != null) {
                SimpleProvision simpleProvision = provision.getSimpleProvision();
                if (customerOrder.getId() == null || simpleProvision.getSimpleProvisionStatus() == null)
                    simpleProvision.setSimpleProvisionStatus(simpleProvisionStatusService
                            .getSimpleProvisionStatusByCode(SimpleProvisionStatus.SIMPLE_PROVISION_NEW));
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
                            else if (customerOrder.getResponsable().getTiers().getFormalisteEmployee() != null)
                                employee = customerOrder.getResponsable().getTiers().getFormalisteEmployee();
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
                            else if (customerOrder.getResponsable().getTiers().getInsertionEmployee() != null)
                                employee = customerOrder.getResponsable().getTiers().getInsertionEmployee();
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
        if (nbrAssignation == 1)
            assoAffaireOrder.setAssignedTo(currentEmployee);

        if (maxWeightEmployee != null) {
            assoAffaireOrder.setAssignedTo(maxWeightEmployee);
            for (Provision provision : assoAffaireOrder.getProvisions())
                provision.setAssignedTo(maxWeightEmployee);
        }

        return assoAffaireOrder;
    }

    @Override
    public ArrayList<AssoAffaireOrderSearchResult> searchForAsso(AffaireSearch affaireSearch) {
        ArrayList<Integer> statusId = null;
        ArrayList<Integer> assignedId = null;
        ArrayList<Integer> responsibleId = null;

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

        ArrayList<String> excludedCustomerOrderStatusCode = new ArrayList<String>();
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.OPEN);
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.WAITING_DEPOSIT);
        excludedCustomerOrderStatusCode.add(CustomerOrderStatus.ABANDONED);

        return assoAffaireOrderRepository.findAsso(responsibleId,
                assignedId, affaireSearch.getLabel(),
                statusId, excludedCustomerOrderStatusCode, customerOrderId, waitedCompetentAuthorityId);
    }

}
