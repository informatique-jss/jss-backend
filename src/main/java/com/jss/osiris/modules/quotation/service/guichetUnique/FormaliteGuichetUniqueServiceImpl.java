package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentVatSettingService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ValidationsRequestStatus;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PartnerCenterRepository;
import com.jss.osiris.modules.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.quotation.service.FormaliteService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDocumentService;

@org.springframework.stereotype.Service
public class FormaliteGuichetUniqueServiceImpl implements FormaliteGuichetUniqueService {

    @Autowired
    FormaliteGuichetUniqueRepository formaliteGuichetUniqueRepository;

    @Autowired
    GuichetUniqueDelegateService guichetUniqueDelegateService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    FormaliteService formaliteService;

    @Autowired
    PartnerCenterRepository partnerCenterRepository;

    @Autowired
    DepartmentVatSettingService departmentVatSettingService;

    @Autowired
    TypeDocumentService typeDocumentService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    BatchService batchService;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    FormaliteGuichetUniqueStatusService formaliteGuichetUniqueStatusService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    private String cartStatusPayed = "PAID";
    private String cartStatusRefund = "REFUNDED";

    @Override
    public FormaliteGuichetUnique addOrUpdateFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique) {
        ArrayList<Cart> cartToRemove = new ArrayList<Cart>();
        if (formaliteGuichetUnique.getCarts() != null)
            for (Cart cart : formaliteGuichetUnique.getCarts())
                if (!cart.getStatus().equals(cartStatusPayed) && !cart.getStatus().equals(cartStatusRefund))
                    cartToRemove.add(cart);

        if (cartToRemove.size() > 0)
            formaliteGuichetUnique.getCarts().removeAll(cartToRemove);

        return formaliteGuichetUniqueRepository.save(formaliteGuichetUnique);
    }

    @Override
    public FormaliteGuichetUnique getFormaliteGuichetUnique(Integer id) {
        Optional<FormaliteGuichetUnique> formaliteGuichetUnique = formaliteGuichetUniqueRepository.findById(id);
        if (formaliteGuichetUnique.isPresent())
            return formaliteGuichetUnique.get();
        return null;
    }

    @Override
    public List<FormaliteGuichetUnique> getFormaliteGuichetUniqueToSign() {
        return formaliteGuichetUniqueRepository.findFormaliteToSign(Arrays.asList(formaliteGuichetUniqueStatusService
                .getFormaliteGuichetUniqueStatus(FormaliteGuichetUniqueStatus.SIGNATURE_PENDING),
                formaliteGuichetUniqueStatusService
                        .getFormaliteGuichetUniqueStatus(FormaliteGuichetUniqueStatus.AMENDMENT_SIGNATURE_PENDING)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(FormaliteGuichetUnique savedFormaliteGuichetUnique,
            Formalite formalite, boolean generateInvoices)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (savedFormaliteGuichetUnique == null)
            throw new OsirisValidationException("savedFormaliteGuichetUnique");

        if (formalite != null && formalite.getId() != null)
            formalite = formaliteService.getFormalite(formalite.getId());

        List<FormaliteStatusHistoryItem> apiFormaliteStatusHistoryItems = new ArrayList<FormaliteStatusHistoryItem>();
        FormaliteGuichetUnique apiFormaliteGuichetUnique = null;

        // Fetch API version
        if (savedFormaliteGuichetUnique.getIsAnnualAccounts() != null
                && savedFormaliteGuichetUnique.getIsAnnualAccounts()) {
            apiFormaliteGuichetUnique = guichetUniqueDelegateService
                    .getAnnualAccountById(savedFormaliteGuichetUnique.getId());
            apiFormaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getAnnualAccountStatusHistoriesById(savedFormaliteGuichetUnique.getId());
        } else if (savedFormaliteGuichetUnique.getIsActeDeposit() != null
                && savedFormaliteGuichetUnique.getIsActeDeposit()) {
            apiFormaliteGuichetUnique = guichetUniqueDelegateService
                    .getActeDepositById(savedFormaliteGuichetUnique.getId());
            apiFormaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getActeDepositStatusHistoriesById(savedFormaliteGuichetUnique.getId());
        } else {
            apiFormaliteGuichetUnique = guichetUniqueDelegateService
                    .getFormalityById(savedFormaliteGuichetUnique.getId());
            apiFormaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getFormalityStatusHistoriesById(savedFormaliteGuichetUnique.getId());
        }

        if (apiFormaliteGuichetUnique.getValidationsRequests() != null)
            for (ValidationRequest validationRequest : apiFormaliteGuichetUnique.getValidationsRequests()) {
                if (validationRequest.getPartnerCenter() != null)
                    partnerCenterRepository.save(validationRequest.getPartnerCenter());
            }

        boolean formalityHasNewStatus = false;

        if (formalite == null) {
            return addOrUpdateFormaliteGuichetUnique(apiFormaliteGuichetUnique);
        } else if (formalite.getProvision() != null && formalite.getProvision().size() > 0) {
            // Content field
            savedFormaliteGuichetUnique.setContent(apiFormaliteGuichetUnique.getContent());

            // Status field
            if (!savedFormaliteGuichetUnique.getStatus().getCode()
                    .equals(apiFormaliteGuichetUnique.getStatus().getCode())) {
                savedFormaliteGuichetUnique.setStatus(apiFormaliteGuichetUnique.getStatus());
                savedFormaliteGuichetUnique.setIsAuthorizedToSign(false);
                formalityHasNewStatus = true;
                addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);
            }

            // Cart field
            if (apiFormaliteGuichetUnique.getCarts() != null && apiFormaliteGuichetUnique.getCarts().size() > 0) {
                if (savedFormaliteGuichetUnique.getCarts() == null
                        || savedFormaliteGuichetUnique.getCarts().size() == 0) {
                    savedFormaliteGuichetUnique.setCarts(new ArrayList<Cart>());
                    for (Cart currentCart : apiFormaliteGuichetUnique.getCarts()) {
                        // Save only if cart > €
                        if (currentCart.getTotal() != 0 && (currentCart.getStatus().equals(cartStatusPayed)
                                || currentCart.getStatus().equals(cartStatusRefund))) {
                            currentCart.setFormaliteGuichetUnique(savedFormaliteGuichetUnique);
                            if (currentCart.getCartRates() != null)
                                for (CartRate cartRate : currentCart.getCartRates())
                                    cartRate.setCart(currentCart);
                            savedFormaliteGuichetUnique.getCarts().add(currentCart);
                        }
                    }
                } else {
                    for (Cart currentCart : apiFormaliteGuichetUnique.getCarts()) {
                        boolean found = false;
                        for (Cart originalCart : savedFormaliteGuichetUnique.getCarts()) {
                            if (originalCart.getId().equals(currentCart.getId())) {
                                if (!originalCart.getStatus().equals(currentCart.getStatus()))
                                    originalCart.setStatus(currentCart.getStatus());
                                found = true;
                            }
                        }
                        if (!found && currentCart.getTotal() != 0) {
                            currentCart.setFormaliteGuichetUnique(savedFormaliteGuichetUnique);
                            if (currentCart.getCartRates() != null)
                                for (CartRate cartRate : currentCart.getCartRates())
                                    cartRate.setCart(currentCart);
                            savedFormaliteGuichetUnique.getCarts().add(currentCart);
                        }
                    }
                }

                savedFormaliteGuichetUnique = addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);

                if (generateInvoices) {
                    for (Cart currentCart : savedFormaliteGuichetUnique.getCarts()) {
                        if (currentCart.getInvoice() == null && currentCart.getTotal() != 0) {
                            if (currentCart.getStatus().equals(cartStatusPayed)) {
                                currentCart.setInvoice(
                                        generateInvoiceFromCart(currentCart, formalite.getProvision().get(0)));
                            } else if (currentCart.getStatus().equals(cartStatusRefund)) {
                                currentCart.setInvoice(
                                        (generateCreditNoteFromCart(currentCart, formalite.getProvision().get(0))));
                            }
                        }
                    }
                    savedFormaliteGuichetUnique = addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);
                }
            }

            // Download attachments
            savedFormaliteGuichetUnique.getContent()
                    .setPiecesJointes(getAttachmentOfFormaliteGuichetUnique(savedFormaliteGuichetUnique));
            if (savedFormaliteGuichetUnique.getContent().getPiecesJointes() != null
                    && savedFormaliteGuichetUnique.getContent().getPiecesJointes().size() > 0)
                for (PiecesJointe piecesJointe : savedFormaliteGuichetUnique.getContent().getPiecesJointes())
                    piecesJointe.setContent(savedFormaliteGuichetUnique.getContent());

            savedFormaliteGuichetUnique = addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);

            if (savedFormaliteGuichetUnique.getContent().getPiecesJointes() != null
                    && savedFormaliteGuichetUnique.getContent().getPiecesJointes().size() > 0) {
                List<TypeDocument> typeDocuments = typeDocumentService.getTypeDocument();
                List<String> typeDocumentsToDownload = new ArrayList<String>();
                if (typeDocuments != null)
                    for (TypeDocument typeDocument : typeDocuments)
                        if (typeDocument.getIsToDownloadOnProvision() != null
                                && typeDocument.getIsToDownloadOnProvision())
                            typeDocumentsToDownload.add(typeDocument.getCode());

                if (typeDocumentsToDownload.size() > 0) {
                    for (PiecesJointe piecesJointe : savedFormaliteGuichetUnique.getContent().getPiecesJointes())
                        if (typeDocumentsToDownload.contains(piecesJointe.getTypeDocument().getCode())) {
                            downloadPieceJointeOnProvision(formalite.getProvision().get(0), piecesJointe);
                        }
                }
            }

            // validationsRequests field
            savedFormaliteGuichetUnique.setValidationsRequests(apiFormaliteGuichetUnique.getValidationsRequests());
            if (savedFormaliteGuichetUnique.getValidationsRequests() != null)
                for (ValidationRequest validationRequest : savedFormaliteGuichetUnique.getValidationsRequests())
                    validationRequest.setFormaliteGuichetUnique(savedFormaliteGuichetUnique);

            // update status history items
            savedFormaliteGuichetUnique.setFormaliteStatusHistoryItems(apiFormaliteStatusHistoryItems);
            for (FormaliteStatusHistoryItem formaliteStatusHistoryItem : savedFormaliteGuichetUnique
                    .getFormaliteStatusHistoryItems())
                formaliteStatusHistoryItem.setFormaliteGuichetUnique(savedFormaliteGuichetUnique);

            savedFormaliteGuichetUnique = addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);

            // Update provision waiting AC field
            if (formalityHasNewStatus) {
                if (savedFormaliteGuichetUnique.getStatus().getCode()
                        .equals(FormaliteGuichetUniqueStatus.VALIDATION_PENDING)
                        && savedFormaliteGuichetUnique.getValidationsRequests() != null) {
                    for (ValidationRequest validationRequest : savedFormaliteGuichetUnique.getValidationsRequests()) {
                        if (validationRequest.getStatus().getCode()
                                .equals(ValidationsRequestStatus.MSA_ACCEPTATION_PENDING)
                                || validationRequest.getStatus().getCode()
                                        .equals(ValidationsRequestStatus.VALIDATION_PENDING)) {
                            List<CompetentAuthority> competentAuthorities = null;
                            if (validationRequest.getPartnerCenter() != null)
                                competentAuthorityService
                                        .getCompetentAuthorityByInpiReference(
                                                validationRequest.getPartnerCenter().getCode());

                            // Try with partner label
                            if ((competentAuthorities == null || competentAuthorities.size() == 0)
                                    && validationRequest.getPartner() != null) {
                                competentAuthorities = competentAuthorityService
                                        .getCompetentAuthorityByInpiReference(
                                                validationRequest.getPartner().getLibelleCourt());
                            }
                            if (competentAuthorities != null && competentAuthorities.size() == 1) {
                                savedFormaliteGuichetUnique.getFormalite()
                                        .setWaitedCompetentAuthority(competentAuthorities.get(0));
                                formaliteService.addOrUpdateFormalite(savedFormaliteGuichetUnique.getFormalite());
                                break;
                            }
                        }
                    }
                }

                // Update formalite status based on GU status
                if (formalite.getFormaliteStatus().getIsCloseState() == null
                        || formalite.getFormaliteStatus().getIsCloseState() == false) {
                    if (savedFormaliteGuichetUnique.getStatus().getCode()
                            .equals(FormaliteGuichetUniqueStatus.AMENDMENT_PENDING)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.ERROR_INSEE_EXISTS_PM)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.ERROR_INSEE_EXISTS_PP)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.ERROR_DECLARATION_INSEE)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.ERROR)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.EXPIRED)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.REJECTED)) {
                        savedFormaliteGuichetUnique.getFormalite().setFormaliteStatus(formaliteStatusService
                                .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_REJECTED));
                        CustomerOrderComment customerOrderComment = customerOrderCommentService
                                .createCustomerOrderComment(
                                        savedFormaliteGuichetUnique.getFormalite()
                                                .getProvision().get(0).getService().getAssoAffaireOrder()
                                                .getCustomerOrder(),
                                        "Formalité GU n°" + savedFormaliteGuichetUnique.getLiasseNumber() + " rejetée ("
                                                + formaliteGuichetUniqueStatusService
                                                        .getFormaliteGuichetUniqueStatus(
                                                                savedFormaliteGuichetUnique.getStatus().getCode())
                                                        .getLabel()
                                                + ")");

                        customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                                constantService.getActiveDirectoryGroupFormalites());

                    } else if (savedFormaliteGuichetUnique.getStatus().getCode()
                            .equals(FormaliteGuichetUniqueStatus.VALIDATED_DGFIP)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.VALIDATED_PARTNER)
                            || savedFormaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.VALIDATED)) {
                        savedFormaliteGuichetUnique.getFormalite().setFormaliteStatus(formaliteStatusService
                                .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_VALIDATED));
                        CustomerOrderComment customerOrderComment = customerOrderCommentService
                                .createCustomerOrderComment(
                                        savedFormaliteGuichetUnique.getFormalite()
                                                .getProvision().get(0).getService().getAssoAffaireOrder()
                                                .getCustomerOrder(),
                                        "Formalité GU n°" + savedFormaliteGuichetUnique.getLiasseNumber() + " validée");

                        customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                                constantService.getActiveDirectoryGroupFormalites());
                    }
                }
                formaliteService.addOrUpdateFormalite(savedFormaliteGuichetUnique.getFormalite());
            }

            savedFormaliteGuichetUnique.setFormalite(formalite);
            addOrUpdateFormaliteGuichetUnique(savedFormaliteGuichetUnique);

            if ((savedFormaliteGuichetUnique.getStatus().getCode()
                    .equals(FormaliteGuichetUniqueStatus.SIGNATURE_PENDING)
                    || savedFormaliteGuichetUnique.getStatus().getCode()
                            .equals(FormaliteGuichetUniqueStatus.AMENDMENT_SIGNATURE_PENDING)
                            && savedFormaliteGuichetUnique.getIsAuthorizedToSign() != null
                            && savedFormaliteGuichetUnique.getIsAuthorizedToSign()))
                batchService.declareNewBatch(Batch.SIGN_FORMALITE_GUICHET_UNIQUE, savedFormaliteGuichetUnique.getId());

            if (formalite != null && savedFormaliteGuichetUnique != null && (Arrays
                    .asList(FormaliteGuichetUniqueStatus.PAYMENT_PENDING,
                            FormaliteGuichetUniqueStatus.PAYMENT_VALIDATION_PENDING,
                            FormaliteGuichetUniqueStatus.AMENDMENT_PAYMENT_PENDING,
                            FormaliteGuichetUniqueStatus.AMENDMENT_PAYMENT_VALIDATION_PENDING)
                    .contains(savedFormaliteGuichetUnique.getStatus().getCode())
                    || savedFormaliteGuichetUnique.getStatus().getCode()
                            .equals(FormaliteGuichetUniqueStatus.AMENDMENT_PENDING)
                            && savedFormaliteGuichetUnique.getIsAuthorizedToSign() != null
                            && savedFormaliteGuichetUnique.getIsAuthorizedToSign()))
                batchService.declareNewBatch(Batch.PAY_FORMALITE_GUICHET_UNIQUE, savedFormaliteGuichetUnique.getId());
        }
        return savedFormaliteGuichetUnique;

    }

    private List<PiecesJointe> getAttachmentOfFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        if (formaliteGuichetUnique.getIsActeDeposit())
            return guichetUniqueDelegateService.getActeDepositAttachments(formaliteGuichetUnique);
        if (formaliteGuichetUnique.getIsAnnualAccounts())
            return guichetUniqueDelegateService.getAnnualAccountsAttachments(formaliteGuichetUnique);
        if (formaliteGuichetUnique.getIsFormality())
            return guichetUniqueDelegateService.getFormalityAttachments(formaliteGuichetUnique);
        return null;
    }

    private void downloadPieceJointeOnProvision(Provision provision, PiecesJointe piecesJointe)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (provision != null && piecesJointe != null) {
            if (provision.getAttachments() == null)
                provision.setAttachments(new ArrayList<Attachment>());

            boolean attachmentFound = false;
            for (Attachment attachment : provision.getAttachments()) {
                if (attachment.getPiecesJointe() != null
                        && attachment.getPiecesJointe().getAttachmentId().equals(piecesJointe.getAttachmentId())) {
                    attachmentFound = true;
                    break;
                }
            }

            if (!attachmentFound) {
                TypeDocument typeDocument = typeDocumentService
                        .getTypeDocumentByCode(piecesJointe.getTypeDocument().getCode());
                File file = null;
                try {
                    file = guichetUniqueDelegateService
                            .getAttachmentById(piecesJointe.getAttachmentId(), null);
                } catch (Exception e) {
                    globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG); // TODO : To handle files not found
                                                                                   // in INPI ... To remove when
                                                                                   // possible
                }
                if (file != null)
                    try {
                        attachmentService.addAttachment(new FileInputStream(file), provision.getId(), null,
                                Provision.class.getSimpleName(),
                                typeDocument.getAttachmentType(), piecesJointe.getNomDocument(), false,
                                piecesJointe.getNomDocument(), piecesJointe, null, piecesJointe.getTypeDocument());
                        file.delete();
                    } catch (FileNotFoundException e) {
                        throw new OsirisException(e, "erreur when reading file");
                    }
            }
        }
    }

    private Invoice generateInvoiceFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException,
            OsirisValidationException, OsirisDuplicateException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getService().getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" +
                cart.getId());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        PaymentType paymentType = null;

        if (cart.getPaymentType() == null)
            paymentType = constantService.getPaymentTypeAccount();
        else
            paymentType = paymentTypeService.getPaymentTypeByCodeInpi(cart.getPaymentType());

        if (paymentType == null)
            throw new OsirisValidationException("Unable to find payment type for INPI code "
                    + cart.getPaymentType() + ". Please fill referential with correct value");

        invoice.setManualPaymentType(paymentType);

        invoice.setManualAccountingDocumentDate(
                LocalDate.parse(cart.getPaymentDate(),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            for (Service service : asso.getServices())
                if (asso.getId().equals(service.getAssoAffaireOrder().getId()))
                    for (Provision inProvision : service.getProvisions()) {
                        if (inProvision.getInvoiceItems() == null)
                            inProvision.setInvoiceItems(new ArrayList<InvoiceItem>());

                        if (provision.getId().equals(inProvision.getId())) {
                            for (CartRate cartRate : cart.getCartRates()) {
                                InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                                invoiceItem.setProvision(null);
                                invoice.getInvoiceItems().add(invoiceItem);
                                provision.getInvoiceItems().add(invoiceItem);
                            }
                        }
                    }

        invoice.setProvision(provision);
        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
    }

    private Invoice generateCreditNoteFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException,
            OsirisValidationException, OsirisDuplicateException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getService().getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" +
                cart.getId());
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        PaymentType paymentType = null;
        if (cart.getPaymentType() == null &&
                cart.getStatus().equals(cartStatusRefund))
            paymentType = constantService.getPaymentTypeAccount();
        else
            paymentType = paymentTypeService.getPaymentTypeByCodeInpi(cart.getPaymentType());

        if (paymentType == null)
            throw new OsirisValidationException("Unable to find payment type for INPI code "
                    + cart.getPaymentType() + ". Please fill referential with correct value");
        invoice.setManualPaymentType(paymentType);

        invoice.setManualAccountingDocumentDate(
                LocalDate.parse(cart.getPaymentDate() != null ? cart.getPaymentDate() : cart.getUpdated(),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            if (asso.getId().equals(provision.getService().getAssoAffaireOrder().getId()))
                for (Service service : asso.getServices())
                    for (Provision inProvision : service.getProvisions()) {
                        if (inProvision.getInvoiceItems() == null)
                            inProvision.setInvoiceItems(new ArrayList<InvoiceItem>());

                        if (provision.getId().equals(inProvision.getId())) {
                            cart.getCartRates()
                                    .sort((o1, o2) -> ((Long) o1.getAmount()).compareTo((Long) (o2.getAmount())));
                            InvoiceItem firstItemTaxable = null;
                            InvoiceItem firstItemNonTaxable = null;
                            for (CartRate cartRate : cart.getCartRates()) {
                                if (cartRate.getRate() != null && cartRate.getAmount() != 0) {
                                    boolean initItem = true;
                                    if (cartRate.getAmount() > 0) {
                                        InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                                        if (invoiceItem.getBillingItem().getBillingType().getId()
                                                .equals(constantService.getBillingTypeDeboursNonTaxable().getId())) {
                                            if (firstItemNonTaxable != null) {
                                                firstItemNonTaxable.setPreTaxPrice(
                                                        firstItemNonTaxable.getPreTaxPrice()
                                                                - Math.abs(invoiceItem.getPreTaxPrice()));
                                                firstItemNonTaxable.setPreTaxPriceReinvoiced(
                                                        -Math.abs(firstItemNonTaxable.getPreTaxPrice()));
                                                initItem = false;
                                            }
                                        } else {
                                            if (firstItemTaxable != null) {
                                                firstItemTaxable.setPreTaxPrice(
                                                        firstItemTaxable.getPreTaxPrice()
                                                                - Math.abs(invoiceItem.getPreTaxPrice()));
                                                firstItemTaxable.setPreTaxPriceReinvoiced(
                                                        -Math.abs(firstItemTaxable.getPreTaxPrice()));
                                                initItem = false;
                                            }
                                        }
                                    }
                                    if (initItem) {
                                        InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                                        invoiceItem.setPreTaxPrice(Math.abs(invoiceItem.getPreTaxPrice()));
                                        invoiceItem.setPreTaxPriceReinvoiced(
                                                -Math.abs(invoiceItem.getPreTaxPrice()));
                                        invoiceItem.setProvision(null);
                                        invoice.getInvoiceItems().add(invoiceItem);
                                        provision.getInvoiceItems().add(invoiceItem);

                                        if (invoiceItem.getBillingItem().getBillingType().getId()
                                                .equals(constantService.getBillingTypeDeboursNonTaxable().getId())) {
                                            if (firstItemNonTaxable == null)
                                                firstItemNonTaxable = invoiceItem;
                                        } else {
                                            if (firstItemTaxable == null)
                                                firstItemTaxable = invoiceItem;
                                        }
                                    }
                                }
                            }
                        }
                    }

        invoice.setIsInvoiceFromProvider(false);
        invoice.setIsProviderCreditNote(true);
        invoice.setProvision(provision);

        return invoiceHelper.getPriceTotal(invoice) > 0f ? invoiceService.addOrUpdateInvoiceFromUser(invoice) : null;
    }

    private InvoiceItem getInvoiceItemForCartRate(CartRate cartRate, Cart cart) throws OsirisException {
        InvoiceItem invoiceItem = new InvoiceItem();
        extractVatFromCartRate(invoiceItem, cartRate);
        invoiceItem.setDiscountAmount(0f);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);

        List<CompetentAuthority> competentAuthorities = competentAuthorityService
                .getCompetentAuthorityByInpiReference(cartRate.getRecipientCode());

        CompetentAuthority competentAuthority;
        // TODO : handle multiple INPI code for same AC, for CMA for instance
        if (competentAuthorities == null || competentAuthorities.size() != 1)
            competentAuthority = constantService.getCompetentAuthorityInpi();
        else
            competentAuthority = competentAuthorities.get(0);

        invoiceItem.setLabel(competentAuthority.getLabel() + " - " + cartRate.getRate().getLabel());
        float amount = cartRate.getHtAmount();
        if (amount == 0)
            amount = cartRate.getAmount() / 1.2f;

        if (cartRate.getAmount() < 0 && amount > 0)
            amount = -amount;
        if (cartRate.getAmount() > 0 && amount < 0)
            amount = -amount;

        invoiceItem.setPreTaxPrice(Float.parseFloat(amount + "") / 100f);
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());

        return invoiceItem;
    }

    private void extractVatFromCartRate(InvoiceItem invoiceItem, CartRate cartRate) throws OsirisException {
        if (Math.abs(cartRate.getAmount()) == Math.abs(cartRate.getHtAmount())) {
            invoiceItem.setVat(constantService.getVatZero());
            invoiceItem.setVatPrice(0f);
            invoiceItem.setBillingItem(
                    pricingHelper.getAppliableBillingItem(constantService.getBillingTypeDeboursNonTaxable(), null));
        } else {
            Float vatRate = (cartRate.getAmount() - cartRate.getHtAmount()) * 1.0f / cartRate.getHtAmount() * 100f;
            vatRate = Math.round(vatRate * 10f) / 10f;
            Vat vat = null;
            invoiceItem.setBillingItem(pricingHelper
                    .getAppliableBillingItem(constantService.getBillingTypeEmolumentsDeGreffeDebour(), null));

            if (isVatEqual(vatRate, constantService.getVatDeductible().getRate()))
                vat = constantService.getVatDeductible();
            else if (isVatEqual(vatRate, constantService.getVatDeductibleTwo().getRate()))
                vat = constantService.getVatDeductibleTwo();
            else {
                List<DepartmentVatSetting> vatSettings = departmentVatSettingService.getDepartmentVatSettings();
                for (DepartmentVatSetting vatSetting : vatSettings) {
                    if (isVatEqual(vatRate, vatSetting.getIntermediateVatForPurshase().getRate())) {
                        vat = vatSetting.getIntermediateVatForPurshase();
                        break;
                    } else if (isVatEqual(vatRate, vatSetting.getReducedVatForPurshase().getRate())) {
                        vat = vatSetting.getReducedVatForPurshase();
                        break;
                    }
                }
            }

            if (vat != null) {
                invoiceItem.setVat(vat);
            }
        }
    }

    private boolean isVatEqual(Float vat1, Float vat2) {
        return Math.abs(vat1 - vat2) < 1;
    }

    @Override
    public List<FormaliteGuichetUnique> getFormaliteGuichetUniqueByLiasseNumber(String value) {
        return formaliteGuichetUniqueRepository.findByLiasseNumber(value);
    }

}