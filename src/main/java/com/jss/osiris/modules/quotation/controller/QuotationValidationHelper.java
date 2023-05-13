package com.jss.osiris.modules.quotation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IDocument;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.BodaccFusion;
import com.jss.osiris.modules.quotation.model.BodaccFusionAbsorbedCompany;
import com.jss.osiris.modules.quotation.model.BodaccFusionMergingCompany;
import com.jss.osiris.modules.quotation.model.BodaccSale;
import com.jss.osiris.modules.quotation.model.BodaccSplit;
import com.jss.osiris.modules.quotation.model.BodaccSplitBeneficiary;
import com.jss.osiris.modules.quotation.model.BodaccSplitCompany;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
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

        @Transactional
        public void validateQuotationAndCustomerOrder(IQuotation quotation, String targetStatusCode)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                boolean isOpen = false;

                if (quotation.getIsCreatedFromWebSite() == null)
                        quotation.setIsCreatedFromWebSite(false);

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

                // If from website, grab special offer from tiers / responsable / confrere
                if (quotation.getIsCreatedFromWebSite()
                                && (quotation.getSpecialOffers() == null || quotation.getSpecialOffers().size() == 0)) {
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
                if (isOpen && quotation.getIsCreatedFromWebSite()
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
                if (quotation.getAssoAffaireOrders().get(0).getProvisions() == null
                                || quotation.getAssoAffaireOrders().get(0).getProvisions().size() == 0)
                        throw new OsirisValidationException("No provision");

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
                        if (assoAffaireOrder.getProvisions() != null && assoAffaireOrder.getProvisions().size() > 0)
                                for (Provision provision : assoAffaireOrder.getProvisions()) {
                                        validateProvision(provision, isOpen, isCustomerOrder, quotation);
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
        public void validateProvisionTransactionnal(Provision provision, boolean isOpen, boolean isCustomerOrder,
                        IQuotation quotation)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {
                validateProvision(provision, isOpen, isCustomerOrder, quotation);
        }

        private void validateProvision(Provision provision, boolean isOpen, boolean isCustomerOrder,
                        IQuotation quotation)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException {

                validationHelper.validateReferential(provision.getProvisionFamilyType(), true, "Famille de prestation");
                validationHelper.validateReferential(provision.getProvisionType(), true, "Type de prestation");

                isCustomerOrder = isCustomerOrder && !isOpen;

                if (provision.getDebours() != null && provision.getDebours().size() > 0)
                        for (Debour debour : provision.getDebours()) {
                                validationHelper.validateReferential(debour.getBillingType(), true, "billingType");
                                validationHelper.validateReferential(debour.getCompetentAuthority(), true,
                                                "competentAuthority");
                                validationHelper.validateReferential(debour.getPaymentType(), true, "paymentType");
                                validationHelper.validateString(debour.getComments(), false, 250, "comments");
                                validationHelper.validateFloat(debour.getDebourAmount(), true, "debourAmount");
                                validationHelper.validateFloat(debour.getInvoicedAmount(),
                                                debour.getBillingType().getIsFee(),
                                                "invoicedAmount");

                                if (debour.getBillingType().getIsDebour())
                                        debour.setInvoicedAmount(Math.min(debour.getDebourAmount(),
                                                        debour.getInvoicedAmount() != null ? debour.getInvoicedAmount()
                                                                        : debour.getDebourAmount()));

                                // check debour payment type
                                if (debour.getId() == null && debour.getCompetentAuthority().getPaymentTypes() != null
                                                && debour.getCompetentAuthority().getPaymentTypes().size() > 0) {
                                        boolean found = false;
                                        for (PaymentType paymentType : debour.getCompetentAuthority().getPaymentTypes())
                                                if (paymentType.getId().equals(debour.getPaymentType().getId()))
                                                        found = true;
                                        if (!found)
                                                throw new OsirisClientMessageException(
                                                                "Type de paiement non autorisé pour l'autorité compétente");
                                }

                                if (debour.getPaymentDateTime() == null)
                                        debour.setPaymentDateTime(LocalDateTime.now());
                                validationHelper.validateDateMax(debour.getPaymentDateTime().toLocalDate(), true,
                                                LocalDateTime.now().toLocalDate(),
                                                "paymentDateTime");

                                if (debour.getId() != null) {
                                        // If already saved, can't change anything except invoicedAmount
                                        Float invoicedAmount = debour.getInvoicedAmount();
                                        debour = (Debour) validationHelper.validateReferential(debour, true, "debour");
                                        debour.setInvoicedAmount(invoicedAmount);
                                }
                        }

                // Check deleted debours
                if (provision.getId() != null) {
                        Provision currentProvision = provisionService.getProvision(provision.getId());
                        if (currentProvision.getDebours() != null && currentProvision.getDebours().size() > 0) {
                                for (Debour debour : currentProvision.getDebours()) {
                                        boolean isDeleted = true;
                                        if (provision.getDebours() != null && provision.getDebours().size() > 0)
                                                for (Debour newDebour : provision.getDebours())
                                                        if (newDebour.getId() != null
                                                                        && newDebour.getId().equals(debour.getId()))
                                                                isDeleted = false;

                                        if (isDeleted && (debour.getPaymentType().getId()
                                                        .equals(constantService.getPaymentTypeEspeces().getId())
                                                        || debour.getPaymentType().getId()
                                                                        .equals(constantService.getPaymentTypeCheques()
                                                                                        .getId())
                                                        || debour.getInvoiceItem() != null
                                                        || debour.getPayment() != null
                                                        || debour.getIsAssociated() != null
                                                                        && debour.getIsAssociated() == true))
                                                throw new OsirisClientMessageException(
                                                                "Impossible de supprimer ce débours, merci de contacter l'administrateur pour cela");
                                }
                        }
                }

                // Domiciliation
                if (provision.getDomiciliation() != null) {
                        Domiciliation domiciliation = provision.getDomiciliation();
                        validationHelper.validateReferential(domiciliation.getDomiciliationContractType(), !isOpen,
                                        "DomiciliationContractType");
                        validationHelper.validateReferential(domiciliation.getLanguage(), !isOpen, "Language");
                        validationHelper.validateReferential(domiciliation.getBuildingDomiciliation(), !isOpen,
                                        "BuildingDomiciliation");
                        validationHelper.validateReferential(domiciliation.getMailRedirectionType(), !isOpen,
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
                                        isCustomerOrder, 60,
                                        "AccountingRecordDomiciliation");

                        if (domiciliation.isLegalPerson()) {
                                if ((domiciliation.getLegalGardianSiren() == null
                                                || !validationHelper
                                                                .validateSiren(domiciliation.getLegalGardianSiren())))
                                        throw new OsirisValidationException("LegalGardianSiren");
                                validationHelper.validateString(domiciliation.getLegalGardianDenomination(),
                                                isCustomerOrder, 60,
                                                "LegalGardianDenomination");
                                validationHelper.validateReferential(domiciliation.getLegalGardianLegalForm(),
                                                isCustomerOrder,
                                                "LegalGardianLegalForm");
                        } else {
                                validationHelper.validateReferential(domiciliation.getLegalGardianCivility(),
                                                isCustomerOrder,
                                                "LegalGardianCivility");
                                validationHelper.validateString(domiciliation.getLegalGardianFirstname(),
                                                isCustomerOrder, 20,
                                                "LegalGardianFirstname");
                                validationHelper.validateString(domiciliation.getLegalGardianLastname(),
                                                isCustomerOrder, 20,
                                                "LegalGardianLastname");
                                validationHelper.validateDateMax(domiciliation.getLegalGardianBirthdate(),
                                                isCustomerOrder,
                                                LocalDate.now(),
                                                "LegalGardianBirthdate");
                                validationHelper.validateString(domiciliation.getLegalGardianPlaceOfBirth(),
                                                isCustomerOrder, 60,
                                                "LegalGardianPlaceOfBirth");
                                validationHelper.validateString(domiciliation.getLegalGardianJob(), isCustomerOrder, 30,
                                                "LegalGardianJob");
                        }

                        validationHelper.validateString(domiciliation.getLegalGardianMailRecipient(), isCustomerOrder,
                                        60,
                                        "LegalGardianMailRecipient");
                        validationHelper.validateString(domiciliation.getLegalGardianAddress(), isCustomerOrder, 60,
                                        "LegalGardianAddress");
                        if (domiciliation.getCountry() != null && domiciliation.getCountry().getCode().equals("FR"))
                                validationHelper.validateString(domiciliation.getLegalGardianPostalCode(),
                                                isCustomerOrder, 10,
                                                "LegalGardianPostalCode");
                        validationHelper.validateString(domiciliation.getLegalGardianCedexComplement(), false, 20,
                                        "LegalGardianCedexComplement");
                        validationHelper.validateReferential(domiciliation.getLegalGardianCity(), isCustomerOrder,
                                        "LegalGardianCity");
                        validationHelper.validateReferential(domiciliation.getLegalGardianCountry(), isCustomerOrder,
                                        "LegalGardianCountry");

                }
                // Announcement
                if (provision.getAnnouncement() != null) {
                        Announcement announcement = provision.getAnnouncement();
                        Announcement currentAnnouncement = null;
                        if (announcement.getId() != null) {
                                currentAnnouncement = announcementService.getAnnouncement(announcement.getId());
                        } else {
                                // By default, always redacted by JSS if option exists
                                for (BillingType billingType : provision.getProvisionType().getBillingTypes()) {
                                        if (billingType.getId()
                                                        .equals(constantService.getBillingTypeRedactedByJss().getId()))
                                                provision.setIsRedactedByJss(true);
                                }
                        }

                        LocalDate publicationDateVerification = LocalDate.now().minusDays(1);
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

                        validationHelper.validateDateMin(announcement.getPublicationDate(), true,
                                        publicationDateVerification,
                                        "Date de publication de l'annonce");
                        validationHelper.validateReferential(announcement.getDepartment(), !isOpen, "Department");
                        validationHelper.validateReferential(announcement.getConfrere(), isCustomerOrder, "Confrere");
                        validationHelper.validateReferential(announcement.getNoticeTypeFamily(), isCustomerOrder,
                                        "NoticeTypeFamily");
                        if (isCustomerOrder && (announcement.getNoticeTypes() == null
                                        || announcement.getNoticeTypes().size() == 0))
                                throw new OsirisValidationException("NoticeTypes");

                        if (announcement.getNoticeTypes() != null)
                                for (NoticeType noticeType : announcement.getNoticeTypes()) {
                                        validationHelper.validateReferential(noticeType, isCustomerOrder, "noticeType");
                                }
                        validationHelper.validateString(announcement.getNotice(), !isOpen, "Notice");

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

                // BODACC
                if (provision.getBodacc() != null) {
                        Bodacc bodacc = provision.getBodacc();
                        validationHelper.validateReferential(bodacc.getPaymentType(), false, "PaymentType");
                        validationHelper.validateReferential(bodacc.getBodaccPublicationType(), !isOpen,
                                        "BodaccPublicationType");
                        validationHelper.validateReferential(bodacc.getTransfertFundsType(), false,
                                        "TransfertFundsType");

                        if (bodacc.getBodaccSale() != null) {
                                BodaccSale bodaccSale = bodacc.getBodaccSale();

                                validationHelper.validateString(bodaccSale.getDivestedBusinessAddress(), false, 100,
                                                "DivestedBusinessAddress");
                                validationHelper.validateReferential(bodaccSale.getFundType(), isCustomerOrder,
                                                "FundType");
                                validationHelper.validateString(bodaccSale.getOwnerFirstname(), false, 30,
                                                "OwnerFirstname");
                                validationHelper.validateString(bodaccSale.getOwnerLastname(), false, 30,
                                                "OwnerLastname");
                                validationHelper.validateString(bodaccSale.getOwnerDenomination(), false, 60,
                                                "OwnerDenomination");
                                validationHelper.validateString(bodaccSale.getOwnerSiren(), false, 9, "OwnerSiren");
                                validationHelper.validateSiren(bodaccSale.getOwnerSiren());
                                validationHelper.validateString(bodaccSale.getOwnerAddress(), isCustomerOrder, 100,
                                                "OwnerAddress");
                                validationHelper.validateString(bodaccSale.getOwnerAbbreviation(), false, 20,
                                                "OwnerAbbreviation");
                                validationHelper.validateString(bodaccSale.getOwnerBusinessName(), false, 60,
                                                "OwnerBusinessName");
                                validationHelper.validateReferential(bodaccSale.getOwnerLegalForm(), false,
                                                "OwnerLegalForm");
                                validationHelper.validateString(bodaccSale.getPurchaserFirstname(), false, 30,
                                                "PurchaserFirstname");
                                validationHelper.validateString(bodaccSale.getPurchaserLastname(), false, 30,
                                                "PurchaserLastname");
                                validationHelper.validateString(bodaccSale.getPurchaserDenomination(), false, 60,
                                                "PurchaserDenomination");
                                validationHelper.validateString(bodaccSale.getPurchaserSiren(), false, 9,
                                                "PurchaserSiren");
                                validationHelper.validateSiren(bodaccSale.getPurchaserSiren());
                                validationHelper.validateString(bodaccSale.getPurchaserBusinessName(), false, 60,
                                                "PurchaserBusinessName");
                                validationHelper.validateString(bodaccSale.getPurchaserAbbreviation(), false, 20,
                                                "PurchaserAbbreviation");
                                validationHelper.validateReferential(bodaccSale.getPurchaserLegalForm(), false,
                                                "PurchaserLegalForm");
                                validationHelper.validateDate(bodaccSale.getPurchaserActivityStartDate(),
                                                isCustomerOrder,
                                                "PurchaserActivityStartDate");
                                validationHelper.validateDate(bodaccSale.getDeedDate(), isCustomerOrder, "DeedDate");
                                validationHelper.validateDate(bodaccSale.getRegistrationDate(), isCustomerOrder,
                                                "RegistrationDate");
                                validationHelper.validateReferential(bodaccSale.getRegistrationAuthority(),
                                                isCustomerOrder,
                                                "RegistrationAuthority");
                                validationHelper.validateString(bodaccSale.getRegistrationReferences(), false, 50,
                                                "RegistrationReferences");
                                validationHelper.validateReferential(bodaccSale.getActType(), isCustomerOrder,
                                                "ActType");
                                validationHelper.validateString(bodaccSale.getWritor(), false, 60, "Writor");
                                validationHelper.validateString(bodaccSale.getWritorAddress(), false, 100,
                                                "WritorAddress");
                                validationHelper.validateString(bodaccSale.getValidityObjectionAddress(),
                                                isCustomerOrder, 100,
                                                "ValidityObjectionAddress");
                                validationHelper.validateString(bodaccSale.getMailObjectionAddress(), false, 100,
                                                "MailObjectionAddress");
                                validationHelper.validateDate(bodaccSale.getLeaseResilisationDate(),
                                                isCustomerOrder
                                                                && constantService.getTransfertFundsTypeBail().getId()
                                                                                .equals(bodacc.getTransfertFundsType()
                                                                                                .getId()),
                                                "LeaseResilisationDate");
                                validationHelper.validateString(bodaccSale.getLeaseAddress(), false, 100,
                                                "LeaseAddress");
                                validationHelper.validateString(bodaccSale.getTenantFirstname(), false, 30,
                                                "TenantFirstname");
                                validationHelper.validateString(bodaccSale.getTenantLastname(), false, 30,
                                                "TenantLastname");
                                validationHelper.validateString(bodaccSale.getTenantAddress(), isCustomerOrder, 100,
                                                "TenantAddress");
                                validationHelper.validateString(bodaccSale.getTenantDenomination(), false, 60,
                                                "TenantDenomination");
                                validationHelper.validateString(bodaccSale.getTenantSiren(), false, 9, "TenantSiren");
                                validationHelper.validateSiren(bodaccSale.getTenantSiren());
                                validationHelper.validateString(bodaccSale.getTenantBusinessName(), false, 60,
                                                "TenantBusinessName");
                                validationHelper.validateString(bodaccSale.getTenantAbbreviation(), false, 20,
                                                "TenantAbbreviation");
                                validationHelper.validateReferential(bodaccSale.getTenantLegalForm(), false,
                                                "TenantLegalForm");

                        }

                        if (bodacc.getBodaccFusion() != null) {
                                BodaccFusion bodaccFusion = bodacc.getBodaccFusion();

                                if (bodaccFusion.getBodaccFusionAbsorbedCompanies() == null
                                                || bodaccFusion.getBodaccFusionAbsorbedCompanies().size() == 0)
                                        throw new OsirisValidationException("BodaccFusionAbsorbedCompanies");

                                for (BodaccFusionAbsorbedCompany bodaccFusionAbsorbedCompany : bodaccFusion
                                                .getBodaccFusionAbsorbedCompanies()) {
                                        validationHelper.validateString(
                                                        bodaccFusionAbsorbedCompany.getAbsorbedCompanyDenomination(),
                                                        isCustomerOrder,
                                                        60, "AbsorbedCompanyDenomination");
                                        validationHelper.validateString(
                                                        bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren(),
                                                        isCustomerOrder, 9,
                                                        "AbsorbedCompanySiren");
                                        validationHelper.validateSiren(
                                                        bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren());
                                        validationHelper.validateString(
                                                        bodaccFusionAbsorbedCompany.getAbsorbedCompanyAddress(),
                                                        isCustomerOrder, 100,
                                                        "AbsorbedCompanyAddress");
                                        validationHelper.validateReferential(
                                                        bodaccFusionAbsorbedCompany.getAbsorbedCompanyLegalForm(),
                                                        false,
                                                        "AbsorbedCompanyLegalForm");
                                        validationHelper.validateDate(
                                                        bodaccFusionAbsorbedCompany
                                                                        .getAbsorbedCompanyRcsDeclarationDate(),
                                                        isCustomerOrder, "AbsorbedCompanyRcsDeclarationDate");
                                        validationHelper.validateReferential(
                                                        bodaccFusionAbsorbedCompany
                                                                        .getAbsorbedCompanyRcsCompetentAuthority(),
                                                        isCustomerOrder, "AbsorbedCompanyRcsCompetentAuthority");
                                }

                                if (bodaccFusion.getBodaccFusionMergingCompanies() == null
                                                || bodaccFusion.getBodaccFusionMergingCompanies().size() == 0)
                                        throw new OsirisValidationException("BodaccFusionMergingCompanies");

                                for (BodaccFusionMergingCompany bodaccFusionMergingCompany : bodaccFusion
                                                .getBodaccFusionMergingCompanies()) {
                                        validationHelper.validateString(
                                                        bodaccFusionMergingCompany.getMergingCompanyDenomination(),
                                                        isCustomerOrder,
                                                        60, "MergingCompanyDenomination");
                                        validationHelper.validateString(
                                                        bodaccFusionMergingCompany.getMergingCompanySiren(),
                                                        isCustomerOrder, 9,
                                                        "MergingCompanySiren");
                                        validationHelper.validateSiren(
                                                        bodaccFusionMergingCompany.getMergingCompanySiren());
                                        validationHelper.validateString(
                                                        bodaccFusionMergingCompany.getMergingCompanyAddress(),
                                                        isCustomerOrder, 100,
                                                        "MergingCompanyAddress");
                                        validationHelper.validateReferential(
                                                        bodaccFusionMergingCompany.getMergingCompanyLegalForm(), false,
                                                        "MergingCompanyLegalForm");
                                        validationHelper.validateDate(
                                                        bodaccFusionMergingCompany
                                                                        .getMergingCompanyRcsDeclarationDate(),
                                                        isCustomerOrder, "MergingCompanyRcsDeclarationDate");
                                        validationHelper.validateReferential(
                                                        bodaccFusionMergingCompany
                                                                        .getMergingCompanyRcsCompetentAuthority(),
                                                        isCustomerOrder, "MergingCompanyRcsCompetentAuthority");
                                }
                        }

                        if (bodacc.getBodaccSplit() != null) {
                                BodaccSplit bodaccSplit = bodacc.getBodaccSplit();

                                if (bodaccSplit.getBodaccSplitBeneficiaries() == null
                                                || bodaccSplit.getBodaccSplitBeneficiaries().size() == 0)
                                        throw new OsirisValidationException("BodaccSplitBeneficiaries");

                                for (BodaccSplitBeneficiary bodaccSplitBeneficiary : bodaccSplit
                                                .getBodaccSplitBeneficiaries()) {
                                        validationHelper.validateString(
                                                        bodaccSplitBeneficiary.getBeneficiaryCompanyDenomination(),
                                                        isCustomerOrder,
                                                        60, "BeneficiaryCompanyDenomination");
                                        validationHelper.validateString(
                                                        bodaccSplitBeneficiary.getBeneficiaryCompanySiren(),
                                                        isCustomerOrder, 9,
                                                        "BeneficiaryCompanySiren");
                                        validationHelper.validateSiren(
                                                        bodaccSplitBeneficiary.getBeneficiaryCompanySiren());
                                        validationHelper.validateString(
                                                        bodaccSplitBeneficiary.getBeneficiaryCompanyAddress(),
                                                        isCustomerOrder, 100,
                                                        "BeneficiaryCompanyAddress");
                                        validationHelper.validateReferential(
                                                        bodaccSplitBeneficiary.getBeneficiaryCompanyLegalForm(), false,
                                                        "BeneficiaryCompanyLegalForm");
                                        validationHelper.validateDate(
                                                        bodaccSplitBeneficiary
                                                                        .getBeneficiaryCompanyRcsDeclarationDate(),
                                                        isCustomerOrder, "BeneficiaryCompanyRcsDeclarationDate");
                                        validationHelper.validateReferential(
                                                        bodaccSplitBeneficiary
                                                                        .getBeneficiaryCompanyRcsCompetentAuthority(),
                                                        true, "BeneficiaryCompanyRcsCompetentAuthority");
                                }

                                if (bodaccSplit.getBodaccSplitCompanies() == null
                                                || bodaccSplit.getBodaccSplitCompanies().size() == 0)
                                        throw new OsirisValidationException("BodaccSplitCompanies");
                                for (BodaccSplitCompany bodaccSplitCompany : bodaccSplit.getBodaccSplitCompanies()) {
                                        validationHelper.validateString(
                                                        bodaccSplitCompany.getSplitCompanyDenomination(),
                                                        isCustomerOrder,
                                                        60,
                                                        "SplitCompanyDenomination");
                                        validationHelper.validateString(bodaccSplitCompany.getSplitCompanySiren(),
                                                        isCustomerOrder, 9,
                                                        "SplitCompanySiren");
                                        validationHelper.validateSiren(bodaccSplitCompany.getSplitCompanySiren());
                                        validationHelper.validateString(bodaccSplitCompany.getSplitCompanyAddress(),
                                                        isCustomerOrder, 100,
                                                        "SplitCompanyAddress");
                                        validationHelper.validateReferential(
                                                        bodaccSplitCompany.getSplitCompanyLegalForm(), false,
                                                        "SplitCompanyLegalForm");
                                        validationHelper.validateDate(
                                                        bodaccSplitCompany.getSplitCompanyRcsDeclarationDate(),
                                                        isCustomerOrder,
                                                        "SplitCompanyRcsDeclarationDate");
                                        validationHelper.validateReferential(
                                                        bodaccSplitCompany.getSplitCompanyRcsCompetentAuthority(),
                                                        isCustomerOrder, "SplitCompanyRcsCompetentAuthority");
                                }
                        }

                        validationHelper.validateDateMin(bodacc.getDateOfPublication(), false, LocalDate.now(),
                                        "DateOfPublication");
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
