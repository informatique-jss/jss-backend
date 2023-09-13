package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.ProvisionService;

@Service
public class FormaliteGuichetUniqueServiceImpl implements FormaliteGuichetUniqueService {

    @Autowired
    FormaliteGuichetUniqueRepository formaliteGuichetUniqueRepository;

    @Autowired
    GuichetUniqueDelegateService guichetUniqueDelegateService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    DebourService debourService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    NotificationService notificationService;

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
        boolean cartChange = false;

        if (originalFormalite == null) {
            originalFormalite = addOrUpdateFormaliteGuichetUnique(formaliteGuichetUnique);
            if (originalFormalite.getCarts() != null && originalFormalite.getCarts().size() > 0)
                cartChange = true;
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
                    cartChange = true;
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
                            cartChange = true;
                        }
                    }
                }
            }
            addOrUpdateFormaliteGuichetUnique(originalFormalite);

            if (cartChange)
                refreshDeboursFromFormalite(originalFormalite);

            if (originalFormalite != null && originalFormalite.getCarts() != null
                    && originalFormalite.getCarts().size() > 0)
                for (Cart cart : originalFormalite.getCarts()) {
                    if (cart.getInvoice() == null
                            && cart.getFormaliteGuichetUnique().getFormalite() != null
                            && cart.getFormaliteGuichetUnique().getFormalite().getProvision() != null) {
                        if (cart.getStatus().equals(cartStatusPayed))
                            cart.setInvoice(generateInvoiceFromCart(cart,
                                    cart.getFormaliteGuichetUnique().getFormalite().getProvision().get(0)));
                        else if (cart.getStatus().equals(cartStatusRefund))
                            cart.setInvoice((generateCreditNoteFromCart(cart,
                                    cart.getFormaliteGuichetUnique().getFormalite().getProvision().get(0))));
                    }
                }
        }

        if (formalite != null) {
            originalFormalite.setFormalite(formalite);
            addOrUpdateFormaliteGuichetUnique(originalFormalite);
        }
        return originalFormalite;
    }

    private void refreshDeboursFromFormalite(FormaliteGuichetUnique formalite)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (formalite != null && formalite.getCarts() != null && formalite.getCarts().size() > 0)
            for (Cart cart : formalite.getCarts()) {
                if ((cart.getStatus().equals(cartStatusPayed) || cart.getStatus().equals(cartStatusRefund))
                        && cart.getFormaliteGuichetUnique().getFormalite() != null
                        && cart.getFormaliteGuichetUnique().getFormalite().getProvision() != null
                        && cart.getFormaliteGuichetUnique().getFormalite().getProvision().size() > 0) {
                    Provision provision = cart.getFormaliteGuichetUnique().getFormalite().getProvision().get(0);
                    if (cart.getTotal() != 0 && cart.getCartRates() != null && cart.getCartRates().size() > 0
                            && (cart.getCartRates().get(0).getDebours() == null
                                    || cart.getCartRates().get(0).getDebours().size() == 0)) {
                        if (cart.getStatus().equals(cartStatusPayed)) {
                            if (cart.getCartRates() != null) {
                                for (CartRate cartRate : cart.getCartRates()) {
                                    if (cartRate.getAmount() != 0) {
                                        Debour newDebour = getDebourFromCart(cart, cartRate, provision);
                                        if (provision.getDebours() == null)
                                            provision.setDebours(new ArrayList<Debour>());
                                        provision.getDebours().add(newDebour);
                                        provisionService.addOrUpdateProvision(provision);
                                    }
                                }
                            }
                        } else if (cart.getStatus().equals(cartStatusRefund)) {
                            // Aggregate CartRate on first one to take account of positive / negatif rates
                            // possibles
                            if (cart.getCartRates() != null) {
                                CartRate cartRateFinal = null;
                                for (CartRate cartRate : cart.getCartRates()) {
                                    if (cartRate.getAmount() != 0) {
                                        if (cartRateFinal == null)
                                            cartRateFinal = cartRate;
                                        else
                                            cartRateFinal
                                                    .setAmount(cartRateFinal.getAmount() + cartRate.getAmount());
                                    }
                                }
                                Debour newDebour = getDebourFromCart(cart, cartRateFinal, provision);
                                provision.setDebours(new ArrayList<Debour>());
                                provision.getDebours().add(newDebour);
                                provisionService.addOrUpdateProvision(provision);
                            }
                        }
                    }
                }
            }
    }

    private Debour getDebourFromCart(Cart cart, CartRate cartRate, Provision provision)
            throws OsirisValidationException, OsirisException {
        Debour newDebour = new Debour();
        newDebour.setBillingType(constantService.getBillingTypeEmolumentsDeGreffeDebour());
        newDebour.setComments(cartRate.getRate().getLabel());

        List<CompetentAuthority> competentAuthorities = competentAuthorityService
                .getCompetentAuthorityByInpiReference(cartRate.getRecipientCode());

        CompetentAuthority competentAuthority;
        if (competentAuthorities == null || competentAuthorities.size() > 1)
            competentAuthority = constantService.getCompetentAuthorityInpi();
        else
            competentAuthority = competentAuthorities.get(0);

        newDebour.setCompetentAuthority(competentAuthority);
        newDebour.setDebourAmount(Float.parseFloat(cartRate.getAmount() + "") / 100f);
        newDebour.setInvoicedAmount(newDebour.getDebourAmount());
        newDebour
                .setPaymentDateTime(
                        LocalDateTime.parse(cart.getPaymentDate() != null ? cart.getPaymentDate() : cart.getUpdated(),
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        PaymentType paymentType = null;
        if (cart.getPaymentType() == null && cart.getStatus().equals(cartStatusRefund))
            paymentType = constantService.getPaymentTypeAccount();
        else
            paymentType = paymentTypeService.getPaymentTypeByCodeInpi(cart.getPaymentType());

        if (paymentType == null)
            throw new OsirisValidationException("Unable to find payment type for INPI code "
                    + cart.getPaymentType() + ". Please fill referential with correct value");

        newDebour.setPaymentType(paymentType);
        newDebour.setProvision(provision);
        newDebour.setCartRate(cartRate);
        return debourService.addOrUpdateDebour(newDebour);
    }

    private Invoice generateInvoiceFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" + cart.getId());
        invoice.setIsInvoiceFromProvider(true);

        PaymentType paymentType = paymentTypeService.getPaymentTypeByCodeInpi(cart.getPaymentType());

        if (paymentType == null)
            throw new OsirisValidationException("Unable to find payment type for INPI code "
                    + cart.getPaymentType() + ". Please fill referential with correct value");
        invoice.setManualPaymentType(paymentType);

        invoice.setManualAccountingDocumentDate(
                LocalDate.parse(cart.getPaymentDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            if (asso.getId().equals(provision.getAssoAffaireOrder().getId()))
                for (Provision inProvision : asso.getProvisions()) {
                    if (provision.getId().equals(inProvision.getId())) {
                        List<Debour> debours = debourService.getDeboursForProvision(provision);
                        if (debours != null && debours.size() > 0)
                            for (Debour debour : debours) {
                                for (CartRate cartRate : cart.getCartRates()) {
                                    if (debour.getCartRate() != null
                                            && debour.getCartRate().getId().equals(cartRate.getId())) {
                                        if (cartRate.getAmount() == cartRate.getHtAmount())
                                            debour.setNonTaxableAmount(cartRate.getAmount() / 100f);
                                        else
                                            debour.setNonTaxableAmount(0f);
                                        debourService.addOrUpdateDebour(debour);
                                    }
                                }
                            }
                    }
                }

        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
    }

    private Invoice generateCreditNoteFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" + cart.getId());

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
                LocalDate.parse(cart.getPaymentDate() != null ? cart.getPaymentDate() : cart.getUpdated(),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            if (asso.getId().equals(provision.getAssoAffaireOrder().getId()))
                for (Provision inProvision : asso.getProvisions()) {
                    if (provision.getId().equals(inProvision.getId())) {
                        List<Debour> debours = debourService.getDeboursForProvision(provision);
                        if (debours != null && debours.size() > 0)
                            for (Debour debour : debours) {
                                for (CartRate cartRate : cart.getCartRates()) {
                                    if (debour.getCartRate() != null
                                            && debour.getCartRate().getId().equals(cartRate.getId())) {
                                        debour.setNonTaxableAmount(0f);
                                    }
                                }
                            }
                    }
                }

        return invoiceService.generateInvoiceCreditNote(invoice, null);
    }
}
