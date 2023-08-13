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
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
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

        FormaliteGuichetUnique formaliteGuichetUnique;
        if (inFormaliteGuichetUnique.getIsAnnualAccounts() != null && inFormaliteGuichetUnique.getIsAnnualAccounts())
            formaliteGuichetUnique = guichetUniqueDelegateService
                    .getAnnualAccountById(inFormaliteGuichetUnique.getId());
        else
            formaliteGuichetUnique = guichetUniqueDelegateService.getFormalityById(inFormaliteGuichetUnique.getId());

        FormaliteGuichetUnique originalFormalite = getFormaliteGuichetUnique(inFormaliteGuichetUnique.getId());

        if (originalFormalite == null) {
            originalFormalite = addOrUpdateFormaliteGuichetUnique(formaliteGuichetUnique);
        } else {
            // update only wanted field
            if (!originalFormalite.getStatus().getCode().equals(formaliteGuichetUnique.getStatus().getCode())) {
                originalFormalite.setStatus(formaliteGuichetUnique.getStatus());

                if (originalFormalite.getFormalite() != null)
                    notificationService.notifyGuichetUniqueFormaliteStatus(
                            originalFormalite.getFormalite().getProvision().get(0), originalFormalite);
            }
            if (formaliteGuichetUnique.getCarts() != null && formaliteGuichetUnique.getCarts().size() > 0) {
                if (originalFormalite.getCarts() == null || originalFormalite.getCarts().size() == 0) {
                    originalFormalite.setCarts(new ArrayList<Cart>());
                    for (Cart currentCart : formaliteGuichetUnique.getCarts()) {
                        currentCart.setFormaliteGuichetUnique(originalFormalite);
                        if (currentCart.getCartRates() != null)
                            for (CartRate cartRate : currentCart.getCartRates())
                                cartRate.setCart(currentCart);
                        originalFormalite.getCarts().add(currentCart);
                    }
                } else {
                    for (Cart currentCart : formaliteGuichetUnique.getCarts()) {
                        boolean found = false;
                        for (Cart originalCart : originalFormalite.getCarts()) {
                            if (originalCart.getId().equals(currentCart.getId()))
                                found = true;
                        }
                        if (!found) {
                            currentCart.setFormaliteGuichetUnique(originalFormalite);
                            if (currentCart.getCartRates() != null)
                                for (CartRate cartRate : currentCart.getCartRates())
                                    cartRate.setCart(currentCart);
                            originalFormalite.getCarts().add(currentCart);
                        }
                    }
                }
            }
            addOrUpdateFormaliteGuichetUnique(originalFormalite);

            if (originalFormalite != null && originalFormalite.getCarts() != null
                    && originalFormalite.getCarts().size() > 0)
                for (Cart cart : originalFormalite.getCarts()) {
                    if (cart.getInvoice() == null
                            && cart.getFormaliteGuichetUnique().getFormalite() != null
                            && cart.getFormaliteGuichetUnique().getFormalite().getProvision() != null) {
                        if (cart.getStatus().equals(cartStatusPayed)) {
                            cart.setInvoice(generateInvoiceFromCart(cart,
                                    cart.getFormaliteGuichetUnique().getFormalite().getProvision().get(0)));
                        } else if (cart.getStatus().equals(cartStatusRefund)) {
                            cart.setInvoice((generateCreditNoteFromCart(cart,
                                    cart.getFormaliteGuichetUnique().getFormalite().getProvision().get(0))));
                        }
                    }
                }
        }

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
                            invoiceItem.setProvision(provision);
                            provision.getInvoiceItems().add(invoiceItem);
                        }
                    }
                }

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
                        for (CartRate cartRate : cart.getCartRates()) {
                            InvoiceItem invoiceItem = getInvoiceItemForCartRate(cartRate, cart);
                            invoiceItem.setProvision(provision);
                            provision.getInvoiceItems().add(invoiceItem);
                        }
                    }
                }

        invoice.setIsInvoiceFromProvider(false);
        invoice.setIsProviderCreditNote(true);
        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
    }

    private InvoiceItem getInvoiceItemForCartRate(CartRate cartRate, Cart cart) throws OsirisException {
        List<BillingItem> deboursBillingItem = billingItemService
                .getBillingItemByBillingType(constantService.getBillingTypeEmolumentsDeGreffeDebour());

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(pricingHelper.getAppliableBillingItem(deboursBillingItem));
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
        invoiceItem.setPreTaxPrice(Float.parseFloat(cartRate.getAmount() + "") / 100f);
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());

        return invoiceItem;
    }
}