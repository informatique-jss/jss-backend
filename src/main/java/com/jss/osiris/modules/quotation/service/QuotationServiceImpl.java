package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.repository.QuotationRepository;
import com.jss.osiris.modules.tiers.model.ITiers;

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

    @Autowired
    VatService vatService;

    @Autowired
    DocumentService documentService;

    @Value("${miscellaneous.document.billing.label.type.customer.code}")
    private String billingLabelCustomerCode;

    @Value("${miscellaneous.document.billing.label.type.affaire.code}")
    private String billingLabelAffaireCode;

    @Value("${miscellaneous.document.billing.label.type.other.code}")
    private String billingLabelOtherCode;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (!quotation.isEmpty())
            return quotation.get();
        return null;
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) throws Exception {
        if (quotation.getId() == null)
            quotation.setCreatedDate(LocalDateTime.now());

        quotation.setIsQuotation(true);

        // Complete domiciliation end date
        for (Provision provision : quotation.getProvisions()) {
            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (domiciliation.getEndDate() == null) {
                    domiciliation.setEndDate(domiciliation.getStartDate().plusYears(1));

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

        getAndSetInvoiceItemsForQuotation(quotation);
        quotation = quotationRepository.save(quotation);
        indexEntityService.indexEntity(quotation, quotation.getId());
        return quotation;
    }

    @Override
    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation) throws Exception {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        if (quotation.getProvisions() != null && quotation.getProvisions().size() > 0) {
            for (Provision provision : quotation.getProvisions()) {
                invoiceItems.addAll(getInvoiceItemsForProvision(provision, quotation));
            }
        }
        mergeInvoiceItemsForQuotation(quotation, invoiceItems);
        return quotation;
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
            if (billingItem.getStartDate().isBefore(LocalDate.now()))
                return billingItem;
        }
        return null;
    }

    private List<InvoiceItem> getInvoiceItemsForProvision(Provision provision, IQuotation quotation) throws Exception {
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

                            InvoiceItem invoiceItem = new InvoiceItem();
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
                            invoiceItem.setProvision(provision);
                            computeInvoiceItemsVatAndDiscount(invoiceItem, quotation);

                            invoiceItems.add(invoiceItem);
                        }
                    }
                }
            }
        }
        return invoiceItems;
    }

    private void computeInvoiceItemsVatAndDiscount(InvoiceItem invoiceItem, IQuotation quotation) throws Exception {
        AssoSpecialOfferBillingType assoSpecialOfferBillingType = getAppliableSpecialOfferForProvision(
                invoiceItem.getBillingItem().getBillingType(), quotation);

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

        Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());
        Vat vat = null;

        // Search for customer order
        ITiers customerOrder = null;
        if (quotation.getConfrere() != null)
            customerOrder = quotation.getConfrere();
        if (quotation.getResponsable() != null)
            customerOrder = quotation.getResponsable();
        customerOrder = quotation.getTiers();

        // If document not found or document indicate to use it, take customer order as
        // default
        if (billingDocument == null || billingDocument.getBillingLabelType() == null
                || billingDocument.getBillingLabelType().getCode().equals(billingLabelCustomerCode)) {
            vat = vatService.getApplicableVat(customerOrder.getCountry(), customerOrder.getCity().getDepartment(),
                    customerOrder.getIsIndividual());
        } else if (billingDocument.getBillingLabelType().getCode().equals(billingLabelAffaireCode)) {
            Affaire affaire = invoiceItem.getProvision().getAffaire();
            vat = vatService.getApplicableVat(affaire.getCountry(), affaire.getCity().getDepartment(),
                    affaire.getIsIndividual());
        } else {
            vat = vatService.getApplicableVat(billingDocument.getBillingLabelCountry(),
                    billingDocument.getBillingLabelCity().getDepartment(),
                    billingDocument.getBillingLabelIsIndividual());
        }

        if (vat != null) {
            invoiceItem.setVatPrice(vat.getRate() / 100 * (invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0)));
            invoiceItem.setVat(vat);
        } else {
            invoiceItem.setVatPrice(0f);
        }

    }

    private void mergeInvoiceItemsForQuotation(IQuotation quotation, List<InvoiceItem> invoiceItemsToMerge)
            throws Exception {
        if (quotation != null && invoiceItemsToMerge != null && quotation.getProvisions() != null) {
            for (Provision provision : quotation.getProvisions()) {
                ArrayList<InvoiceItem> finalInvoiceItems = new ArrayList<InvoiceItem>();
                for (InvoiceItem invoiceItemToMerge : invoiceItemsToMerge) {
                    if (provision.getInvoiceItems() != null) {
                        for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                            if (invoiceItemToMerge.getProvision().getId() != null
                                    && invoiceItem.getProvision().getId() != null
                                    && invoiceItemToMerge.getProvision().getId()
                                            .equals(invoiceItem.getProvision().getId())) {
                                if (invoiceItemToMerge.getBillingItem().getId()
                                        .equals(invoiceItem.getBillingItem().getId())) {
                                    invoiceItemToMerge.setId(invoiceItem.getId());
                                    invoiceItemToMerge.setInvoice(invoiceItem.getInvoice());
                                    if (invoiceItemToMerge.getBillingItem().getBillingType().getCanOverridePrice()
                                            && invoiceItem.getPreTaxPrice() != null
                                            && invoiceItem.getPreTaxPrice() > 0) {
                                        invoiceItemToMerge.setPreTaxPrice(invoiceItem.getPreTaxPrice().floatValue());
                                        computeInvoiceItemsVatAndDiscount(invoiceItemToMerge, quotation);
                                    }
                                }
                            }
                        }
                    }
                    finalInvoiceItems.add(invoiceItemToMerge);
                }
                provision.setInvoiceItems(finalInvoiceItems);
            }
        }
    }

    @Override
    public Quotation addOrUpdateQuotationStatus(Quotation quotation) throws Exception {
        return this.addOrUpdateQuotation(quotation);
    }

    @Override
    public ITiers getCustomerOrderOfQuotation(IQuotation quotation) throws Exception {
        if (quotation.getConfrere() != null)
            return quotation.getConfrere();
        if (quotation.getResponsable() != null)
            return quotation.getResponsable();
        if (quotation.getTiers() != null)
            return quotation.getTiers();

        throw new Exception("No customer order declared on IQuotation " + quotation.getId());
    }
}
