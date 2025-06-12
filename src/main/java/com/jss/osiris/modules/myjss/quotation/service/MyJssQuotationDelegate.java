package com.jss.osiris.modules.myjss.quotation.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.TiersValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
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
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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

    @Transactional(rollbackFor = Exception.class)
    public IQuotation validateAndCreateQuotation(IQuotation quotation, Boolean isValidation) throws OsirisException {

        Responsable responsable = null;
        if (quotation.getResponsable() != null && quotation.getResponsable().getMail() != null) {
            responsable = responsableService.getResponsableByMail(quotation.getResponsable().getMail().getMail());
            if (responsable != null)
                quotation.setResponsable(responsable);

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

        populateBooleansOfProvisions(quotation);

        // Save new affaire
        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            if (asso.getAffaire() != null && asso.getAffaire().getId() == null
                    && (asso.getAffaire().getDenomination() != null || asso.getAffaire().getLastname() != null)) {
                asso.setAffaire(affaireService.addOrUpdateAffaire(asso.getAffaire()));
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
                if (customerOrderService.isOnlyJssAnnouncement((CustomerOrder) quotation, true)) {
                    // quotationValidationHelper.validateQuotationAndCustomerOrder(customerOrder,
                    // CustomerOrderStatus.TO_BILLED);
                    customerOrderService.autoBilledProvisions((CustomerOrder) quotation);
                }
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

        return quotation;
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
        quotation.getResponsable().getTiers().setIsIndividual(false);
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
                                provision.setIsPublicationFlag(false);
                                provision.setIsPublicationPaper(false);
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
                                // TODO : delete after modif in front
                                provision.setIsRedactedByJss(false);
                            }

    }

}
