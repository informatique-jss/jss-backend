package com.jss.osiris.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
import com.jss.osiris.modules.tiers.model.IRffCompute;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.RffSearch;
import com.jss.osiris.modules.tiers.repository.RffRepository;

@Service
public class RffServiceImpl implements RffService {

    @Autowired
    RffRepository rffRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersService tiersService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    VatService vatService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    PricingHelper pricingHelper;

    @Override
    public Rff getRff(Integer id) {
        Optional<Rff> rff = rffRepository.findById(id);
        if (rff.isPresent())
            return rff.get();
        return null;
    }

    @Override
    public List<Rff> getRffs(RffSearch rffSearch) throws OsirisException {
        Integer idTiers = 0;
        if (rffSearch.getTiers() != null)
            idTiers = rffSearch.getTiers().getEntityId();

        Integer idResponsable = 0;
        if (rffSearch.getResponsable() != null)
            idResponsable = rffSearch.getResponsable().getEntityId();

        Integer idSalesEmployee = 0;
        if (rffSearch.getSalesEmployee() != null)
            idSalesEmployee = rffSearch.getSalesEmployee().getId();

        List<IRffCompute> rffComputes = rffRepository.getRffComputes(constantService.getRffFrequencyAnnual().getId(),
                constantService.getRffFrequencyQuarterly().getId(), constantService.getRffFrequencyMonthly().getId(),
                idTiers, idResponsable, idSalesEmployee, rffSearch.getStartDate(), rffSearch.getEndDate());

        List<Rff> finalRffs = new ArrayList<Rff>();
        if (rffComputes != null)
            for (IRffCompute rffCompute : rffComputes) {
                Rff currentRff = new Rff();
                if (rffCompute.getRffId() != null) {
                    currentRff = getRff(rffCompute.getRffId());
                } else {
                    currentRff.setStartDate(rffCompute.getStartDate());
                    currentRff.setEndDate(rffCompute.getEndDate());
                    currentRff.setIsCancelled(false);
                    currentRff.setIsSent(false);
                    if (rffCompute.getResponsableId() != null)
                        currentRff.setResponsable(responsableService.getResponsable(rffCompute.getResponsableId()));
                    currentRff.setTiers(tiersService.getTiers(rffCompute.getTiersId()));
                    finalRffs.add(addOrUpdateRff(currentRff));
                }

                if (currentRff.getIsCancelled() == false && currentRff.getIsSent() == false) {
                    currentRff.setRffFormalite(rffCompute.getRffFor());
                    currentRff.setRffInsertion(rffCompute.getRffAl());
                    currentRff.setRffTotal(rffCompute.getRffAl() + rffCompute.getRffFor());
                }

                currentRff.setTiersId(currentRff.getTiers().getId());
                if (currentRff.getTiers().getDenomination() != null)
                    currentRff.setTiersLabel(currentRff.getTiers().getDenomination());
                else
                    currentRff.setTiersLabel(
                            currentRff.getTiers().getFirstname() + " " + currentRff.getTiers().getLastname());

                if (currentRff.getResponsable() != null) {
                    currentRff.setResponsableLabel(currentRff.getResponsable().getFirstname() + " "
                            + currentRff.getResponsable().getLastname());
                    currentRff.setResponsableId(currentRff.getResponsable().getId());
                }

                if (currentRff.getIsCancelled() == false && currentRff.getIsSent() == false) {
                    String iban = null;
                    String bic = null;
                    if (currentRff.getResponsable() != null && currentRff.getResponsable().getRffBic() != null
                            && currentRff.getResponsable().getRffIban() != null
                            && currentRff.getResponsable().getRffBic().length() > 0
                            && currentRff.getResponsable().getRffIban().length() > 0) {
                        iban = currentRff.getResponsable().getRffIban();
                        bic = currentRff.getResponsable().getRffBic();
                    } else if (currentRff.getTiers().getRffBic() != null
                            && currentRff.getTiers().getRffIban() != null
                            && currentRff.getTiers().getRffBic().length() > 0
                            && currentRff.getTiers().getRffIban().length() > 0) {
                        iban = currentRff.getTiers().getRffIban();
                        bic = currentRff.getTiers().getRffBic();
                    } else if (currentRff.getTiers().getPaymentBic() != null
                            && currentRff.getTiers().getPaymentIban() != null
                            && currentRff.getTiers().getPaymentBic().length() > 0
                            && currentRff.getTiers().getPaymentIban().length() > 0) {
                        iban = currentRff.getTiers().getPaymentIban();
                        bic = currentRff.getTiers().getPaymentBic();
                    }
                    if (iban != null && bic != null) {
                        currentRff.setRffBic(bic);
                        currentRff.setRffIban(iban);
                    }

                    String mail = null;
                    if (currentRff.getResponsable() != null && currentRff.getResponsable().getRffMail() != null
                            && currentRff.getResponsable().getRffMail().length() > 0) {
                        mail = currentRff.getResponsable().getRffMail();
                    } else if (currentRff.getTiers().getRffMail() != null
                            && currentRff.getTiers().getRffMail().length() > 0) {
                        mail = currentRff.getTiers().getRffMail();
                    } else if (currentRff.getTiers().getMails() != null
                            && currentRff.getTiers().getMails().size() > 0) {
                        mail = currentRff.getTiers().getMails().get(0).getMail();
                    }
                    if (mail != null) {
                        currentRff.setRffMail(mail);
                    }
                }

                addOrUpdateRff(currentRff);
                if (!rffSearch.getIsHideCancelledRff() || currentRff.getIsCancelled() == false)
                    finalRffs.add(addOrUpdateRff(currentRff));
            }

        return finalRffs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Rff addOrUpdateRff(Rff rff) {
        return rffRepository.save(rff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Rff cancelRff(Rff rff) {
        rff = getRff(rff.getId());
        rff.setIsCancelled(true);
        return addOrUpdateRff(rff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Rff sendRff(Rff rff, Float amount, boolean sendToMe) throws OsirisException, OsirisClientMessageException {
        rff = getRff(rff.getId());
        if (!sendToMe) {
            rff.setIsSent(true);
            rff.setRffTotal(amount);
        }
        mailHelper.sendRffToCustomer(rff, sendToMe);
        return addOrUpdateRff(rff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice generateInvoiceForRff(Rff rff)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        rff = getRff(rff.getId());

        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setIsProviderCreditNote(false);
        invoice.setIsInvoiceFromProvider(true);
        invoice.setTiers(rff.getTiers());
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        List<BillingItem> rffBillingItem = billingItemService
                .getBillingItemByBillingType(constantService.getBillingTypeRff());

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(pricingHelper.getAppliableBillingItem(rffBillingItem));
        invoiceItem.setDiscountAmount(0f);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);

        String tiersLabel = "";
        if (rff.getTiers().getDenomination() != null)
            tiersLabel = rff.getTiers().getDenomination();
        else
            tiersLabel = rff.getTiers().getFirstname() + " " + rff.getTiers().getLastname();

        String invoiceLabel = "RFF - " + rff.getStartDate().getYear() + "-"
                + StringUtils.leftPad(rff.getStartDate().getMonthValue() + "", 2, "0") + " - " + (tiersLabel);
        invoiceItem.setLabel(invoiceLabel);

        invoiceItem.setPreTaxPrice(Math.round(rff.getRffTotal() * 100f) / 100f);
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());
        invoice.getInvoiceItems().add(invoiceItem);
        vatService.completeVatOnInvoiceItem(invoiceItem, invoice);
        invoice.setManualPaymentType(constantService.getPaymentTypeVirement());
        invoice.setRff(rff);

        return invoiceService.addOrUpdateInvoiceFromUser(invoice);
    }
}
