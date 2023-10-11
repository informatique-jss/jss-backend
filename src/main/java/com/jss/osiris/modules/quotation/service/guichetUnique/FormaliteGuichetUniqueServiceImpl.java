package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentVatSettingService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PartnerCenterRepository;
import com.jss.osiris.modules.quotation.service.FormaliteService;
import com.jss.osiris.modules.quotation.service.PricingHelper;

@Service
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
    BillingItemService billingItemService;

    @Autowired
    FormaliteService formaliteService;

    @Autowired
    PartnerCenterRepository partnerCenterRepository;

    @Autowired
    DepartmentVatSettingService departmentVatSettingService;

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
    @Transactional(rollbackFor = Exception.class)
    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(FormaliteGuichetUnique inFormaliteGuichetUnique,
            Formalite formalite)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (inFormaliteGuichetUnique == null)
            throw new OsirisValidationException("inFormaliteGuichetUnique");

        if (formalite != null && formalite.getId() != null)
            formalite = formaliteService.getFormalite(formalite.getId());
        FormaliteGuichetUnique formaliteGuichetUnique;
        List<FormaliteStatusHistoryItem> formaliteStatusHistoryItems = new ArrayList<FormaliteStatusHistoryItem>();

        if (inFormaliteGuichetUnique.getIsAnnualAccounts() != null && inFormaliteGuichetUnique.getIsAnnualAccounts()) {
            formaliteGuichetUnique = guichetUniqueDelegateService
                    .getAnnualAccountById(inFormaliteGuichetUnique.getId());
            formaliteStatusHistoryItems = guichetUniqueDelegateService
                    .getAnnualAccountStatusHistoriesById(inFormaliteGuichetUnique.getId());
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

        FormaliteGuichetUnique originalFormalite = getFormaliteGuichetUnique(inFormaliteGuichetUnique.getId());

        if (originalFormalite == null && formalite != null && formalite.getId() != null) {
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
                    if (cart.getInvoice() == null)
                        if (cart.getStatus().equals(cartStatusPayed)) {
                            cart.setInvoice(generateInvoiceFromCart(cart, formalite.getProvision().get(0)));
                        } else if (cart.getStatus().equals(cartStatusRefund)) {
                            cart.setInvoice((generateCreditNoteFromCart(cart, formalite.getProvision().get(0))));
                        }
                    cart.setFormaliteGuichetUnique(originalFormalite);
                }
        } else if (originalFormalite != null && formalite != null && formalite.getId() != null) {
            // update only wanted field
            // Status field
            if (!originalFormalite.getStatus().getCode().equals(formaliteGuichetUnique.getStatus().getCode())) {
                originalFormalite.setStatus(formaliteGuichetUnique.getStatus());

                if (originalFormalite.getFormalite() != null)
                    notificationService.notifyGuichetUniqueFormaliteStatus(
                            originalFormalite.getFormalite().getProvision().get(0), originalFormalite);
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

                    for (Cart currentCart : originalFormalite.getCarts()) {
                        if (currentCart.getInvoice() == null
                                && currentCart.getFormaliteGuichetUnique().getFormalite() != null
                                && currentCart.getFormaliteGuichetUnique().getFormalite().getProvision() != null) {
                            if (currentCart.getStatus().equals(cartStatusPayed)) {
                                currentCart.setInvoice(generateInvoiceFromCart(currentCart,
                                        currentCart.getFormaliteGuichetUnique().getFormalite().getProvision()
                                                .get(0)));
                            } else if (currentCart.getStatus().equals(cartStatusRefund)) {
                                currentCart.setInvoice((generateCreditNoteFromCart(currentCart,
                                        currentCart.getFormaliteGuichetUnique().getFormalite().getProvision()
                                                .get(0))));
                            }
                        }
                    }
                }
            }
        }

        originalFormalite = addOrUpdateFormaliteGuichetUnique(originalFormalite);
        // validationsRequests field
        originalFormalite.setValidationsRequests(formaliteGuichetUnique.getValidationsRequests());
        if (originalFormalite.getValidationsRequests() != null)
            for (ValidationRequest validationRequest : originalFormalite.getValidationsRequests())
                validationRequest.setFormaliteGuichetUnique(originalFormalite);

        // update status history items
        originalFormalite.setFormaliteStatusHistoryItems(formaliteStatusHistoryItems);
        for (FormaliteStatusHistoryItem formaliteStatusHistoryItem : originalFormalite.getFormaliteStatusHistoryItems())
            formaliteStatusHistoryItem.setFormaliteGuichetUnique(originalFormalite);

        addOrUpdateFormaliteGuichetUnique(originalFormalite);

        if (formalite != null) {
            originalFormalite.setFormalite(formalite);
            addOrUpdateFormaliteGuichetUnique(originalFormalite);
        }
        return originalFormalite;
    }

    private Invoice generateInvoiceFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException,
            OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getAssoAffaireOrder().getCustomerOrder());
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
            if (asso.getId().equals(provision.getAssoAffaireOrder().getId()))
                for (Provision inProvision : asso.getProvisions()) {
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
            OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getAssoAffaireOrder().getCustomerOrder());
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
            if (asso.getId().equals(provision.getAssoAffaireOrder().getId()))
                for (Provision inProvision : asso.getProvisions()) {
                    if (inProvision.getInvoiceItems() == null)
                        inProvision.setInvoiceItems(new ArrayList<InvoiceItem>());

                    if (provision.getId().equals(inProvision.getId())) {
                        cart.getCartRates()
                                .sort((o1, o2) -> ((Long) o1.getAmount()).compareTo((Long) (o2.getAmount())));
                        InvoiceItem firstItem = null;
                        for (CartRate cartRate : cart.getCartRates()) {
                            if (cartRate.getRate() != null && cartRate.getAmount() != 0) {
                                if (cartRate.getAmount() > 0 && firstItem != null) {
                                    firstItem.setPreTaxPrice(
                                            firstItem.getPreTaxPrice() - Math.abs(cartRate.getHtAmount() / 100f));
                                    firstItem.setPreTaxPriceReinvoiced(
                                            -Math.abs(firstItem.getPreTaxPrice()));
                                } else {
                                    InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                                    invoiceItem.setPreTaxPrice(Math.abs(invoiceItem.getPreTaxPrice()));
                                    invoiceItem.setPreTaxPriceReinvoiced(
                                            -Math.abs(invoiceItem.getPreTaxPrice()));
                                    invoiceItem.setProvision(null);
                                    invoice.getInvoiceItems().add(invoiceItem);
                                    provision.getInvoiceItems().add(invoiceItem);
                                    if (firstItem == null)
                                        firstItem = invoiceItem;
                                }
                            }
                        }
                    }
                }

        invoice.setIsInvoiceFromProvider(false);
        invoice.setIsProviderCreditNote(true);
        invoice.setProvision(provision);
        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
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
        if (competentAuthorities == null || competentAuthorities.size() > 1)
            competentAuthority = constantService.getCompetentAuthorityInpi();
        else
            competentAuthority = competentAuthorities.get(0);

        invoiceItem.setLabel(competentAuthority.getLabel() + " - " + cartRate.getRate().getLabel());
        long amount = cartRate.getHtAmount();
        if (amount == 0)
            amount = cartRate.getAmount();

        if (cartRate.getAmount() < 0 && amount > 0)
            amount = -amount;
        if (cartRate.getAmount() > 0 && amount < 0)
            amount = -amount;

        invoiceItem.setPreTaxPrice(Float.parseFloat(amount + "") / 100f);
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());

        return invoiceItem;
    }

    private void extractVatFromCartRate(InvoiceItem invoiceItem, CartRate cartRate) throws OsirisException {
        List<BillingItem> deboursBillingItem;

        if (Math.abs(cartRate.getAmount()) == Math.abs(cartRate.getHtAmount())) {
            deboursBillingItem = billingItemService
                    .getBillingItemByBillingType(constantService.getBillingTypeDeboursNonTaxable());
            invoiceItem.setVat(constantService.getVatZero());
            invoiceItem.setVatPrice(0f);
        } else {
            deboursBillingItem = billingItemService
                    .getBillingItemByBillingType(constantService.getBillingTypeEmolumentsDeGreffeDebour());

            Float vatRate = (cartRate.getAmount() - cartRate.getHtAmount()) * 1.0f / cartRate.getHtAmount() * 100f;
            vatRate = Math.round(vatRate * 10f) / 10f;
            Vat vat = null;

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

        invoiceItem.setBillingItem(pricingHelper.getAppliableBillingItem(deboursBillingItem));
    }

    private boolean isVatEqual(Float vat1, Float vat2) {
        return Math.abs(vat1 - vat2) < 1;
    }
}