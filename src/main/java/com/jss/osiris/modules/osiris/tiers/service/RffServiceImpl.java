package com.jss.osiris.modules.osiris.tiers.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.tiers.model.IRffCompute;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.RffSearch;
import com.jss.osiris.modules.osiris.tiers.repository.RffRepository;

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
    VatService vatService;

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
        if (rffSearch.getTiers() != null && rffSearch.getTiers().getEntityId() != null)
            idTiers = rffSearch.getTiers().getEntityId();

        Integer idResponsable = 0;
        if (rffSearch.getResponsable() != null && rffSearch.getResponsable().getEntityId() != null)
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
                    currentRff.setRffTotal(rffCompute.getRffAl().add(rffCompute.getRffFor()));
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

                if (currentRff.getIsCancelled() == false) {
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
    public Rff sendRff(Rff rff, BigDecimal amount, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {
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
        invoice.setIsCreditNote(false);
        invoice.setResponsable(rff.getResponsable());
        if (rff.getResponsable() == null)
            invoice.setResponsable(rff.getTiers().getResponsables().get(0));
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(pricingHelper.getAppliableBillingItem(constantService.getBillingTypeRff(), null));
        invoiceItem.setDiscountAmount(BigDecimal.ZERO);
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

        invoiceItem.setPreTaxPrice(rff.getRffTotal().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_EVEN)
                .divide(new BigDecimal(100)));
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());
        invoice.getInvoiceItems().add(invoiceItem);
        vatService.completeVatOnInvoiceItem(invoiceItem, invoice);
        invoice.setManualPaymentType(constantService.getPaymentTypeVirement());
        invoice.setRff(rff);

        return invoice;
    }
}
