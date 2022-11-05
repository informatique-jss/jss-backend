package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
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

    @Autowired
    QuotationStatusService quotationStatusService;

    @Autowired
    ConstantService constantService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (quotation.isPresent())
            return quotation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationFromUser(Quotation quotation) throws Exception {
        return addOrUpdateQuotation(quotation);
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) throws Exception {
        if (quotation.getId() == null)
            quotation.setCreatedDate(LocalDateTime.now());

        quotation.setIsQuotation(true);

        if (quotation.getDocuments() != null)
            for (Document document : quotation.getDocuments())
                document.setQuotation(quotation);

        // Complete domiciliation end date
        for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
            assoAffaireOrder.setCustomerOrder(null);
            assoAffaireOrder.setQuotation(quotation);

            for (Provision provision : assoAffaireOrder.getProvisions()) {
                provision.setAssoAffaireOrder(assoAffaireOrder);
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
        }

        getAndSetInvoiceItemsForQuotation(quotation);
        quotation = quotationRepository.save(quotation);
        indexEntityService.indexEntity(quotation, quotation.getId());
        return quotation;
    }

    @Override
    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation) throws Exception {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0
                && quotation.getAssoAffaireOrders().get(0).getProvisions() != null
                && quotation.getAssoAffaireOrders().get(0).getProvisions().size() > 0) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                for (Provision provision : assoAffaireOrder.getProvisions()) {
                    invoiceItems.addAll(getInvoiceItemsForProvision(provision, quotation));
                }
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
        if (billingItems == null)
            return null;

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

                        if (billingType.getAccountingAccountProduct() != null
                                && (!billingItem.getBillingType().getIsOptionnal()
                                        || hasOption(billingItem, provision))) {

                            InvoiceItem invoiceItem = new InvoiceItem();
                            invoiceItem.setBillingItem(billingItem);

                            invoiceItem.setLabel(billingType.getLabel());

                            if (billingType.getIsPriceBasedOnCharacterNumber()) {
                                CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
                                if (characterPrice != null) {
                                    Float price = characterPrice.getPrice()
                                            * characterPriceService.getCharacterNumber(provision);
                                    invoiceItem.setPreTaxPrice(Math.round(price * 100f) / 100f);
                                    invoiceItem.setLabel(invoiceItem.getLabel() + " ("
                                            + characterPriceService.getCharacterNumber(provision) + " caractères)");
                                } else {
                                    invoiceItem.setPreTaxPrice(0f);
                                }
                            } else {
                                invoiceItem.setPreTaxPrice(Math.round(billingItem.getPreTaxPrice() * 100f) / 100f);
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

    private boolean hasOption(BillingItem billingItem, Provision provision) throws Exception {
        if (billingItem.getBillingType().getId().equals(constantService.getBillingTypeLogo().getId())
                && provision.getAnnouncement() != null
                && provision.getIsLogo() != null && provision.getIsLogo())
            return true;
        return false;
    }

    private void computeInvoiceItemsVatAndDiscount(InvoiceItem invoiceItem, IQuotation quotation) throws Exception {
        AssoSpecialOfferBillingType assoSpecialOfferBillingType = getAppliableSpecialOfferForProvision(
                invoiceItem.getBillingItem().getBillingType(), quotation);

        if (assoSpecialOfferBillingType != null) {
            if (assoSpecialOfferBillingType.getDiscountAmount() != null
                    && assoSpecialOfferBillingType.getDiscountAmount() > 0)
                invoiceItem
                        .setDiscountAmount(Math.round(assoSpecialOfferBillingType.getDiscountAmount() * 100f) / 100f);
            if (assoSpecialOfferBillingType.getDiscountRate() != null
                    && assoSpecialOfferBillingType.getDiscountRate() > 0)
                invoiceItem.setDiscountAmount(Math.round(
                        invoiceItem.getPreTaxPrice() * assoSpecialOfferBillingType.getDiscountRate() / 100f * 100f)
                        / 100f);
        }

        Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());
        Vat vat = null;

        // Search for customer order
        ITiers customerOrder = null;
        if (quotation.getConfrere() != null)
            customerOrder = quotation.getConfrere();
        else if (quotation.getResponsable() != null)
            customerOrder = quotation.getResponsable().getTiers();
        else
            customerOrder = quotation.getTiers();

        // If document not found or document indicate to use it, take customer order as
        // default
        if (invoiceItem.getBillingItem() != null && invoiceItem.getBillingItem().getBillingType() != null
                && invoiceItem.getBillingItem().getBillingType().getIsOverrideVat()) {
            vat = invoiceItem.getBillingItem().getBillingType().getVat();
        } else {
            if (billingDocument == null || billingDocument.getBillingLabelType() == null
                    || billingDocument.getBillingLabelType().getId()
                            .equals(constantService.getBillingLabelTypeCustomer().getId())) {
                vat = vatService.getGeographicalApplicableVat(customerOrder.getCountry(),
                        customerOrder.getCity().getDepartment(),
                        customerOrder.getIsIndividual());
            } else if (billingDocument.getBillingLabelType().getId()
                    .equals(constantService.getBillingLabelTypeCodeAffaire().getId())) {
                Affaire affaire = invoiceItem.getProvision().getAssoAffaireOrder().getAffaire();
                vat = vatService.getGeographicalApplicableVat(affaire.getCountry(), affaire.getCity().getDepartment(),
                        affaire.getIsIndividual());
            } else {
                vat = vatService.getGeographicalApplicableVat(billingDocument.getBillingLabelCountry(),
                        billingDocument.getBillingLabelCity().getDepartment(),
                        billingDocument.getBillingLabelIsIndividual());
            }
        }

        if (vat != null) {
            Float vatPrice = vat.getRate() / 100 * (invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0));
            invoiceItem.setVatPrice(Math.round(vatPrice * 100f) / 100f);
            invoiceItem.setVat(vat);
        } else {
            invoiceItem.setVatPrice(0f);
        }

    }

    private void mergeInvoiceItemsForQuotation(IQuotation quotation, List<InvoiceItem> invoiceItemsToMerge)
            throws Exception {
        if (quotation != null && invoiceItemsToMerge != null && quotation.getAssoAffaireOrders() != null
                && quotation.getAssoAffaireOrders().size() > 0) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getProvisions() != null) {
                    for (Provision provision : assoAffaireOrder.getProvisions()) {
                        ArrayList<InvoiceItem> finalInvoiceItems = new ArrayList<InvoiceItem>();
                        for (InvoiceItem invoiceItemToMerge : invoiceItemsToMerge) {
                            if (provision.getInvoiceItems() != null) {
                                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                                    if (invoiceItemToMerge.getProvision().getId() != null
                                            && provision.getId() != null
                                            && invoiceItemToMerge.getProvision().getId()
                                                    .equals(provision.getId())) {
                                        if (invoiceItemToMerge.getBillingItem().getId()
                                                .equals(invoiceItem.getBillingItem().getId())) {
                                            // Only if invoice is not cancelled, in that case generate new invoice item
                                            if (invoiceItem.getInvoice() == null || !invoiceItem.getInvoice()
                                                    .getInvoiceStatus().getId()
                                                    .equals(constantService.getInvoiceStatusCancelled().getId())) {
                                                invoiceItemToMerge.setId(invoiceItem.getId());
                                                invoiceItemToMerge.setInvoice(invoiceItem.getInvoice());
                                            }
                                            if (invoiceItemToMerge.getBillingItem().getBillingType()
                                                    .getCanOverridePrice()
                                                    && invoiceItem.getPreTaxPrice() != null
                                                    && invoiceItem.getPreTaxPrice() > 0) {
                                                invoiceItemToMerge.setPreTaxPrice(
                                                        Math.round(invoiceItem.getPreTaxPrice().floatValue() * 100f)
                                                                / 100f);
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
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode) throws Exception {
        QuotationStatus quotationStatus = quotationStatusService.getQuotationStatusByCode(targetStatusCode);
        if (quotationStatus == null)
            throw new Exception("Quotation status not found for code " + targetStatusCode);
        quotation.setQuotationStatus(quotationStatus);
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

    @Override
    public List<Quotation> searchQuotations(OrderingSearch orderingSearch) {
        return quotationRepository.findQuotations(
                orderingSearch.getSalesEmployee(),
                orderingSearch.getQuotationStatus(),
                orderingSearch.getStartDate(),
                orderingSearch.getEndDate());
    }

    @Override
    public void reindexQuotation() {
        List<Quotation> quotations = IterableUtils.toList(quotationRepository.findAll());
        if (quotations != null)
            for (Quotation quotation : quotations)
                indexEntityService.indexEntity(quotation, quotation.getId());
    }
}
