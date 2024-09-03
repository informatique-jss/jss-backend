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
    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(FormaliteGuichetUnique inFormaliteGuichetUnique,
            Formalite formalite, boolean generateInvoices)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (inFormaliteGuichetUnique == null)
            throw new OsirisValidationException("inFormaliteGuichetUnique");

        if (formalite != null && formalite.getId() != null)
            formalite = formaliteService.getFormalite(formalite.getId());
        FormaliteGuichetUnique formaliteGuichetUnique;
        List<FormaliteStatusHistoryItem> formaliteStatusHistoryItems = new ArrayList<FormaliteStatusHistoryItem>();
        boolean formalityHasNewStatus = false;

        if (inFormaliteGuichetUnique.getIsAnnualAccounts() != null && inFormaliteGuichetUnique.getIsAnnualAccounts()) {
            formaliteGuichetUnique = guichetUniqueDelegateService
                    .getAnnualAccountById(inFormaliteGuichetUnique.getId());
            formaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getAnnualAccountStatusHistoriesById(inFormaliteGuichetUnique.getId());
        } else if (inFormaliteGuichetUnique.getIsActeDeposit() != null && inFormaliteGuichetUnique.getIsActeDeposit()) {
            formaliteGuichetUnique = guichetUniqueDelegateService
                    .getActeDepositById(inFormaliteGuichetUnique.getId());
            formaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getActeDepositStatusHistoriesById(inFormaliteGuichetUnique.getId());
        } else {
            formaliteGuichetUnique = guichetUniqueDelegateService.getFormalityById(inFormaliteGuichetUnique.getId());
            formaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getFormalityStatusHistoriesById(inFormaliteGuichetUnique.getId());
        }

        if (formaliteGuichetUnique.getValidationsRequests() != null)
            for (ValidationRequest validationRequest : formaliteGuichetUnique.getValidationsRequests()) {
                if (validationRequest.getPartnerCenter() != null)
                    partnerCenterRepository.save(validationRequest.getPartnerCenter());
            }

        // if (formaliteGuichetUnique.getFormaliteStatusHistoryItems() != null)
        // for (FormaliteStatusHistoryItem statusHistoryItem : formaliteGuichetUnique
        // .getFormaliteStatusHistoryItems()) {
        // if (statusHistoryItem.getPartnerCenter() != null)
        // partnerCenterRepository.save(statusHistoryItem.getPartnerCenter());
        // }

        FormaliteGuichetUnique originalFormalite = getFormaliteGuichetUnique(inFormaliteGuichetUnique.getId());

        if (formalite != null && formalite.getId() != null) {
            if (originalFormalite == null) {
                // Save only if cart > €
                ArrayList<Cart> carts = new ArrayList<Cart>();
                if (formaliteGuichetUnique.getCarts() != null)
                    for (Cart cart : formaliteGuichetUnique.getCarts())
                        if (cart.getTotal() != 0) {
                            carts.add(cart);
                        }

                formaliteGuichetUnique.setCarts(carts);
                originalFormalite = addOrUpdateFormaliteGuichetUnique(formaliteGuichetUnique);
                if (originalFormalite.getCarts() != null)
                    for (Cart cart : originalFormalite.getCarts()) {
                        if (cart.getInvoice() == null && cart.getTotal() != 0)
                            if (cart.getStatus().equals(cartStatusPayed)) {
                                cart.setInvoice(generateInvoiceFromCart(cart, formalite.getProvision().get(0)));
                            } else if (cart.getStatus().equals(cartStatusRefund)) {
                                cart.setInvoice((generateCreditNoteFromCart(cart, formalite.getProvision().get(0))));
                            }
                        cart.setFormaliteGuichetUnique(originalFormalite);
                    }
            } else if (originalFormalite != null) {
                // update only wanted field
                // Status field
                if (!originalFormalite.getStatus().getCode().equals(formaliteGuichetUnique.getStatus().getCode())) {
                    originalFormalite.setStatus(formaliteGuichetUnique.getStatus());
                    originalFormalite.setIsAuthorizedToSign(false);
                    formalityHasNewStatus = true;
                    addOrUpdateFormaliteGuichetUnique(originalFormalite);

                    if (originalFormalite.getFormalite() != null) {
                        notificationService.notifyGuichetUniqueFormaliteStatus(
                                originalFormalite.getFormalite().getProvision().get(0), originalFormalite);
                    }
                }

                // Cart field
                if (formaliteGuichetUnique.getCarts() != null && formaliteGuichetUnique.getCarts().size() > 0) {
                    if (originalFormalite.getCarts() == null || originalFormalite.getCarts().size() == 0) {
                        originalFormalite.setCarts(new ArrayList<Cart>());
                        for (Cart currentCart : formaliteGuichetUnique.getCarts()) {
                            // Save only if cart > €
                            if (currentCart.getTotal() != 0) {
                                currentCart.setFormaliteGuichetUnique(originalFormalite);
                                if (currentCart.getCartRates() != null)
                                    for (CartRate cartRate : currentCart.getCartRates())
                                        cartRate.setCart(currentCart);
                                originalFormalite.getCarts().add(currentCart);
                            }
                        }
                    } else {
                        ArrayList<Cart> cartsToReplace = new ArrayList<Cart>();
                        for (Cart currentCart : formaliteGuichetUnique.getCarts()) {
                            boolean found = false;
                            for (Cart originalCart : originalFormalite.getCarts()) {
                                if (originalCart.getId().equals(currentCart.getId())) {
                                    if (!originalCart.getStatus().equals(currentCart.getStatus())
                                            && originalCart.getInvoice() == null)
                                        cartsToReplace.add(currentCart);
                                    found = true;
                                }
                            }
                            if (!found) {
                                currentCart.setFormaliteGuichetUnique(originalFormalite);
                                if (currentCart.getCartRates() != null)
                                    for (CartRate cartRate : currentCart.getCartRates())
                                        cartRate.setCart(currentCart);
                                originalFormalite.getCarts().add(currentCart);
                                currentCart.setFormaliteGuichetUnique(originalFormalite);
                            }
                        }

                        if (cartsToReplace != null) {
                            ArrayList<Cart> finalCarts = new ArrayList<Cart>();
                            boolean found = false;
                            for (Cart cart : originalFormalite.getCarts()) {
                                for (Cart cartToReplace : cartsToReplace) {
                                    if (cart.getId().equals(cartToReplace.getId()))
                                        found = true;
                                }
                                if (!found)
                                    finalCarts.add(cart);
                            }
                            finalCarts.addAll(cartsToReplace);
                            originalFormalite.setCarts(finalCarts);
                            for (Cart cart : originalFormalite.getCarts())
                                cart.setFormaliteGuichetUnique(originalFormalite);
                        }

                        originalFormalite = addOrUpdateFormaliteGuichetUnique(originalFormalite);

                        if (generateInvoices)
                            for (Cart currentCart : originalFormalite.getCarts()) {
                                if (currentCart.getInvoice() == null
                                        && currentCart.getFormaliteGuichetUnique().getFormalite() != null
                                        && currentCart.getFormaliteGuichetUnique().getFormalite()
                                                .getProvision() != null) {
                                    if (currentCart.getTotal() != 0)
                                        if (currentCart.getStatus().equals(cartStatusPayed)) {
                                            currentCart.setInvoice(generateInvoiceFromCart(currentCart,
                                                    currentCart.getFormaliteGuichetUnique().getFormalite()
                                                            .getProvision()
                                                            .get(0)));
                                        } else if (currentCart.getStatus().equals(cartStatusRefund)) {
                                            currentCart.setInvoice((generateCreditNoteFromCart(currentCart,
                                                    currentCart.getFormaliteGuichetUnique().getFormalite()
                                                            .getProvision()
                                                            .get(0))));
                                        }
                                }
                            }
                    }
                }

                // Content field
                originalFormalite.setContent(formaliteGuichetUnique.getContent());
            }

            // Download attachments
            originalFormalite.getContent().setPiecesJointes(getAttachmentOfFormaliteGuichetUnique(originalFormalite));
            if (originalFormalite.getContent().getPiecesJointes() != null
                    && originalFormalite.getContent().getPiecesJointes().size() > 0)
                for (PiecesJointe piecesJointe : originalFormalite.getContent().getPiecesJointes())
                    piecesJointe.setContent(originalFormalite.getContent());

            originalFormalite = addOrUpdateFormaliteGuichetUnique(originalFormalite);
            List<Provision> provisions = formaliteService.getFormalite(formalite.getId()).getProvision();

            if (provisions != null && originalFormalite.getContent().getPiecesJointes() != null
                    && originalFormalite.getContent().getPiecesJointes().size() > 0) {
                List<TypeDocument> typeDocuments = typeDocumentService.getTypeDocument();
                List<String> typeDocumentsToDownload = new ArrayList<String>();
                if (typeDocuments != null)
                    for (TypeDocument typeDocument : typeDocuments)
                        if (typeDocument.getIsToDownloadOnProvision() != null
                                && typeDocument.getIsToDownloadOnProvision())
                            typeDocumentsToDownload.add(typeDocument.getCode());

                if (typeDocumentsToDownload.size() > 0) {
                    for (PiecesJointe piecesJointe : originalFormalite.getContent().getPiecesJointes())
                        if (typeDocumentsToDownload.contains(piecesJointe.getTypeDocument().getCode())) {
                            downloadPieceJointeOnProvision(provisions.get(0), piecesJointe);
                        }
                }
            }
        }

        if (originalFormalite != null) {
            originalFormalite = addOrUpdateFormaliteGuichetUnique(originalFormalite);
            // validationsRequests field
            originalFormalite.setValidationsRequests(formaliteGuichetUnique.getValidationsRequests());
            if (originalFormalite.getValidationsRequests() != null)
                for (ValidationRequest validationRequest : originalFormalite.getValidationsRequests())
                    validationRequest.setFormaliteGuichetUnique(originalFormalite);

            // update status history items
            originalFormalite.setFormaliteStatusHistoryItems(formaliteStatusHistoryItems);
            for (FormaliteStatusHistoryItem formaliteStatusHistoryItem : originalFormalite
                    .getFormaliteStatusHistoryItems())
                formaliteStatusHistoryItem.setFormaliteGuichetUnique(originalFormalite);

            addOrUpdateFormaliteGuichetUnique(originalFormalite);

            // Update provision waiting AC field
            if (originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.VALIDATION_PENDING)
                    && originalFormalite.getFormalite() != null
                    && originalFormalite.getValidationsRequests() != null) {
                for (ValidationRequest validationRequest : originalFormalite.getValidationsRequests()) {
                    if (validationRequest.getStatus().getCode().equals(ValidationsRequestStatus.MSA_ACCEPTATION_PENDING)
                            || validationRequest.getStatus().getCode()
                                    .equals(ValidationsRequestStatus.VALIDATION_PENDING)) {
                        List<CompetentAuthority> competentAuthorities = competentAuthorityService
                                .getCompetentAuthorityByInpiReference(validationRequest.getPartnerCenter().getCode());

                        // Try with partner label
                        if ((competentAuthorities == null || competentAuthorities.size() == 0)
                                && validationRequest.getPartner() != null) {
                            competentAuthorities = competentAuthorityService
                                    .getCompetentAuthorityByInpiReference(
                                            validationRequest.getPartner().getLibelleCourt());
                        }
                        if (competentAuthorities != null && competentAuthorities.size() == 1) {
                            originalFormalite.getFormalite().setWaitedCompetentAuthority(competentAuthorities.get(0));
                            formaliteService.addOrUpdateFormalite(originalFormalite.getFormalite());
                            break;
                        }
                    }
                }
            }

            // Update formalite status based on GU status
            if (formalityHasNewStatus && originalFormalite.getFormalite() != null) {
                if (originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.AMENDMENT_PENDING)
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.ERROR_INSEE_EXISTS_PM)
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.ERROR_INSEE_EXISTS_PP)
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.ERROR_DECLARATION_INSEE)
                        || originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.ERROR)
                        || originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.EXPIRED)
                        || originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.REJECTED)) {
                    originalFormalite.getFormalite().setFormaliteStatus(formaliteStatusService
                            .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_REJECTED));
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            originalFormalite.getFormalite()
                                    .getProvision().get(0).getService().getAssoAffaireOrder().getCustomerOrder(),
                            "Formalité GU n°" + originalFormalite.getLiasseNumber() + " rejetée ("
                                    + formaliteGuichetUniqueStatusService
                                            .getFormaliteGuichetUniqueStatus(originalFormalite.getStatus().getCode())
                                            .getLabel()
                                    + ")");

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFormalites());

                } else if (originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.VALIDATED_DGFIP)
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.VALIDATED_PARTNER)
                        || originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.VALIDATED)) {
                    originalFormalite.getFormalite().setFormaliteStatus(formaliteStatusService
                            .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_VALIDATED));
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            originalFormalite.getFormalite()
                                    .getProvision().get(0).getService().getAssoAffaireOrder().getCustomerOrder(),
                            "Formalité GU n°" + originalFormalite.getLiasseNumber() + " validée");

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFormalites());
                }
                formaliteService.addOrUpdateFormalite(originalFormalite.getFormalite());
            }

            if (formalite != null) {
                originalFormalite.setFormalite(formalite);
                addOrUpdateFormaliteGuichetUnique(originalFormalite);
            }
        }

        if (formalite != null && originalFormalite != null
                && (originalFormalite.getStatus().getCode().equals(FormaliteGuichetUniqueStatus.SIGNATURE_PENDING)
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.AMENDMENT_SIGNATURE_PENDING)
                                && originalFormalite.getIsAuthorizedToSign() != null
                                && originalFormalite.getIsAuthorizedToSign()))
            batchService.declareNewBatch(Batch.SIGN_FORMALITE_GUICHET_UNIQUE, originalFormalite.getId());

        if (formalite != null && originalFormalite != null
                && (Arrays
                        .asList(FormaliteGuichetUniqueStatus.PAYMENT_PENDING,
                                FormaliteGuichetUniqueStatus.PAYMENT_VALIDATION_PENDING,
                                FormaliteGuichetUniqueStatus.AMENDMENT_PAYMENT_PENDING,
                                FormaliteGuichetUniqueStatus.AMENDMENT_PAYMENT_VALIDATION_PENDING)
                        .contains(originalFormalite.getStatus().getCode())
                        || originalFormalite.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.AMENDMENT_PENDING)
                                && originalFormalite.getIsAuthorizedToSign() != null
                                && originalFormalite.getIsAuthorizedToSign()))
            batchService.declareNewBatch(Batch.PAY_FORMALITE_GUICHET_UNIQUE, originalFormalite.getId());

        return originalFormalite;
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

        if (cart.getPaymentType() == null && cart.getStatus().equals(cartStatusRefund))
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