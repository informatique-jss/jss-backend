package com.jss.osiris.modules.myjss.quotation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.TiersValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

import jakarta.servlet.http.HttpServletRequest;

@org.springframework.stereotype.Service
public class MyJssQuotationDelegate {

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersValidationHelper tiersValidationHelper;

    @Autowired
    TiersService tiersService;

    @Autowired
    DocumentService documentService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DocumentTypeService documentTypeService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Autowired
    MailService mailService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    UserScopeService userScopeService;

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder saveCustomerOrderFromMyJss(CustomerOrder order, Boolean isValidation,
            HttpServletRequest request)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (order.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : order.getAssoAffaireOrders())
                if (asso.getAffaire() != null && asso.getAffaire().getId() == null)
                    affaireService.addOrUpdateAffaire(asso.getAffaire());

        saveNewMailsOnAffaire(order);

        Responsable newResponsable = order.getResponsable();

        if (order.getResponsable() != null && order.getResponsable().getId() != null
                && !order.getResponsable().getId().equals(employeeService.getCurrentMyJssUser().getId())) {
            List<Responsable> responsables = userScopeService.getPotentialUserScope();
            Boolean found = false;
            if (responsables != null) {
                for (Responsable responsable : responsables) {
                    if (responsable.getId().equals(order.getResponsable().getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found)
                newResponsable = employeeService.getCurrentMyJssUser();
            else
                newResponsable = responsableService.getResponsable(order.getResponsable().getId());
        } else {
            newResponsable = employeeService.getCurrentMyJssUser();
        }

        CustomerOrder fetchOrder = null;
        if (order.getId() == null) {
            order.setResponsable(newResponsable);
            populateBooleansOfProvisions(order);
            quotationValidationHelper.completeIQuotationDocuments(order, true);
            order.setCustomerOrderOrigin(constantService.getCustomerOrderOriginMyJss());
            order.setCustomerOrderStatus(
                    customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT));
            fetchOrder = customerOrderService.addOrUpdateCustomerOrder(order, true, false);
        } else
            fetchOrder = customerOrderService.getCustomerOrder(order.getId());

        if (fetchOrder.getResponsable() == null
                || !fetchOrder.getResponsable().getId().equals(newResponsable.getId())) {
            fetchOrder.setResponsable(newResponsable);
            fetchOrder = customerOrderService.addOrUpdateCustomerOrder(fetchOrder, true, false);
        }

        if (!fetchOrder.getServiceFamilyGroup().getId().equals(order.getServiceFamilyGroup().getId())) {
            fetchOrder.setServiceFamilyGroup(order.getServiceFamilyGroup());
            fetchOrder = customerOrderService.addOrUpdateCustomerOrder(fetchOrder, true, false);
        }

        if (isValidation != null && isValidation) {
            customerOrderService.addOrUpdateCustomerOrderStatus(fetchOrder, CustomerOrderStatus.BEING_PROCESSED, true);
            if (customerOrderService.isOnlyJssAnnouncement(fetchOrder, true)) {
                quotationValidationHelper.validateQuotationAndCustomerOrder(fetchOrder, null);
                customerOrderService.autoBilledProvisions(fetchOrder);
            }
        }
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public Quotation saveQuotationFromMyJss(Quotation quotation, Boolean isValidation, HttpServletRequest request)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                if (asso.getAffaire() != null && asso.getAffaire().getId() == null)
                    affaireService.addOrUpdateAffaire(asso.getAffaire());

        saveNewMailsOnAffaire(quotation);

        Responsable newResponsable = quotation.getResponsable();

        if (quotation.getResponsable() != null
                && !quotation.getResponsable().getId().equals(employeeService.getCurrentMyJssUser().getId())) {
            List<Responsable> responsables = userScopeService.getPotentialUserScope();
            Boolean found = false;
            if (responsables != null) {
                for (Responsable responsable : responsables) {
                    if (responsable.getId().equals(quotation.getResponsable().getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found)
                newResponsable = employeeService.getCurrentMyJssUser();
        } else {
            newResponsable = employeeService.getCurrentMyJssUser();
        }

        Quotation fetchQuotation = null;
        if (quotation.getId() == null) {
            populateBooleansOfProvisions(quotation);
            quotation.setResponsable(newResponsable);
            quotationValidationHelper.completeIQuotationDocuments(quotation, false);
            quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginMyJss());
            quotation.setQuotationStatus(quotationStatusService.getQuotationStatusByCode(QuotationStatus.DRAFT));
            fetchQuotation = quotationService.addOrUpdateQuotation(quotation);
        } else
            fetchQuotation = quotationService.getQuotation(quotation.getId());

        if (fetchQuotation.getResponsable() == null
                || !fetchQuotation.getResponsable().getId().equals(newResponsable.getId())) {
            fetchQuotation.setResponsable(newResponsable);
            fetchQuotation = quotationService.addOrUpdateQuotationFromUser(fetchQuotation);
        }

        if (isValidation != null && isValidation) {
            quotationService.addOrUpdateQuotationStatus(quotation, QuotationStatus.TO_VERIFY);
        }

        return quotation;

    }

    @Transactional(rollbackFor = Exception.class)
    public IQuotation validateAndCreateQuotation(IQuotation quotation, Boolean isValidation, HttpServletRequest request)
            throws OsirisException {

        Responsable responsable = null;
        Boolean hasToSendConfirmation = false;
        Boolean shouldConnectUserAtTheEnd = true;
        if (quotation.getResponsable() != null && quotation.getResponsable().getMail() != null) {
            responsable = responsableService.getResponsableByMail(quotation.getResponsable().getMail().getMail());
            if (responsable != null) {
                quotation.setResponsable(responsable);
                if (employeeService.getCurrentMyJssUser() == null) {
                    // User create IQuotation but not connected => send a mail
                    hasToSendConfirmation = true;
                    shouldConnectUserAtTheEnd = false;
                }
            }

            else {
                mailService.populateMailId(quotation.getResponsable().getMail());
                populateTiersAndResponsable(quotation);
                Boolean isTiersValid = tiersValidationHelper.validateTiers(quotation.getResponsable().getTiers());

                if (!isTiersValid) {
                    throw new OsirisValidationException("Tiers");
                } else {
                    tiersService.addOrUpdateTiers(quotation.getResponsable().getTiers());
                }
            }
        }

        quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginMyJss());
        populateBooleansOfProvisions(quotation);

        // Save new affaire
        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            if (asso.getAffaire() != null) {
                if (asso.getAffaire().getId() == null
                        && (asso.getAffaire().getDenomination() != null || asso.getAffaire().getLastname() != null)) {
                    asso.setAffaire(affaireService.addOrUpdateAffaire(asso.getAffaire()));
                }
                saveNewMailsOnAffaire(quotation);
            } else {
                Affaire newAffaire = createAffaireWithTiers(quotation.getResponsable().getTiers());
                asso.setAffaire(affaireService.addOrUpdateAffaire(newAffaire));
            }
        }

        // Validate Quotation or CustomerOrder
        quotationValidationHelper.validateQuotationAndCustomerOrder(quotation, null);

        // Save Quotation or CustomerOrder
        if (!quotation.getIsQuotation()) {
            ((CustomerOrder) quotation).setCustomerOrderStatus(
                    customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT));
            quotation = customerOrderService.addOrUpdateCustomerOrder((CustomerOrder) quotation, false, false);
            if (isValidation) {
                quotation = customerOrderService.addOrUpdateCustomerOrderStatus((CustomerOrder) quotation,
                        CustomerOrderStatus.BEING_PROCESSED, true);
                if (customerOrderService.isOnlyJssAnnouncement((CustomerOrder) quotation, true))
                    customerOrderService.autoBilledProvisions((CustomerOrder) quotation);
            }
        }
        if (quotation.getIsQuotation()) {
            ((Quotation) quotation)
                    .setQuotationStatus(quotationStatusService.getQuotationStatusByCode(QuotationStatus.TO_VERIFY));
            quotation = quotationService.addOrUpdateQuotation((Quotation) quotation);
            if (isValidation)
                quotation = quotationService.addOrUpdateQuotationStatus((Quotation) quotation,
                        QuotationStatus.TO_VERIFY);
        }

        if (hasToSendConfirmation)
            if (quotation.getIsQuotation())
                mailHelper.sendConfirmationQuotationCreationMyJss(
                        quotation.getResponsable().getMail().getMail(), (Quotation) quotation);
            else
                mailHelper.sendConfirmationOrderCreationMyJss(quotation.getResponsable().getMail().getMail(),
                        (CustomerOrder) quotation);

        if (shouldConnectUserAtTheEnd)
            userScopeService.authenticateUser(quotation.getResponsable(), request);
        else
            return null;
        return quotation;
    }

    public void saveNewMailsOnAffaire(IQuotation quotation) throws OsirisDuplicateException, OsirisException {
        // Save new mails
        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            if (asso.getAffaire().getMails() != null) {
                for (Mail mail : asso.getAffaire().getMails()) {
                    if (mail.getMail() == null)
                        continue;

                    Affaire originalAffaire = affaireService.getAffaire(asso.getAffaire().getId());
                    boolean found = false;
                    if (originalAffaire.getMails() != null) {
                        for (Mail mailAffaire : originalAffaire.getMails()) {
                            if (mailAffaire.getMail().equals(mail.getMail())) {
                                found = true;
                            }
                        }
                    }
                    if (!found) {
                        if (originalAffaire.getMails() == null) {
                            originalAffaire.setMails(new ArrayList<Mail>());
                        }
                        Mail newMail = new Mail();
                        newMail.setMail(mail.getMail());
                        mailService.populateMailId(newMail);
                        originalAffaire.getMails().add(newMail);
                        affaireService.addOrUpdateAffaire(originalAffaire);
                    }
                }
            }
        }
    }

    private void populateTiersAndResponsable(IQuotation quotation) throws OsirisException {
        quotation.getResponsable().setIsActive(true);
        quotation.getResponsable().setIsBouclette(true);
        quotation.getResponsable().setTiersType(constantService.getTiersTypeClient());
        quotation.getResponsable().setSalesEmployee(constantService.getEmployeeSalesDirector());
        quotation.getResponsable().setLanguage(constantService.getLanguageFrench());

        quotation.getResponsable().setDocuments(new ArrayList<Document>());
        // Cloning quotation documents on Responsable and Tiers
        quotation.getResponsable().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getBillingDocument(quotation.getDocuments()),
                        null));
        quotation.getResponsable().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getDigitalDocument(quotation.getDocuments()),
                        null));
        quotation.getResponsable().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getPaperDocument(quotation.getDocuments()),
                        null));

        quotation.getResponsable().getTiers().setDocuments(new ArrayList<Document>());
        quotation.getResponsable().getTiers().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getBillingDocument(quotation.getDocuments()),
                        null));
        quotation.getResponsable().getTiers().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getDigitalDocument(quotation.getDocuments()),
                        null));
        quotation.getResponsable().getTiers().getDocuments()
                .add(documentService.cloneOrMergeDocument(documentService.getPaperDocument(quotation.getDocuments()),
                        null));

        Document documentDunning = new Document();
        documentDunning.setPaymentDeadlineType(constantService.getPaymentDeadLineType30());
        documentDunning.setIsRecipientAffaire(false);
        documentDunning.setIsRecipientClient(false);
        documentDunning.setDocumentType(constantService.getDocumentTypeDunning());
        quotation.getResponsable().getTiers().setIsProvisionalPaymentMandatory(true);
        quotation.getResponsable().getTiers().getDocuments().add(documentDunning);

        Document receiptDocument = new Document();
        receiptDocument.setBillingClosureRecipientType(constantService.getBillingClosureRecipientTypeClient());
        receiptDocument.setIsRecipientAffaire(false);
        receiptDocument.setIsRecipientClient(false);
        receiptDocument.setDocumentType(constantService.getDocumentTypeBillingClosure());
        receiptDocument.setBillingClosureType(constantService.getBillingClosureTypeAffaire());
        quotation.getResponsable().getTiers().getDocuments().add(receiptDocument);

        quotation.getResponsable().getTiers().setIsNewTiers(true);

        if (quotation.getResponsable().getTiers().getIsIndividual() == null)
            quotation.getResponsable().getTiers().setIsIndividual(false);

        if (quotation.getResponsable().getTiers().getIsIndividual()) {
            quotation.getResponsable().getTiers().setCivility(quotation.getResponsable().getCivility());
            quotation.getResponsable().getTiers().setFirstname(quotation.getResponsable().getFirstname());
            quotation.getResponsable().getTiers().setLastname(quotation.getResponsable().getLastname());
            quotation.getResponsable().getTiers().setAddress(quotation.getResponsable().getAddress());
            quotation.getResponsable().getTiers().setCountry(quotation.getResponsable().getCountry());
            quotation.getResponsable().getTiers().setPostalCode(quotation.getResponsable().getPostalCode());
            quotation.getResponsable().getTiers().setCity(quotation.getResponsable().getCity());
        }

        quotation.getResponsable().getTiers().setTiersType(constantService.getTiersTypeClient());
        quotation.getResponsable().getTiers().setTiersType(constantService.getTiersTypeClient());
        quotation.getResponsable().getTiers().setSalesEmployee(constantService.getEmployeeSalesDirector());
        // quotation.getResponsable().getTiers().setSiret(quotation.getSiret());
        quotation.getResponsable().getTiers().setIsSepaMandateReceived(false);
        quotation.getResponsable().getTiers().setLanguage(constantService.getLanguageFrench());
        quotation.getResponsable().getTiers().setPaymentType(constantService.getPaymentTypeCB());
        // TODO : check if good, but needed for the validationHelper
        quotation.getResponsable().getTiers().setDeliveryService(constantService.getDeliveryServiceJss());

        quotation.getResponsable().getTiers().setResponsables(new ArrayList<Responsable>());
        quotation.getResponsable().setTiers(quotation.getResponsable().getTiers());
        quotation.getResponsable().getTiers().getResponsables().add(quotation.getResponsable());
    }

    public void populateBooleansOfProvisions(IQuotation quotation) throws OsirisException {
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                if (asso.getServices() != null)
                    for (Service service : asso.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions()) {
                                provision.setIsApplicationFees(false);
                                provision.setIsBaloNormalization(false);
                                provision.setIsBaloPackage(false);
                                provision.setIsBaloPublicationFlag(false);
                                provision.setIsBankCheque(false);
                                provision.setIsBilan(false);
                                provision.setIsBodaccFollowup(false);
                                provision.setIsBodaccFollowupAndRedaction(false);
                                provision.setIsBusinnessNantissementRedaction(false);
                                provision.setIsChronopostFees(false);
                                provision.setIsComplexeFile(false);
                                provision.setIsCorrespondenceFees(false);
                                provision.setIsDisbursement(false);
                                provision.setIsDocumentScanning(false);
                                provision.setIsFeasibilityStudy(false);
                                provision.setIsFormalityAdditionalDeclaration(false);
                                provision.setIsLogo(false);
                                provision.setIsNantissementDeposit(false);
                                if (provision.getIsPublicationFlag() == null)
                                    provision.setIsPublicationFlag(false);
                                if (provision.getIsPublicationPaper() == null)
                                    provision.setIsPublicationPaper(false);
                                if (provision.getIsPublicationReceipt() == null)
                                    provision.setIsPublicationReceipt(false);
                                provision.setIsRegisterInitials(false);
                                provision.setIsRegisterPurchase(false);
                                provision.setIsRegisterShippingCosts(false);
                                provision.setIsRneUpdate(false);
                                provision.setIsSellerPrivilegeRedaction(false);
                                provision.setIsSocialShareNantissementRedaction(false);
                                provision.setIsSupplyFullBeCopy(false);
                                provision.setIsTreatmentMultipleModiciation(false);
                                provision.setIsVacationMultipleModification(false);
                                provision.setIsVacationUpdateBeneficialOwners(false);
                                if (provision.getIsRedactedByJss() == null)
                                    provision.setIsRedactedByJss(false);
                                if (provision.getIsEmergency() == null)
                                    provision.setIsEmergency(false);
                            }

    }

    private Affaire createAffaireWithTiers(Tiers tiers) {
        Affaire affaire = new Affaire();
        affaire.setDenomination(tiers.getDenomination());
        affaire.setCivility(tiers.getCivility());
        affaire.setAddress(tiers.getAddress());
        affaire.setPostalCode(tiers.getPostalCode());
        affaire.setCity(tiers.getCity());
        affaire.setCountry(tiers.getCountry());
        affaire.setIsIndividual(tiers.getIsIndividual());
        affaire.setFirstname(tiers.getFirstname());
        affaire.setLastname(tiers.getLastname());
        List<Mail> mails = new ArrayList<>();
        if (tiers.getMails() != null) {
            for (Mail mail : tiers.getMails()) {
                mails.add(mail);
            }
        }
        affaire.setMails(mails);
        List<Phone> phones = new ArrayList<>();
        if (tiers.getPhones() != null) {
            for (Phone phone : tiers.getPhones()) {
                phones.add(phone);
            }
        }
        affaire.setPhones(phones);
        affaire.setSiret(tiers.getSiret());
        return affaire;
    }
}
