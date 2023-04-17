package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.guichetUnique.Cart;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.GuichetUniqueDelegateService;
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

    @Override
    public FormaliteGuichetUnique getFormaliteGuichetUnique(Integer id) {
        Optional<FormaliteGuichetUnique> formaliteGuichetUnique = formaliteGuichetUniqueRepository.findById(id);
        if (formaliteGuichetUnique.isPresent())
            return formaliteGuichetUnique.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(Integer id, Employee employee)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (employee == null)
            throw new OsirisValidationException("Employee");
        if (id == null)
            throw new OsirisValidationException("id");

        FormaliteGuichetUnique formaliteGuichetUnique = guichetUniqueDelegateService.getFormalityById(id, employee);
        FormaliteGuichetUnique originalFormalite = getFormaliteGuichetUnique(id);
        boolean cartChange = false;

        if (originalFormalite == null) {
            originalFormalite = formaliteGuichetUniqueRepository.save(formaliteGuichetUnique);
            if (originalFormalite.getCarts() != null && originalFormalite.getCarts().size() > 0)
                cartChange = true;
        } else {
            // update only wanted field
            if (!originalFormalite.getStatus().getCode().equals(formaliteGuichetUnique.getStatus().getCode())) {
                originalFormalite.setStatus(formaliteGuichetUnique.getStatus());

                if (originalFormalite.getFormalites() != null && originalFormalite.getFormalites().size() > 0)
                    notificationService.notifyGuichetUniqueFormaliteStatus(
                            originalFormalite.getFormalites().get(0).getProvision().get(0));
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
            formaliteGuichetUniqueRepository.save(originalFormalite);

            if (cartChange)
                refreshDeboursFromFormalite(originalFormalite);

            if (originalFormalite != null && originalFormalite.getCarts() != null
                    && originalFormalite.getCarts().size() > 0)
                for (Cart cart : originalFormalite.getCarts()) {
                    if (cart.getStatus().equals("PAID") && cart.getInvoice() == null) {
                        cart.setInvoice(generateInvoiceFromCart(cart,
                                cart.getFormaliteGuichetUnique().getFormalites().get(0).getProvision().get(0)));
                    }
                }
        }
        return originalFormalite;
    }

    private void refreshDeboursFromFormalite(FormaliteGuichetUnique formalite)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (formalite != null && formalite.getCarts() != null && formalite.getCarts().size() > 0)
            for (Cart cart : formalite.getCarts()) {
                Provision provision = cart.getFormaliteGuichetUnique().getFormalites().get(0).getProvision().get(0);
                if (cart.getCartRates() != null && cart.getCartRates().size() > 0
                        && (cart.getCartRates().get(0).getDebours() == null
                                || cart.getCartRates().get(0).getDebours().size() == 0)) {
                    if (cart.getCartRates() != null)
                        for (CartRate cartRate : cart.getCartRates()) {
                            if (cartRate.getAmount() > 0) {
                                Debour newDebour = getDebourFromCart(cart, cartRate, provision);
                                if (provision.getDebours() == null)
                                    provision.setDebours(new ArrayList<Debour>());
                                provision.getDebours().add(newDebour);
                                provisionService.addOrUpdateProvision(provision);
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

        CompetentAuthority competentAuthority = competentAuthorityService
                .getCompetentAuthorityByInpiReference(cartRate.getRecipientCode());

        if (competentAuthority == null)
            throw new OsirisValidationException("Unable to find competent autority for INPI code "
                    + cartRate.getRecipientCode() + ". Please fill referential with correct value");

        newDebour.setCompetentAuthority(competentAuthority);
        newDebour.setDebourAmount(Float.parseFloat(cartRate.getAmount() + "") / 100f);
        newDebour.setInvoicedAmount(newDebour.getDebourAmount());
        newDebour
                .setPaymentDateTime(LocalDateTime.parse(cart.getPaymentDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        PaymentType paymentType = paymentTypeService.getPaymentTypeByCodeInpi(cart.getPaymentType());

        if (paymentType == null)
            throw new OsirisValidationException("Unable to find payment type for INPI code "
                    + cart.getPaymentType() + ". Please fill referential with correct value");

        newDebour.setPaymentType(paymentType);
        newDebour.setProvision(provision);
        newDebour.setCartRate(cartRate);
        return debourService.addOrUpdateDebour(newDebour);
    }

    private Invoice generateInvoiceFromCart(Cart cart, Provision provision)
            throws OsirisException, OsirisClientMessageException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInpi());
        invoice.setCustomerOrderForInboundInvoice(provision.getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(cart.getMipOrderNum() + "/" + cart.getId());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setManualAccountingDocumentDate(
                LocalDate.parse(cart.getPaymentDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            if (asso.getId().equals(provision.getAssoAffaireOrder().getId()))
                for (Provision inProvision : asso.getProvisions())
                    if (provision.getId().equals(inProvision.getId()) && inProvision.getDebours() != null
                            && inProvision.getDebours().size() > 0)
                        for (Debour debour : inProvision.getDebours()) {
                            for (CartRate cartRate : cart.getCartRates()) {
                                if (debour.getCartRate() != null
                                        && debour.getCartRate().getId().equals(cartRate.getId())) {
                                    debour.setNonTaxableAmount(0f);
                                }
                            }
                        }

        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
    }
}
