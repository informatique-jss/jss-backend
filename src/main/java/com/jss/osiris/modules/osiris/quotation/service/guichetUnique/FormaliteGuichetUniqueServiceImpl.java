package com.jss.osiris.modules.osiris.quotation.service.guichetUnique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DepartmentVatSettingService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.ValidationsRequestStatus;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.PartnerCenterRepository;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.FormaliteService;
import com.jss.osiris.modules.osiris.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeDocumentService;

@org.springframework.stereotype.Service
public class FormaliteGuichetUniqueServiceImpl implements FormaliteGuichetUniqueService {

    private BigDecimal zeroValue = new BigDecimal(0);
    private BigDecimal oneHundredValue = new BigDecimal(100);

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
        // invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        // TODO : refonte
        invoice.setProvider(constantService.getCompetentAuthorityInpi().getProvider());
        invoice.setCustomerOrderForInboundInvoice(provision.getService().getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" +
                cart.getId());
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

        if (invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders() != null)
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
        // invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        // TODO refonte
        invoice.setProvider(constantService.getCompetentAuthorityInpi().getProvider());
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

        if (invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders() != null)
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
                                                    .equals(constantService.getBillingTypeDeboursNonTaxable()
                                                            .getId())) {
                                                if (firstItemNonTaxable != null) {
                                                    firstItemNonTaxable.setPreTaxPrice(
                                                            firstItemNonTaxable.getPreTaxPrice()
                                                                    .subtract(invoiceItem.getPreTaxPrice().abs()));
                                                    firstItemNonTaxable.setPreTaxPriceReinvoiced(
                                                            firstItemNonTaxable.getPreTaxPrice().abs().negate());
                                                    initItem = false;
                                                }
                                            } else {
                                                if (firstItemTaxable != null) {
                                                    firstItemTaxable.setPreTaxPrice(
                                                            firstItemTaxable.getPreTaxPrice()
                                                                    .subtract(invoiceItem.getPreTaxPrice().abs()));
                                                    firstItemTaxable.setPreTaxPriceReinvoiced(
                                                            firstItemTaxable.getPreTaxPrice().abs().negate());
                                                    initItem = false;
                                                }
                                            }
                                        }
                                        if (initItem) {
                                            InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                                            invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice().abs());
                                            invoiceItem
                                                    .setPreTaxPriceReinvoiced(
                                                            invoiceItem.getPreTaxPrice().abs().negate());
                                            invoiceItem.setProvision(null);
                                            invoice.getInvoiceItems().add(invoiceItem);
                                            provision.getInvoiceItems().add(invoiceItem);

                                            if (invoiceItem.getBillingItem().getBillingType().getId()
                                                    .equals(constantService.getBillingTypeDeboursNonTaxable()
                                                            .getId())) {
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

        invoice.setIsCreditNote(true);
        invoice.setProvision(provision);

        return invoiceHelper.getPriceTotal(invoice).compareTo(zeroValue) > 0
                ? invoiceService.addOrUpdateInvoiceFromUser(invoice)
                : null;
    }

    private InvoiceItem getInvoiceItemForCartRate(CartRate cartRate, Cart cart) throws OsirisException {
        InvoiceItem invoiceItem = new InvoiceItem();
        extractVatFromCartRate(invoiceItem, cartRate);
        invoiceItem.setDiscountAmount(zeroValue);
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
        BigDecimal amount = BigDecimal.valueOf(cartRate.getHtAmount());
        if (amount.compareTo(zeroValue) == 0)
            amount = BigDecimal.valueOf(cartRate.getAmount() / 1.2f);

        if (cartRate.getAmount() < 0 && amount.compareTo(zeroValue) > 0)
            amount = amount.negate();
        if (cartRate.getAmount() > 0 && amount.compareTo(zeroValue) < 0)
            amount = amount.negate();

        if (cartRate.getQuantity() > 1)
            amount = amount.multiply(BigDecimal.valueOf(cartRate.getQuantity()));

        invoiceItem.setPreTaxPrice(amount.divide(oneHundredValue));
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());

        return invoiceItem;
    }

    private void extractVatFromCartRate(InvoiceItem invoiceItem, CartRate cartRate) throws OsirisException {
        if (Math.abs(cartRate.getAmount()) == Math.abs(cartRate.getHtAmount())) {
            invoiceItem.setVat(constantService.getVatZero());
            invoiceItem.setVatPrice(zeroValue);
            invoiceItem.setBillingItem(
                    pricingHelper.getAppliableBillingItem(constantService.getBillingTypeDeboursNonTaxable(), null));
        } else {
            BigDecimal vatRate = BigDecimal
                    .valueOf((cartRate.getAmount() - cartRate.getHtAmount()) * 1.0 / cartRate.getHtAmount() * 100.0);
            vatRate = vatRate.multiply(BigDecimal.TEN).setScale(0, RoundingMode.HALF_UP).divide(BigDecimal.TEN);
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

    private boolean isVatEqual(BigDecimal vat1, BigDecimal vat2) {
        return vat1.subtract(vat2).abs().compareTo(BigDecimal.ONE) < 0;
    }

    @Override
    public List<FormaliteGuichetUnique> getFormaliteGuichetUniqueByLiasseNumber(String value) {
        return formaliteGuichetUniqueRepository.findByLiasseNumber(value);
    }

}