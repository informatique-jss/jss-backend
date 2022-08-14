package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.repository.QuotationRepository;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    ProvisionTypeService provisionTypeService;

    @Autowired
    SpecialOfferService specialOfferService;

    @Autowired
    CharacterPriceService characterPriceService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (!quotation.isEmpty())
            return quotation.get();
        return null;
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) {
        if (quotation.getId() == null)
            quotation.setCreatedDate(new Date());

        // Complete domiciliation end date
        for (Provision provision : quotation.getProvisions()) {
            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (domiciliation.getEndDate() == null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(domiciliation.getStartDate());
                    c.add(Calendar.YEAR, 1);
                    domiciliation.setEndDate(c.getTime());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getMails() != null
                            && domiciliation.getMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getActivityMails() != null
                            && domiciliation.getActivityMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getActivityMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null
                            && domiciliation.getLegalGardianMails() != null
                            && domiciliation.getLegalGardianMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getLegalGardianMails());

                    if (domiciliation != null
                            && domiciliation.getLegalGardianPhones() != null
                            && domiciliation.getLegalGardianPhones().size() > 0)
                        phoneService.populateMPhoneIds(domiciliation.getLegalGardianPhones());

                }
            }
        }

        quotation = quotationRepository.save(quotation);
        indexEntityService.indexEntity(quotation, quotation.getId());
        return quotation;
    }

    @Override
    public List<InvoiceItem> getInvoiceItemsForQuotation(IQuotation quotation) {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        if (quotation.getProvisions() != null && quotation.getProvisions().size() > 0) {
            for (Provision provision : quotation.getProvisions()) {
                invoiceItems.addAll(getInvoiceItemsForProvision(provision, quotation));
            }
        }
        return invoiceItems;
    }

    private List<SpecialOffer> getAppliableSpecialOffersForQuotation(IQuotation quotation) {
        if (quotation != null) {
            if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0)
                return quotation.getSpecialOffers();

            if (quotation.getResponsable() != null && quotation.getResponsable().getTiers() != null
                    && quotation.getResponsable().getTiers().getSpecialOffers() != null
                    && quotation.getResponsable().getTiers().getSpecialOffers().size() > 0)
                return quotation.getResponsable().getTiers().getSpecialOffers();

            if (quotation.getTiers() != null && quotation.getTiers().getSpecialOffers() != null
                    && quotation.getTiers().getSpecialOffers().size() > 0)
                return quotation.getTiers().getSpecialOffers();
        }
        return null;
    }

    private AssoSpecialOfferBillingType getAppliableSpecialOfferForProvision(BillingType billingType,
            IQuotation quotation) {
        List<SpecialOffer> applicableSpecialOffers = getAppliableSpecialOffersForQuotation(quotation);
        if (applicableSpecialOffers != null) {
            for (SpecialOffer specialOffer : applicableSpecialOffers) {
                SpecialOffer fetchedSpecialOffer = specialOfferService.getSpecialOffer(specialOffer.getId());
                if (fetchedSpecialOffer != null && fetchedSpecialOffer.getAssoSpecialOfferBillingTypes() != null
                        && fetchedSpecialOffer.getAssoSpecialOfferBillingTypes().size() > 0)
                    for (AssoSpecialOfferBillingType assoSpecialOfferBillingType : fetchedSpecialOffer
                            .getAssoSpecialOfferBillingTypes()) {
                        if (assoSpecialOfferBillingType.getBillingType().getId()
                                .equals(billingType.getId()))
                            return assoSpecialOfferBillingType;
                    }
            }
        }
        return null;
    }

    private BillingItem getAppliableBillingItem(List<BillingItem> billingItems) {
        if (billingItems != null && billingItems.size() > 0)
            billingItems.sort(new Comparator<BillingItem>() {
                @Override
                public int compare(BillingItem o1, BillingItem o2) {
                    return o2.getStartDate().compareTo(o1.getStartDate());
                }
            });
        for (BillingItem billingItem : billingItems) {
            if (billingItem.getStartDate().before(new Date()))
                return billingItem;
        }
        return null;
    }

    private List<InvoiceItem> getInvoiceItemsForProvision(Provision provision, IQuotation quotation) {
        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        if (provision.getProvisionType() != null) {
            ProvisionType provisionType = provisionTypeService.getProvisionType(provision.getProvisionType().getId());
            if (provisionType != null) {
                for (BillingType billingType : provisionType.getBillingTypes()) {
                    List<BillingItem> billingItems = billingItemService.getBillingItemByBillingType(billingType);
                    if (billingItems != null && billingItems.size() > 0) {
                        BillingItem billingItem = getAppliableBillingItem(billingItems);

                        if (billingItem.getAccountingAccounts() != null
                                && billingItem.getAccountingAccounts().size() > 0) {
                            // TODO : algo pour déterminer la TVA à appliquer et donc le compte comptable à
                            // utiliser
                            AccountingAccount accountingAccount = billingItem.getAccountingAccounts().get(0);
                            if (!accountingAccount.getAccountingAccountNumber().substring(0, 1).equals("7"))
                                accountingAccount = billingItem.getAccountingAccounts().get(1);

                            InvoiceItem invoiceItem = new InvoiceItem();
                            invoiceItem.setAccountingAccount(accountingAccount);
                            invoiceItem.setBillingItem(billingItem);

                            invoiceItem.setLabel(billingType.getLabel());

                            if (billingType.getIsPriceBasedOnCharacterNumber()) {
                                CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
                                if (characterPrice != null) {
                                    invoiceItem.setPreTaxPrice(characterPrice.getPrice()
                                            * characterPriceService.getCharacterNumber(provision));
                                    invoiceItem.setLabel(invoiceItem.getLabel() + " ("
                                            + characterPriceService.getCharacterNumber(provision) + " caractères)");
                                } else {
                                    invoiceItem.setPreTaxPrice(0f);
                                }
                            } else {
                                invoiceItem.setPreTaxPrice(billingItem.getPreTaxPrice());
                            }

                            AssoSpecialOfferBillingType assoSpecialOfferBillingType = getAppliableSpecialOfferForProvision(
                                    billingType, quotation);

                            if (assoSpecialOfferBillingType != null) {
                                if (assoSpecialOfferBillingType.getDiscountAmount() != null
                                        && assoSpecialOfferBillingType.getDiscountAmount() > 0)
                                    invoiceItem.setDiscountAmount(assoSpecialOfferBillingType.getDiscountAmount());
                                if (assoSpecialOfferBillingType.getDiscountRate() != null
                                        && assoSpecialOfferBillingType.getDiscountRate() > 0)
                                    invoiceItem.setDiscountAmount(
                                            invoiceItem.getPreTaxPrice()
                                                    * assoSpecialOfferBillingType.getDiscountRate() / 100);
                            }

                            invoiceItem.setVatPrice(accountingAccount.getVat().getRate() / 100 * (invoiceItem
                                    .getPreTaxPrice()
                                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0)));
                            invoiceItem.setProvision(provision);
                            invoiceItems.add(invoiceItem);
                        }
                    }
                }
            }
        }
        return invoiceItems;
    }
}
