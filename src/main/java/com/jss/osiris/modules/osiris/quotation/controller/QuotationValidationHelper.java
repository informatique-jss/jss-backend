package com.jss.osiris.modules.osiris.quotation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Domiciliation;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionTypeService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

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

        public void validateAffaire(Affaire affaire) throws OsirisValidationException, OsirisException {
                validationHelper.validateReferential(affaire.getCountry(), true, "Country");
                validationHelper.validateReferential(affaire.getMainActivity(), false, "MainActivity");
                validationHelper.validateReferential(affaire.getLegalForm(), false, "LegalForm");
                validationHelper.validateReferential(affaire.getCompetentAuthority(), false, "CompetentAuthority");
                validationHelper.validateString(affaire.getExternalReference(), false, 60, "ExternalReference");
                validationHelper.validateString(affaire.getIntercommunityVat(), false, 20, "IntercommunityVat");
                if (affaire.getCountry() != null
                                && affaire.getCountry().getId().equals(constantService.getCountryFrance().getId()))
                        validationHelper.validateString(affaire.getPostalCode(), true, 10, "PostalCode");
                validationHelper.validateString(affaire.getCedexComplement(), false, 20, "CedexComplement");
                validationHelper.validateString(affaire.getAddress(), true, 100, "Address");
                validationHelper.validateReferential(affaire.getCity(), true, "City");

                if (affaire.getIsIndividual()) {
                        validationHelper.validateReferential(affaire.getCivility(), true, "Civility");
                        validationHelper.validateString(affaire.getFirstname(), true, 40, "Firstname");
                        validationHelper.validateString(affaire.getLastname(), true, 40, "Lastname");
                        affaire.setDenomination(null);
                        if (affaire.getLastname() != null)
                                affaire.setLastname(affaire.getLastname().toUpperCase());

                } else {
                        validationHelper.validateString(affaire.getDenomination(), true, 150, "Denomination");
                        affaire.setFirstname(null);
                        affaire.setLastname(null);
                        if (affaire.getRna() != null
                                        && !validationHelper.validateRna(
                                                        affaire.getRna().toUpperCase().replaceAll(" ", "")))
                                throw new OsirisValidationException("RNA");
                        if (affaire.getSiren() != null
                                        && !validationHelper.validateSiren(
                                                        affaire.getSiren().toUpperCase().replaceAll(" ", "")))
                                throw new OsirisValidationException("SIREN");
                        if (affaire.getSiret() != null
                                        && !validationHelper.validateSiret(
                                                        affaire.getSiret().toUpperCase().replaceAll(" ", "")))
                                throw new OsirisValidationException("SIRET");
                }
        }

        @Transactional(rollbackFor = Exception.class)
        public void validateQuotationAndCustomerOrder(IQuotation quotation, String targetStatusCode)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                boolean isOpen = false;

                if (targetStatusCode != null) {
                        // Is for status update, override input quotation with database one
                        if (!quotation.getIsQuotation())
                                quotation = customerOrderService.getCustomerOrder(quotation.getId());
                        if (quotation.getIsQuotation())
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
                                                        .equals(CustomerOrderStatus.DRAFT);

                        if (targetStatusCode != null)
                                isOpen = targetStatusCode.equals(CustomerOrderStatus.DRAFT);
                }

                if (quotation instanceof Quotation) {
                        Quotation quotationQuotation = (Quotation) quotation;
                        isOpen = quotationQuotation.getQuotationStatus() == null ||
                                        quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.DRAFT);
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

                quotation.setResponsable(
                                (Responsable) validationHelper.validateReferential(quotation.getResponsable(), false,
                                                "Responsable"));

                // If new or if from old website, grab special offer from tiers / responsable /
                // confrere
                if (quotation.getCustomerOrderOrigin().getId()
                                .equals(constantService.getCustomerOrderOriginOldWebSite().getId())
                                && (quotation.getSpecialOffers() == null || quotation.getSpecialOffers().size() == 0)
                                || quotation.getId() == null) {
                        Tiers tiers = quotation.getResponsable().getTiers();

                        List<SpecialOffer> specialOffers = null;
                        if (tiers.getSpecialOffers() != null && tiers.getSpecialOffers().size() > 0) {
                                specialOffers = tiers.getSpecialOffers();
                                quotation.setSpecialOffers(new ArrayList<SpecialOffer>());
                                for (SpecialOffer specialOffer : specialOffers)
                                        quotation.getSpecialOffers().add(specialOffer);
                        }
                }

                if (quotation.getResponsable() == null)
                        throw new OsirisValidationException("No customer order");

                if (quotation.getResponsable() != null && !quotation.getResponsable().getIsActive())
                        throw new OsirisClientMessageException(
                                        "Il n'est pas possible d'utiliser un responsable inactif !");

                // Generate missing documents
                Document billingDocument = documentService
                                .getBillingDocument(quotation.getResponsable().getDocuments());
                if (documentService.getBillingDocument(quotation.getDocuments()) == null && billingDocument != null) {
                        billingDocument = documentService.cloneOrMergeDocument(billingDocument, null);
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

                Document digitalDocument = documentService.getDocumentByDocumentType(
                                quotation.getResponsable().getDocuments(),
                                constantService.getDocumentTypeDigital());
                if (documentService.getDocumentByDocumentType(quotation.getDocuments(),
                                constantService.getDocumentTypeDigital()) == null && digitalDocument != null) {
                        digitalDocument = documentService.cloneOrMergeDocument(digitalDocument, null);
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

                Document paperDocument = documentService.getDocumentByDocumentType(
                                quotation.getResponsable().getDocuments(),
                                constantService.getDocumentTypePaper());
                if (documentService.getDocumentByDocumentType(quotation.getDocuments(),
                                constantService.getDocumentTypePaper()) == null && paperDocument != null) {
                        paperDocument = documentService.cloneOrMergeDocument(paperDocument, null);
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
                                .equals(constantService.getCustomerOrderOriginOldWebSite().getId())
                                && (quotation.getAssoAffaireOrders() == null
                                                || quotation.getAssoAffaireOrders().size() == 0))
                        return;

                // Check customer order is not a prospect
                if (isCustomerOrder && !isOpen) {
                        if (quotation.getResponsable().getTiersType().getId()
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
                                validateDocument(document);
                        }
                }

                CustomerOrderFrequency lastProvisionFrequency = null;
                if (quotation.getAssoAffaireOrders() != null)
                        for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                                if (assoAffaireOrder.getAffaire() == null)
                                        throw new OsirisValidationException("Affaire");
                                assoAffaireOrder
                                                .setAffaire((Affaire) validationHelper.validateReferential(
                                                                assoAffaireOrder.getAffaire(), true,
                                                                "Affaire"));

                                if (assoAffaireOrder.getServices() == null
                                                || assoAffaireOrder.getServices().size() == 0)
                                        throw new OsirisClientMessageException("Au moins un service est nécessaire");

                                for (Service service : assoAffaireOrder.getServices())
                                        if (service.getProvisions() == null
                                                        || service.getProvisions().size() == 0)
                                                throw new OsirisClientMessageException(
                                                                "Chaque service doit avoir au moins une prestation");

                                for (Service service : assoAffaireOrder.getServices())
                                        for (Provision provision : service.getProvisions()) {
                                                validateProvision(provision, isCustomerOrder,
                                                                quotation, false);

                                                // Check unique frequency in all customer order
                                                if (provision.getProvisionType().getIsRecurring() != null
                                                                && provision.getProvisionType().getIsRecurring()
                                                                && provision.getProvisionType()
                                                                                .getRecurringFrequency() != null) {
                                                        if (lastProvisionFrequency != null && !lastProvisionFrequency
                                                                        .getId()
                                                                        .equals(provision.getProvisionType()
                                                                                        .getRecurringFrequency()
                                                                                        .getId()))
                                                                throw new OsirisClientMessageException(
                                                                                "Une seule fréquence de récurence est possible pour l'ensemble des prestations récurrentes de la commande");
                                                        lastProvisionFrequency = provision.getProvisionType()
                                                                        .getRecurringFrequency();
                                                }
                                        }
                        }

                // Check recursivity
                if (isCustomerOrder && ((CustomerOrder) quotation).getIsRecurring() != null
                                && ((CustomerOrder) quotation).getIsRecurring()) {
                        CustomerOrder currentCustomerOrder = (CustomerOrder) quotation;
                        validationHelper.validateDate(currentCustomerOrder.getRecurringPeriodStartDate(), true,
                                        "recurringStartDate");
                        validationHelper.validateDateMin(currentCustomerOrder.getRecurringPeriodEndDate(), true,
                                        currentCustomerOrder.getRecurringPeriodStartDate().plusDays(1),
                                        "recurringEndDate");
                } else if (quotation instanceof CustomerOrder && lastProvisionFrequency != null) {
                        ((CustomerOrder) quotation).setCustomerOrderFrequency(lastProvisionFrequency);
                        ((CustomerOrder) quotation).setIsRecurring(true);
                        ((CustomerOrder) quotation).setRecurringPeriodStartDate(LocalDate.now());
                        ((CustomerOrder) quotation).setRecurringPeriodEndDate(LocalDate.now().plusYears(100));
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

        @Transactional(rollbackFor = Exception.class)
        public Document validateDocument(Document document)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
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
                validationHelper.validateString(document.getCommandNumber(),
                                document.getIsCommandNumberMandatory() != null
                                                && document.getIsCommandNumberMandatory(),
                                40,
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

                return document;
        }

        @Transactional
        public void validateProvisionTransactionnal(Provision provision,
                        IQuotation iQuotation, Boolean isByPassMandatoryFields)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                validateProvision(provision, !iQuotation.getIsQuotation(), iQuotation, isByPassMandatoryFields);
        }

        private void validateProvision(Provision provision, boolean isCustomerOrder,
                        IQuotation quotation, Boolean isByPassMandatoryFields)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {

                validationHelper.validateReferential(provision.getProvisionFamilyType(),
                                !isByPassMandatoryFields || true, "Famille de prestation");
                validationHelper.validateReferential(provision.getProvisionType(), !isByPassMandatoryFields || true,
                                "Type de prestation");

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
                                        false, 600,
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
                                        .equals(constantService.getCustomerOrderOriginOldWebSite().getId())) {
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
                                if (status != null && (status.getCode().equals(CustomerOrderStatus.DRAFT)
                                                || status.getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)
                                                || status.getCode().equals(CustomerOrderStatus.ABANDONED)))
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

                        boolean verifyAnnouncement = isCustomerOrder
                                        && announcement.getAnnouncementStatus() != null
                                        && (announcement.getAnnouncementStatus().getCode().equals(
                                                        AnnouncementStatus.ANNOUNCEMENT_PUBLISHED)
                                                        || announcement.getAnnouncementStatus().getCode().equals(
                                                                        AnnouncementStatus.ANNOUNCEMENT_DONE));

                        if (!isCustomerOrder || ((CustomerOrder) quotation).getCustomerOrderStatus() == null
                                        || ((CustomerOrder) quotation).getCustomerOrderStatus().getCode()
                                                        .equals(CustomerOrderStatus.DRAFT))
                                verifyAnnouncement = false;

                        validationHelper.validateDateMin(announcement.getPublicationDate(),
                                        !isByPassMandatoryFields && verifyAnnouncement,
                                        publicationDateVerification,
                                        "Date de publication de l'annonce");
                        validationHelper.validateReferential(announcement.getDepartment(),
                                        !isByPassMandatoryFields && verifyAnnouncement,
                                        "Department");
                        validationHelper.validateReferential(announcement.getConfrere(),
                                        !isByPassMandatoryFields && verifyAnnouncement,
                                        "Confrere");
                        validationHelper.validateReferential(announcement.getNoticeTypeFamily(),
                                        !isByPassMandatoryFields && verifyAnnouncement,
                                        "NoticeTypeFamily");
                        if ((!isByPassMandatoryFields && verifyAnnouncement) && (announcement.getNoticeTypes() == null
                                        || announcement.getNoticeTypes().size() == 0))
                                throw new OsirisValidationException("NoticeTypes");

                        if (announcement.getNoticeTypes() != null)
                                for (NoticeType noticeType : announcement.getNoticeTypes()) {
                                        validationHelper.validateReferential(noticeType,
                                                        !isByPassMandatoryFields && verifyAnnouncement,
                                                        "noticeType");
                                }
                        validationHelper.validateString(announcement.getNotice(),
                                        !isByPassMandatoryFields && verifyAnnouncement, "Notice");

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
                }

                // Simple provision
                if (provision.getSimpleProvision() != null) {
                        SimpleProvision simpleProvision = provision.getSimpleProvision();
                        validationHelper.validateReferential(simpleProvision.getWaitedCompetentAuthority(), false,
                                        "WaitedCompetentAuthority");
                }

        }
}
