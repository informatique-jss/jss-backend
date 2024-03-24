package com.jss.osiris.modules.quotation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IDocument;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.ProvisionTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@org.springframework.stereotype.Service
public class QuotationValidationHelper {

        @Autowired
        ValidationHelper validationHelper;

        @Autowired
        QuotationService quotationService;

        @Autowired
        CustomerOrderService customerOrderService;

        @Autowired
        ConstantService constantService;

        @Autowired
        DocumentService documentService;

        @Autowired
        AnnouncementService announcementService;

        @Autowired
        ProvisionService provisionService;

        @Autowired
        ProvisionTypeService provisionTypeService;

        @Autowired
        CustomerOrderOriginService customerOrderOriginService;

        @Autowired
        ActiveDirectoryHelper activeDirectoryHelper;

        @Transactional(rollbackFor = Exception.class)
        public void validateQuotationAndCustomerOrder(IQuotation quotation, String targetStatusCode)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                boolean isOpen = false;

                if (targetStatusCode != null) {
                        // Is for status update, override input quotation with database one
                        if (quotation instanceof CustomerOrder)
                                quotation = customerOrderService.getCustomerOrder(quotation.getId());
                        if (quotation instanceof Quotation)
                                quotation = quotationService.getQuotation(quotation.getId());
                }

                if (quotation.getId() == null) {
                        quotation.setCreatedDate(LocalDateTime.now());

                        List<CustomerOrderOrigin> origins = customerOrderOriginService
                                        .getByUsername(activeDirectoryHelper.getCurrentUsername());
                        if (origins != null && origins.size() == 1)
                                quotation.setCustomerOrderOrigin(origins.get(0));
                        else
                                quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());
                }

                if (quotation.getCustomerOrderOrigin() == null)
                        quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());

                if (quotation instanceof CustomerOrder) {
                        CustomerOrder customerOrder = (CustomerOrder) quotation;
                        isOpen = customerOrder.getCustomerOrderStatus() == null ||
                                        customerOrder.getCustomerOrderStatus().getCode()
                                                        .equals(CustomerOrderStatus.OPEN);

                        if (targetStatusCode != null)
                                isOpen = targetStatusCode.equals(CustomerOrderStatus.OPEN);
                }

                if (quotation instanceof Quotation) {
                        Quotation quotationQuotation = (Quotation) quotation;
                        isOpen = quotationQuotation.getQuotationStatus() == null ||
                                        quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN);
                }

                validationHelper.validateReferential(quotation.getAssignedTo(), false, "AssignedTo");

                if (targetStatusCode != null) {
                        IWorkflowElement status = null;
                        if (quotation instanceof CustomerOrder) {
                                status = ((CustomerOrder) quotation).getCustomerOrderStatus();
                        } else {
                                status = ((Quotation) quotation).getQuotationStatus();
                        }
                        boolean correctStatus = false;
                        if (status.getSuccessors() != null) {
                                for (IWorkflowElement successor : status.getSuccessors())
                                        if (successor.getCode().equals(targetStatusCode))
                                                correctStatus = true;
                        }
                        if (!correctStatus && status.getPredecessors() != null) {
                                for (IWorkflowElement predecessor : status.getPredecessors())
                                        if (predecessor.getCode().equals(targetStatusCode))
                                                correctStatus = true;
                        }

                        if (!correctStatus)
                                throw new OsirisValidationException("Wrong status : not set accordingly to workflow !");
                }

                boolean isCustomerOrder = quotation instanceof CustomerOrder;

                if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0)
                        for (SpecialOffer specialOffer : quotation.getSpecialOffers())
                                validationHelper.validateReferential(specialOffer, false, "specialOffer");

                // If new or if from website, grab special offer from tiers / responsable /
                // confrere
                if (quotation.getCustomerOrderOrigin().getId()
                                .equals(constantService.getCustomerOrderOriginWebSite().getId())
                                && (quotation.getSpecialOffers() == null || quotation.getSpecialOffers().size() == 0)
                                || quotation.getId() == null) {
                        ITiers tiers = quotationService.getCustomerOrderOfQuotation(quotation);
                        if (tiers instanceof Responsable)
                                tiers = ((Responsable) tiers).getTiers();

                        List<SpecialOffer> specialOffers = null;
                        if (tiers instanceof Tiers && ((Tiers) tiers).getSpecialOffers() != null
                                        && ((Tiers) tiers).getSpecialOffers().size() > 0)
                                specialOffers = ((Tiers) tiers).getSpecialOffers();
                        if (tiers instanceof Confrere && ((Confrere) tiers).getSpecialOffers() != null
                                        && ((Confrere) tiers).getSpecialOffers().size() > 0)
                                specialOffers = ((Confrere) tiers).getSpecialOffers();

                        if (specialOffers != null) {
                                quotation.setSpecialOffers(new ArrayList<SpecialOffer>());
                                for (SpecialOffer specialOffer : specialOffers)
                                        quotation.getSpecialOffers().add(specialOffer);
                        }
                }

                quotation.setTiers((Tiers) validationHelper.validateReferential(quotation.getTiers(), false, "Tiers"));
                quotation.setResponsable(
                                (Responsable) validationHelper.validateReferential(quotation.getResponsable(), false,
                                                "Responsable"));

                if (quotation.getTiers() != null && quotation.getConfrere() == null
                                && quotation.getResponsable() == null
                                && quotation.getTiers().getIsIndividual() == false)
                        throw new OsirisValidationException("Tiers must be individual !");

                quotation.setConfrere(
                                (Confrere) validationHelper.validateReferential(quotation.getConfrere(), false,
                                                "Confrere"));

                if (quotation.getResponsable() == null && quotation.getTiers() == null
                                && quotation.getConfrere() == null)
                        throw new OsirisValidationException("No customer order");

                if (quotation.getResponsable() != null && !quotation.getResponsable().getIsActive())
                        throw new OsirisClientMessageException(
                                        "Il n'est pas possible d'utiliser un responsable inactif !");

                // Generate missing documents
                IDocument tiersDocument = ObjectUtils.firstNonNull(quotation.getConfrere(), quotation.getResponsable(),
                                quotation.getTiers());
                Document billingDocument = documentService.getBillingDocument(tiersDocument.getDocuments());
                if (documentService.getBillingDocument(quotation.getDocuments()) == null && billingDocument != null) {
                        billingDocument = documentService.cloneDocument(billingDocument);
                        billingDocument.setTiers(null);
                        billingDocument.setResponsable(null);
                        billingDocument.setConfrere(null);
                        if (isCustomerOrder)
                                billingDocument.setCustomerOrder((CustomerOrder) quotation);
                        else
                                billingDocument.setQuotation((Quotation) quotation);
                        if (quotation.getDocuments() == null)
                                quotation.setDocuments(new ArrayList<Document>());
                        quotation.getDocuments().add(billingDocument);
                }

                Document digitalDocument = documentService.getDocumentByDocumentType(tiersDocument.getDocuments(),
                                constantService.getDocumentTypeDigital());
                if (documentService.getDocumentByDocumentType(quotation.getDocuments(),
                                constantService.getDocumentTypeDigital()) == null && digitalDocument != null) {
                        digitalDocument = documentService.cloneDocument(digitalDocument);
                        digitalDocument.setTiers(null);
                        digitalDocument.setResponsable(null);
                        digitalDocument.setConfrere(null);
                        if (isCustomerOrder)
                                digitalDocument.setCustomerOrder((CustomerOrder) quotation);
                        else
                                digitalDocument.setQuotation((Quotation) quotation);
                        if (quotation.getDocuments() == null)
                                quotation.setDocuments(new ArrayList<Document>());
                        quotation.getDocuments().add(digitalDocument);
                }

                Document paperDocument = documentService.getDocumentByDocumentType(tiersDocument.getDocuments(),
                                constantService.getDocumentTypePaper());
                if (documentService.getDocumentByDocumentType(quotation.getDocuments(),
                                constantService.getDocumentTypePaper()) == null && paperDocument != null) {
                        paperDocument = documentService.cloneDocument(paperDocument);
                        paperDocument.setTiers(null);
                        paperDocument.setResponsable(null);
                        paperDocument.setConfrere(null);
                        if (isCustomerOrder)
                                paperDocument.setCustomerOrder((CustomerOrder) quotation);
                        else
                                paperDocument.setQuotation((Quotation) quotation);
                        if (quotation.getDocuments() == null)
                                quotation.setDocuments(new ArrayList<Document>());
                        quotation.getDocuments().add(paperDocument);
                }

                // Do not check anything from website with no provision, a human will correct if
                // after
                if (isOpen && quotation.getCustomerOrderOrigin().getId()
                                .equals(constantService.getCustomerOrderOriginWebSite().getId())
                                && (quotation.getAssoAffaireOrders() == null
                                                || quotation.getAssoAffaireOrders().size() == 0))
                        return;

                // Check customer order is not a prospect
                if (isCustomerOrder && !isOpen) {
                        if (quotation.getTiers() != null
                                        && quotation.getTiers().getTiersType().getId()
                                                        .equals(constantService.getTiersTypeProspect().getId()))
                                throw new OsirisClientMessageException(
                                                "Le donneur d'ordre ne doit pas être un prospect");

                        if (quotation.getResponsable() != null
                                        && quotation.getResponsable().getTiersType().getId()
                                                        .equals(constantService.getTiersTypeProspect().getId()))
                                throw new OsirisClientMessageException(
                                                "Le donneur d'ordre ne doit pas être un prospect");
                }

                if (quotation.getAssoAffaireOrders() == null || quotation.getAssoAffaireOrders().size() == 0)
                        throw new OsirisValidationException("No asso affaire order");

                if (quotation.getAssoAffaireOrders().get(0).getAffaire() == null) {
                        throw new OsirisValidationException("No affaire");
                }
                if (quotation.getAssoAffaireOrders().get(0).getServices() == null
                                || quotation.getAssoAffaireOrders().get(0).getServices().size() == 0)
                        throw new OsirisValidationException("No service");

                if (quotation.getDocuments() != null && quotation.getDocuments().size() > 0) {
                        for (Document document : quotation.getDocuments()) {

                                validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

                                if (document.getMailsAffaire() != null
                                                && !validationHelper.validateMailList(document.getMailsAffaire()))
                                        throw new OsirisValidationException("MailsAffaire");
                                if (document.getMailsClient() != null && document.getMailsClient() != null
                                                && document.getMailsClient().size() > 0)
                                        if (!validationHelper.validateMailList(document.getMailsClient()))
                                                throw new OsirisValidationException("MailsClient");

                                validationHelper.validateString(document.getAffaireAddress(), false, 200,
                                                "AffaireAddress");
                                validationHelper.validateString(document.getClientAddress(), false, 100,
                                                "ClientAddress");
                                validationHelper.validateString(document.getAffaireRecipient(), false, 100,
                                                "AffaireRecipient");
                                validationHelper.validateString(document.getClientRecipient(), false, 200,
                                                "ClientRecipient");
                                validationHelper.validateString(document.getCommandNumber(), false, 40,
                                                "CommandNumber");
                                validationHelper.validateReferential(document.getPaymentDeadlineType(), false,
                                                "PaymentDeadlineType");
                                validationHelper.validateReferential(document.getRefundType(), false, "RefundType");
                                validationHelper.validateIban(document.getRefundIBAN(), false, "RefundIBAN");
                                validationHelper.validateBic(document.getRefundBic(), false, "RefundBic");
                                validationHelper.validateReferential(document.getBillingClosureType(), false,
                                                "BillingClosureType");
                                validationHelper.validateReferential(document.getBillingClosureRecipientType(), false,
                                                "BillingClosureRecipientType");

                                if (document.getIsRecipientAffaire() == null)
                                        document.setIsRecipientAffaire(false);
                                if (document.getIsRecipientClient() == null)
                                        document.setIsRecipientClient(false);

                        }
                }

                for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                        if (assoAffaireOrder.getAffaire() == null)
                                throw new OsirisValidationException("Affaire");
                        assoAffaireOrder
                                        .setAffaire((Affaire) validationHelper.validateReferential(
                                                        assoAffaireOrder.getAffaire(), true,
                                                        "Affaire"));

                        if (assoAffaireOrder.getServices() == null || assoAffaireOrder.getServices().size() == 0)
                                throw new OsirisClientMessageException("Au moins un service est nécessaire");

                        for (Service service : assoAffaireOrder.getServices())
                                if (service.getProvisions() == null
                                                || service.getProvisions().size() == 0)
                                        throw new OsirisClientMessageException(
                                                        "Chaque service doit avoir au moins une prestation");

                        for (Service service : assoAffaireOrder.getServices())
                                for (Provision provision : service.getProvisions()) {
                                        validateProvision(provision, targetStatusCode, isCustomerOrder, quotation);
                                }
                }

                if (quotation.getAssoAffaireOrders().size() > 1) {
                        billingDocument = documentService.getBillingDocument(quotation.getDocuments());
                        // If recipient affaire and no override, we can't determine what affaire to use
                        if (billingDocument != null && billingDocument.getIsRecipientAffaire()
                                        && (billingDocument.getMailsAffaire() == null
                                                        || billingDocument.getMailsAffaire().size() == 0))
                                throw new OsirisValidationException("Too many affaire");
                }

        }

        @Transactional
        public void validateProvisionTransactionnal(Provision provision, String targetStatusCode,
                        CustomerOrder customerOrder)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                validateProvision(provision, targetStatusCode, true,
                                customerOrderService.getCustomerOrder(customerOrder.getId()));
        }

        private void validateProvision(Provision provision, String targetStatusCode, boolean isCustomerOrder,
                        IQuotation quotation)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {

                validationHelper.validateReferential(provision.getProvisionFamilyType(), true, "Famille de prestation");
                validationHelper.validateReferential(provision.getProvisionType(), true, "Type de prestation");

                if (quotation.getId() == null && !quotation.getCustomerOrderOrigin().getId()
                                .equals(constantService.getCustomerOrderOriginOsiris().getId()))
                        provision.setAssignedTo(null);

                if (provision.getIsRneUpdate() == null)
                        provision.setIsRneUpdate(false);

                // Domiciliation
                if (provision.getDomiciliation() != null) {
                        Domiciliation domiciliation = provision.getDomiciliation();
                        validationHelper.validateReferential(domiciliation.getDomiciliationContractType(), false,
                                        "DomiciliationContractType");
                        validationHelper.validateReferential(domiciliation.getLanguage(), false, "Language");
                        validationHelper.validateReferential(domiciliation.getBuildingDomiciliation(), false,
                                        "BuildingDomiciliation");
                        validationHelper.validateReferential(domiciliation.getMailRedirectionType(), false,
                                        "MailRedirectionType");
                        validationHelper.validateString(domiciliation.getAddress(), false, 60, "Address");
                        validationHelper.validateString(domiciliation.getPostalCode(), false, 10, "PostalCode");
                        validationHelper.validateString(domiciliation.getCedexComplement(), false, 20,
                                        "CedexComplement");
                        validationHelper.validateString(domiciliation.getMailRecipient(), false, 60, "MailRecipient");
                        validationHelper.validateString(domiciliation.getActivityAddress(), false, 60,
                                        "ActivityAddress");
                        validationHelper.validateReferential(domiciliation.getActivityCity(), false, "ActivityCity");
                        validationHelper.validateString(domiciliation.getActivityPostalCode(), false, 10,
                                        "ActivityPostalCode");
                        validationHelper.validateString(domiciliation.getAcitivityCedexComplement(), false, 20,
                                        "ActivityCedexComplement");
                        validationHelper.validateReferential(domiciliation.getCity(), false, "City");
                        validationHelper.validateReferential(domiciliation.getCountry(), false, "Country");
                        validationHelper.validateString(domiciliation.getAccountingRecordDomiciliation(),
                                        false, 60,
                                        "AccountingRecordDomiciliation");

                        if (domiciliation.isLegalPerson()) {
                                if ((domiciliation.getLegalGardianSiren() == null
                                                || !validationHelper
                                                                .validateSiren(domiciliation.getLegalGardianSiren())))
                                        throw new OsirisValidationException("LegalGardianSiren");
                                validationHelper.validateString(domiciliation.getLegalGardianDenomination(),
                                                false, 60,
                                                "LegalGardianDenomination");
                                validationHelper.validateReferential(domiciliation.getLegalGardianLegalForm(),
                                                false,
                                                "LegalGardianLegalForm");
                        } else {
                                validationHelper.validateReferential(domiciliation.getLegalGardianCivility(),
                                                false,
                                                "LegalGardianCivility");
                                validationHelper.validateString(domiciliation.getLegalGardianFirstname(),
                                                false, 20,
                                                "LegalGardianFirstname");
                                validationHelper.validateString(domiciliation.getLegalGardianLastname(),
                                                false, 20,
                                                "LegalGardianLastname");
                                validationHelper.validateDateMax(domiciliation.getLegalGardianBirthdate(),
                                                false,
                                                LocalDate.now(),
                                                "LegalGardianBirthdate");
                                validationHelper.validateString(domiciliation.getLegalGardianPlaceOfBirth(),
                                                false, 60,
                                                "LegalGardianPlaceOfBirth");
                                validationHelper.validateString(domiciliation.getLegalGardianJob(), false, 30,
                                                "LegalGardianJob");
                        }

                        validationHelper.validateString(domiciliation.getLegalGardianMailRecipient(), false,
                                        60,
                                        "LegalGardianMailRecipient");
                        validationHelper.validateString(domiciliation.getLegalGardianAddress(), false, 60,
                                        "LegalGardianAddress");
                        if (domiciliation.getCountry() != null && domiciliation.getCountry().getCode().equals("FR"))
                                validationHelper.validateString(domiciliation.getLegalGardianPostalCode(),
                                                false, 10,
                                                "LegalGardianPostalCode");
                        validationHelper.validateString(domiciliation.getLegalGardianCedexComplement(), false, 20,
                                        "LegalGardianCedexComplement");
                        validationHelper.validateReferential(domiciliation.getLegalGardianCity(), false,
                                        "LegalGardianCity");
                        validationHelper.validateReferential(domiciliation.getLegalGardianCountry(), false,
                                        "LegalGardianCountry");

                }
                // Announcement
                if (provision.getAnnouncement() != null) {
                        Announcement announcement = provision.getAnnouncement();
                        Announcement currentAnnouncement = null;
                        if (announcement.getId() != null) {
                                currentAnnouncement = announcementService.getAnnouncement(announcement.getId());
                        } else if (!quotation.getCustomerOrderOrigin().getId()
                                        .equals(constantService.getCustomerOrderOriginWebSite().getId())) {
                                // By default, if not from webstite, always redacted by JSS if option exists
                                ProvisionType provisionType = provisionTypeService
                                                .getProvisionType(provision.getProvisionType().getId());
                                for (BillingType billingType : provisionType.getBillingTypes()) {
                                        if (billingType.getId()
                                                        .equals(constantService.getBillingTypeRedactedByJss().getId()))
                                                provision.setIsRedactedByJss(true);
                                }
                        }

                        LocalDate publicationDateVerification = LocalDate.now();
                        // Do not verify date when quotation has started
                        if (isCustomerOrder) {
                                CustomerOrderStatus status = ((CustomerOrder) quotation).getCustomerOrderStatus();
                                if (status.getCode().equals(CustomerOrderStatus.OPEN)
                                                || status.getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)
                                                || status.getCode().equals(CustomerOrderStatus.ABANDONED))
                                        publicationDateVerification = null;
                                else {
                                        if (currentAnnouncement != null)
                                                if (currentAnnouncement.getConfrere() != null
                                                                && currentAnnouncement.getConfrere().getId()
                                                                                .equals(constantService
                                                                                                .getConfrereJssSpel()
                                                                                                .getId())) {
                                                        if (currentAnnouncement.getAnnouncementStatus().getId()
                                                                        .equals(announcement.getAnnouncementStatus()
                                                                                        .getId())
                                                                        && announcement.getAnnouncementStatus()
                                                                                        .getCode()
                                                                                        .equals(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED)
                                                                        || announcement.getAnnouncementStatus()
                                                                                        .getCode()
                                                                                        .equals(AnnouncementStatus.ANNOUNCEMENT_DONE)) {
                                                                publicationDateVerification = null;
                                                                announcement.setPublicationDate(currentAnnouncement
                                                                                .getPublicationDate());
                                                        }
                                                } else {
                                                        // if not jss, no verification
                                                        publicationDateVerification = null;
                                                }
                                }
                        }

                        boolean verifyAnnouncement = isCustomerOrder && targetStatusCode != null
                                        && (targetStatusCode.equals(CustomerOrderStatus.TO_BILLED)
                                                        || targetStatusCode.equals(CustomerOrderStatus.BILLED));

                        validationHelper.validateDateMin(announcement.getPublicationDate(), verifyAnnouncement,
                                        publicationDateVerification,
                                        "Date de publication de l'annonce");
                        validationHelper.validateReferential(announcement.getDepartment(), verifyAnnouncement,
                                        "Department");
                        validationHelper.validateReferential(announcement.getConfrere(), verifyAnnouncement,
                                        "Confrere");
                        validationHelper.validateReferential(announcement.getNoticeTypeFamily(), verifyAnnouncement,
                                        "NoticeTypeFamily");
                        if (verifyAnnouncement && (announcement.getNoticeTypes() == null
                                        || announcement.getNoticeTypes().size() == 0))
                                throw new OsirisValidationException("NoticeTypes");

                        if (announcement.getNoticeTypes() != null)
                                for (NoticeType noticeType : announcement.getNoticeTypes()) {
                                        validationHelper.validateReferential(noticeType, verifyAnnouncement,
                                                        "noticeType");
                                }
                        validationHelper.validateString(announcement.getNotice(), verifyAnnouncement, "Notice");

                        if (announcement.getAnnouncementStatus() != null && (announcement.getAnnouncementStatus()
                                        .getCode()
                                        .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED))
                                        && announcement.getConfrere() != null
                                        && !announcement.getConfrere().getId()
                                                        .equals(constantService.getConfrereJssSpel().getId())) {
                                boolean publicationProofFound = false;
                                if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                                        for (Attachment attachment : provision.getAttachments())
                                                if (attachment.getAttachmentType().getId()
                                                                .equals(constantService
                                                                                .getAttachmentTypePublicationFlag()
                                                                                .getId())
                                                                || attachment.getAttachmentType().getId()
                                                                                .equals(constantService
                                                                                                .getAttachmentTypePublicationReceipt()
                                                                                                .getId()))
                                                        publicationProofFound = true;
                                if (!publicationProofFound)
                                        throw new OsirisValidationException(
                                                        "Le témoin de publication ou le justificatif de parution est obligatoire");
                        }

                        // If published to Actu Legale, to late ...
                        if (currentAnnouncement != null && announcement.getActuLegaleId() != null) {
                                // keep current notice
                                announcement.setNotice(currentAnnouncement.getNotice());
                                announcement.setNoticeHeader(currentAnnouncement.getNoticeHeader());
                                announcement.setPublicationDate(currentAnnouncement.getPublicationDate());
                                announcement.setConfrere(currentAnnouncement.getConfrere());
                                announcement.setDepartment(currentAnnouncement.getDepartment());
                                announcement.setNoticeTypeFamily(currentAnnouncement.getNoticeTypeFamily());
                        }

                }

                // Formalite
                if (provision.getFormalite() != null) {
                        Formalite formalite = provision.getFormalite();
                        validationHelper.validateReferential(formalite.getWaitedCompetentAuthority(), false,
                                        "WaitedCompetentAuthority");
                        validationHelper.validateReferential(formalite.getCompetentAuthorityServiceProvider(), false,
                                        "competentAuthorityServiceProvider");
                }

                // Simple provision
                if (provision.getSimpleProvision() != null) {
                        SimpleProvision simpleProvision = provision.getSimpleProvision();
                        validationHelper.validateReferential(simpleProvision.getWaitedCompetentAuthority(), false,
                                        "WaitedCompetentAuthority");
                }

        }
}
