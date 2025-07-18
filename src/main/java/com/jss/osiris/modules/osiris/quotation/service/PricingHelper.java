package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.service.VoucherService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.CharacterPrice;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationFee;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

@org.springframework.stereotype.Service
public class PricingHelper {

    private BigDecimal zeroValue = new BigDecimal(0);
    private BigDecimal oneHundredValue = new BigDecimal(100);

    @Autowired
    SpecialOfferService specialOfferService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CharacterPriceService characterPriceService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    ProvisionTypeService provisionTypeService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    VatService vatService;

    @Autowired
    AuditService auditService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    VoucherService voucherService;

    @Transactional
    public IQuotation getAndSetInvoiceItemsForQuotationForFront(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return getAndSetInvoiceItemsForQuotation(quotation, persistInvoiceItem);
    }

    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices())
                        for (Provision provision : service.getProvisions()) {
                            provision.setService(service);
                            service.setAssoAffaireOrder(assoAffaireOrder);
                            if (provision.getProvisionType() != null && provision.getProvisionType().getId() != null)
                                setInvoiceItemsForProvision(provision, quotation, persistInvoiceItem);
                        }
            }
        }
        return quotation;
    }

    private List<SpecialOffer> getAppliableSpecialOffersForQuotation(IQuotation quotation) {
        if (quotation != null) {
            if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0)
                return quotation.getSpecialOffers();
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

    public BillingItem getAppliableBillingItem(BillingType billingType, IQuotation quotation)
            throws OsirisException {
        List<BillingItem> billingItems = billingItemService.getBillingItemByBillingType(billingType);
        if (billingItems == null)
            throw new OsirisException(null, "No billing items provided");

        if (billingItems != null && billingItems.size() > 0)
            billingItems.sort(new Comparator<BillingItem>() {

                public int compare(BillingItem o1, BillingItem o2) {
                    return o2.getStartDate().compareTo(o1.getStartDate());
                }
            });

        LocalDate billingDate = null;
        // Use quotation date if customerOrder linked to customer validated one
        // Else use first TO_BILLED status to determine billing date
        if (quotation != null && quotation.getId() != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotation.getId());
            if (customerOrder != null) {
                billingDate = customerOrder.getPrincingEffectiveDate();
            } else {
                Quotation quotationFetch = quotationService.getQuotation(quotation.getId());
                if (quotation != null) {
                    billingDate = quotationFetch.getPrincingEffectiveDate();
                }
            }
        }

        if (billingDate == null)
            billingDate = LocalDate.now();

        for (BillingItem billingItem : billingItems) {
            if (billingItem.getStartDate().isBefore(billingDate))
                return billingItem;
        }
        throw new OsirisException(null, "No billing items appliable found");
    }

    private void setInvoiceItemPreTaxPriceAndLabel(InvoiceItem invoiceItem, BillingItem billingItem,
            Provision provision, IQuotation quotation)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        invoiceItem.setLabel(billingItem.getBillingType().getLabel());
        if (billingItem.getBillingType().getIsPriceBasedOnCharacterNumber()) {
            CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
            if (characterPrice != null) {
                BigDecimal price = characterPrice.getPrice()
                        .multiply(BigDecimal.valueOf(characterPriceService.getCharacterNumber(provision, false)));
                invoiceItem.setPreTaxPrice(price);

                // Add notice type indication for announcements
                String noticeFamiliyType = (provision.getAnnouncement() != null
                        && provision.getAnnouncement().getNoticeTypeFamily() != null)
                                ? provision.getAnnouncement().getNoticeTypeFamily().getLabel()
                                : null;
                ArrayList<String> noticeTypes = new ArrayList<String>();
                if (noticeFamiliyType != null
                        && provision.getAnnouncement().getNoticeTypes() != null)
                    for (NoticeType noticeType : provision.getAnnouncement().getNoticeTypes())
                        noticeTypes.add(noticeType.getLabel());

                if (noticeFamiliyType != null && noticeTypes.size() > 0)
                    invoiceItem.setLabel(invoiceItem.getLabel() + " ("
                            + characterPriceService.getCharacterNumber(provision, false)
                            + " caractères"
                            + (provision.getAnnouncement() != null
                                    && provision.getAnnouncement().getPublicationDate() != null
                                            ? ", " + (provision.getAnnouncement().getPublicationDate()
                                                    .isAfter(LocalDate.now())
                                                            ? "sera publiée le "
                                                            : "publiée le ")
                                                    + provision.getAnnouncement().getPublicationDate()
                                                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                            : "")
                            + ", rubrique " + noticeFamiliyType + ", "
                            + String.join(" / ", noticeTypes) + ")");
                else
                    invoiceItem.setLabel(invoiceItem.getLabel() + " ("
                            + characterPriceService.getCharacterNumber(provision, false) + ")");

                if (provision.getAnnouncement().getDepartment() != null)
                    invoiceItem.setLabel(
                            invoiceItem.getLabel() + " - " + provision.getAnnouncement().getDepartment().getCode());

            } else {
                invoiceItem.setPreTaxPrice(zeroValue);
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypePublicationPaper().getId())) {
            Integer nbr = getPublicationPaperNbr(provision);
            Confrere confrere = provision.getAnnouncement().getConfrere();
            if (nbr > 0 && confrere != null) {
                invoiceItem.setLabel(invoiceItem.getLabel() + " (quantité : " + nbr + ")");

                invoiceItem.setPreTaxPrice(
                        (confrere.getPaperPrice() != null ? confrere.getPaperPrice() : zeroValue)
                                .multiply(BigDecimal.valueOf(nbr)));
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypeConfrereFees().getId())) {
            // If it's an announcement published by a Confrere, apply additionnal fees and
            // JSS markup
            invoiceItem.setPreTaxPrice(zeroValue);

            // Check if we have a character based price announcement
            boolean hasPriceBasedProvisionType = false;
            if (provision.getProvisionType() != null && provision.getProvisionType().getBillingTypes() != null)
                for (BillingType otherBillingType : provision.getProvisionType().getBillingTypes())
                    if (otherBillingType.getIsPriceBasedOnCharacterNumber())
                        hasPriceBasedProvisionType = true;

            if (isNotJssConfrere(provision) && hasPriceBasedProvisionType) {
                CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
                BigDecimal price = zeroValue;
                if (characterPrice != null) {
                    price = characterPrice.getPrice()
                            .multiply(BigDecimal.valueOf(characterPriceService.getCharacterNumber(provision, false)));
                }

                BigDecimal additionnalFees = zeroValue;
                Confrere confrere = provision.getAnnouncement().getConfrere();
                if (confrere.getAdministrativeFees() != null)
                    additionnalFees = additionnalFees.add(BigDecimal.valueOf(confrere.getAdministrativeFees()));

                invoiceItem.setPreTaxPrice(
                        (price != null ? price : zeroValue)
                                .multiply((confrere.getReinvoicing() != null
                                        ? BigDecimal.valueOf(confrere.getReinvoicing())
                                        : zeroValue)
                                        .divide(oneHundredValue))
                                .add(additionnalFees));
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypeShippingCosts().getId())
                && provision.getAnnouncement().getConfrere() != null) {
            invoiceItem.setPreTaxPrice(zeroValue);
            Integer nbr = getPublicationPaperNbr(provision);
            Confrere confrere = provision.getAnnouncement().getConfrere();
            if (nbr > 0)
                invoiceItem.setPreTaxPrice(
                        (confrere.getShippingCosts() != null ? BigDecimal.valueOf(confrere.getShippingCosts())
                                : zeroValue).multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                                .divide(oneHundredValue));
        } else if (billingItem.getBillingType().getIsDebour()
                && !provision.getService().getServiceTypes().isEmpty()) {
            if (quotation instanceof Quotation || ((CustomerOrder) quotation).getCustomerOrderStatus() != null
                    && ((CustomerOrder) quotation).getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT))
                if (invoiceItem.getPreTaxPrice() == null || invoiceItem.getPreTaxPrice().equals(zeroValue))
                    for (ServiceType serviceType : provision.getService().getServiceTypes()) {
                        if (serviceType.getAssoServiceProvisionTypes() != null)
                            for (AssoServiceProvisionType assoServiceProvisionType : serviceType
                                    .getAssoServiceProvisionTypes())
                                if (assoServiceProvisionType.getProvisionType().getId()
                                        .equals(provision.getProvisionType().getId()))
                                    if (billingItem.getBillingType().getIsNonTaxable() == false
                                            && assoServiceProvisionType.getDefaultDeboursPrice() != null) {
                                        if (invoiceItem.getPreTaxPrice() == null)
                                            invoiceItem
                                                    .setPreTaxPrice(assoServiceProvisionType.getDefaultDeboursPrice());
                                        else
                                            invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice()
                                                    .add(assoServiceProvisionType.getDefaultDeboursPrice()));
                                    } else if (billingItem.getBillingType().getIsNonTaxable() == true
                                            && assoServiceProvisionType.getDefaultDeboursPriceNonTaxable() != null) {
                                        if (invoiceItem.getPreTaxPrice() == null)
                                            invoiceItem.setPreTaxPrice(
                                                    assoServiceProvisionType.getDefaultDeboursPriceNonTaxable());
                                        else
                                            invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice()
                                                    .add(assoServiceProvisionType.getDefaultDeboursPriceNonTaxable()));
                                    }
                    }
        } else {
            invoiceItem.setPreTaxPrice(billingItem.getPreTaxPrice());
        }

        // Domiciliation pricing
        // First one => double price for base
        if (quotation instanceof CustomerOrder && provision.getDomiciliation() != null && quotation.getId() != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotation.getId());
            if (customerOrder != null) {
                CustomerOrder masterCustomerOrder = (customerOrder.getIsRecurring() != null
                        && customerOrder.getIsRecurring()) ? customerOrder
                                : customerOrder.getCustomerOrderParentRecurring();
                if (masterCustomerOrder != null && masterCustomerOrder.getCustomerOrderFrequency() != null)
                    if (billingItem.getBillingType().getId()
                            .equals(constantService.getBillingTypeDomiciliationContractTypeKeepMail().getId())
                            || billingItem.getBillingType().getId()
                                    .equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmail().getId())
                            || billingItem.getBillingType().getId()
                                    .equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmailAndMail()
                                            .getId())
                            || billingItem.getBillingType().getId()
                                    .equals(constantService.getBillingTypeDomiciliationContractTypeRouteMail().getId()))
                        invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice().multiply(
                                BigDecimal.valueOf(masterCustomerOrder.getCustomerOrderFrequency().getMonthNumber())));

                if (customerOrder.getIsRecurring() != null && customerOrder.getIsRecurring()
                        && invoiceItem.getPreTaxPrice() != null)
                    invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice().multiply(new BigDecimal(2)));
            }
        }

        if (invoiceItem.getPreTaxPrice() != null)
            invoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice().multiply(oneHundredValue)
                    .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue));

        if (invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted()) {
            invoiceItem.setPreTaxPrice(zeroValue);
            invoiceItem.setDiscountAmount(zeroValue);
            invoiceItem.setLabel(invoiceItem.getLabel() + " (offert)");
        }

    }

    private boolean isNotJssConfrere(Provision provision) throws OsirisException {
        return provision.getAnnouncement() != null && provision.getAnnouncement().getConfrere() != null
                && !provision.getAnnouncement().getConfrere().getId()
                        .equals(constantService.getConfrereJssSpel().getId());
    }

    private Integer getPublicationPaperNbr(Provision provision) {
        Integer nbr = 0;
        if (provision.getIsPublicationPaper() != null && provision.getIsPublicationPaper()) {
            // Compute publication paper price
            if (provision.getPublicationPaperAffaireNumber() != null
                    && provision.getPublicationPaperAffaireNumber() > 0)
                nbr += provision.getPublicationPaperAffaireNumber();
            if (provision.getPublicationPaperClientNumber() != null
                    && provision.getPublicationPaperClientNumber() > 0)
                nbr += provision.getPublicationPaperClientNumber();
        }
        return nbr;
    }

    public void setInvoiceItemsForProvision(Provision provision, IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (provision == null)
            return;

        if (quotation != null && provision != null) {
            if (provision.getInvoiceItems() == null)
                provision.setInvoiceItems(new ArrayList<InvoiceItem>());

            // If billed, do not change items
            if (provision.getInvoiceItems().size() > 0)
                for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                    if (invoiceItem.getInvoice() != null && invoiceItem.getInvoice().getProvider() == null
                            && invoiceItem.getInvoice().getIsCreditNote() == false
                            && (invoiceItem.getInvoice().getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusSend().getId())
                                    || invoiceItem.getInvoice().getInvoiceStatus().getId()
                                            .equals(constantService.getInvoiceStatusPayed().getId())))
                        return;

            ProvisionType provisionType = provisionTypeService.getProvisionType(provision.getProvisionType().getId());
            if (provisionType != null) {
                for (BillingType billingType : provisionType.getBillingTypes()) {
                    BillingItem billingItem = getAppliableBillingItem(billingType, quotation);

                    if (billingItem != null && billingType.getAccountingAccountProduct() != null
                            && (!billingItem.getBillingType().getIsOptionnal()
                                    || hasOption(billingItem.getBillingType(), provision))) {

                        InvoiceItem invoiceItem = null;

                        if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                            for (InvoiceItem invoiceItemProvision : provision.getInvoiceItems()) {

                                InvoiceItem tempInvoiceItem;
                                if (invoiceItemProvision.getId() != null) {
                                    tempInvoiceItem = invoiceItemService
                                            .getInvoiceItem(invoiceItemProvision.getId());
                                } else
                                    tempInvoiceItem = invoiceItemProvision;

                                if (tempInvoiceItem.getOriginProviderInvoice() != null)
                                    continue;

                                if (invoiceItemProvision.getBillingItem() != null
                                        && invoiceItemProvision.getBillingItem().getBillingType().getId()
                                                .equals(billingType.getId()))
                                    invoiceItem = invoiceItemProvision;
                            }

                        if (invoiceItem == null) {
                            invoiceItem = new InvoiceItem();
                            provision.getInvoiceItems().add(invoiceItem);
                        }

                        // Complete invoice item
                        invoiceItem.setBillingItem(billingItem);
                        invoiceItem.setInvoice(null);
                        invoiceItem.setProvision(provision);

                        if (invoiceItem.getIsOverridePrice() == null)
                            invoiceItem.setIsOverridePrice(false);

                        if (!invoiceItem.getIsOverridePrice() || !billingType.getCanOverridePrice()
                                || invoiceItem.getPreTaxPrice() == null
                                || invoiceItem.getPreTaxPrice().compareTo(zeroValue) <= 0
                                || invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted())
                            setInvoiceItemPreTaxPriceAndLabel(invoiceItem, billingItem, provision, quotation);
                        computeInvoiceItemsVatAndDiscount(invoiceItem, quotation, provision);

                        // If vacation multiple option, bypass vacation and traitement billing type
                        if (provision.getIsVacationMultipleModification() != null
                                && provision.getIsVacationMultipleModification() && billingType.getIsVacation() != null
                                && billingType.getIsVacation()) {
                            invoiceItem.setPreTaxPrice(zeroValue);
                            invoiceItem.setVatPrice(zeroValue);
                            invoiceItem.setDiscountAmount(zeroValue);
                        }
                        if (provision.getIsTreatmentMultipleModiciation() != null
                                && provision.getIsTreatmentMultipleModiciation()
                                && billingType.getIsTraitement() != null
                                && billingType.getIsTraitement()) {
                            invoiceItem.setPreTaxPrice(zeroValue);
                            invoiceItem.setVatPrice(zeroValue);
                            invoiceItem.setDiscountAmount(zeroValue);
                        }

                        if (invoiceItem.getPreTaxPrice() == null)
                            invoiceItem.setPreTaxPrice(zeroValue);

                        if (persistInvoiceItem)
                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                    }
                }
            }

            // Delete unused item
            ArrayList<InvoiceItem> invoiceItemsDeleted = new ArrayList<InvoiceItem>();
            ArrayList<Integer> billingTypeAlreadyFound = new ArrayList<Integer>();
            for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                // Invoice item generated from provider invoices are handled after
                InvoiceItem tempInvoiceItem;
                if (invoiceItem.getId() != null) {
                    tempInvoiceItem = invoiceItemService.getInvoiceItem(invoiceItem.getId());
                } else
                    tempInvoiceItem = invoiceItem;

                if (tempInvoiceItem.getInvoice() == null && tempInvoiceItem.getOriginProviderInvoice() == null
                        && provisionType != null) {
                    boolean found = false;
                    for (BillingType billingType : provisionType.getBillingTypes()) {
                        if (invoiceItem.getBillingItem() != null
                                && billingType.getId().equals(invoiceItem.getBillingItem().getBillingType().getId())
                                && (!billingType.getIsOptionnal()
                                        || hasOption(billingType, provision))
                                && !billingTypeAlreadyFound
                                        .contains(invoiceItem.getBillingItem().getBillingType().getId())) {
                            found = true;
                            billingTypeAlreadyFound.add(invoiceItem.getBillingItem().getBillingType().getId());
                        }
                    }

                    if (!found) {
                        invoiceItemsDeleted.add(invoiceItem);
                        invoiceItem.setProvision(null);
                        if (persistInvoiceItem && invoiceItem.getId() != null) {
                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                            invoiceItemService.deleteInvoiceItem(invoiceItem);
                        }
                    }
                }
            }
            provision.getInvoiceItems().removeAll(invoiceItemsDeleted);
        }

        // Delete cancelled invoice
        ArrayList<InvoiceItem> invoiceItemsDeleted = new ArrayList<InvoiceItem>();
        ArrayList<Integer> idInvoiceAlreadyDone = new ArrayList<Integer>();
        if (provision != null && provision.getInvoiceItems() != null) {
            for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {

                InvoiceItem tempInvoiceItem;
                if (invoiceItem.getId() != null) {
                    tempInvoiceItem = invoiceItemService.getInvoiceItem(invoiceItem.getId());
                } else
                    tempInvoiceItem = invoiceItem;

                if (tempInvoiceItem != null && tempInvoiceItem.getOriginProviderInvoice() != null) {
                    if (tempInvoiceItem.getOriginProviderInvoice().getInvoiceStatus()
                            .getId().equals(constantService.getInvoiceStatusCancelled().getId())) {
                        invoiceItemsDeleted.add(invoiceItem);

                        if (persistInvoiceItem && invoiceItem.getId() != null) {
                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                            invoiceItemService.deleteInvoiceItem(invoiceItem);
                        }
                    }
                    idInvoiceAlreadyDone.add(tempInvoiceItem.getOriginProviderInvoice().getId());
                }
            }
            provision.getInvoiceItems().removeAll(invoiceItemsDeleted);
        }

        // Reinvoiced provider invoices
        if (provision != null) {
            if (provision.getProviderInvoices() != null) {
                for (Invoice invoice : provision.getProviderInvoices()) {
                    if (!invoice.getInvoiceStatus().getId().equals(
                            constantService.getInvoiceStatusCancelled().getId()) && invoice.getProvider() != null
                            && !idInvoiceAlreadyDone.contains(invoice.getId()) && invoice.getInvoiceItems() != null) {
                        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                            InvoiceItem newInvoiceItem = invoiceItemService.cloneInvoiceItem(invoiceItem);
                            newInvoiceItem.setOriginProviderInvoice(invoice);
                            newInvoiceItem.setInvoice(null);
                            newInvoiceItem.setIsOverridePrice(false);
                            newInvoiceItem.setLabel(invoiceItem.getLabel().replace("<", ""));
                            newInvoiceItem.setLabel(invoice.getProvider().getLabel() + " - "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel());
                            newInvoiceItem.setProvision(provision);
                            newInvoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPriceReinvoiced());
                            if (invoiceItem.getVat().getRate().compareTo(zeroValue) > 0)
                                newInvoiceItem.setVat(null);

                            if (invoiceItem.getVat().getRate().compareTo(zeroValue) <= 0
                                    || invoiceItem.getBillingItem().getBillingType().getIsNonTaxable())
                                newInvoiceItem.setVat(constantService.getVatZero());
                            vatService.completeVatOnInvoiceItem(newInvoiceItem, quotation);

                            // Keep less rate of both and recompute price
                            if (invoiceItem.getVat() != null
                                    && invoiceItem.getVat().getRate()
                                            .compareTo(newInvoiceItem.getVat().getRate()) < 0) {
                                newInvoiceItem.setVat(invoiceItem.getVat());
                                vatService.completeVatOnInvoiceItem(newInvoiceItem, quotation);
                            }
                            if (persistInvoiceItem)
                                invoiceItemService.addOrUpdateInvoiceItem(newInvoiceItem);
                            provision.getInvoiceItems().add(newInvoiceItem);
                        }
                    }
                }
            }

            // Authorize gift for provider invoices
            for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                InvoiceItem tempInvoiceItem;
                if (invoiceItem.getId() != null) {
                    tempInvoiceItem = invoiceItemService.getInvoiceItem(invoiceItem.getId());
                } else
                    tempInvoiceItem = invoiceItem;

                if (tempInvoiceItem.getOriginProviderInvoice() != null) {
                    if (invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted()) {
                        invoiceItem.setPreTaxPrice(zeroValue);
                        if (!invoiceItem.getLabel().contains("(offert)"))
                            invoiceItem.setLabel(invoiceItem.getLabel() + " (offert)");
                        vatService.completeVatOnInvoiceItem(invoiceItem, quotation);
                        invoiceItem.setProvision(provision);
                        if (persistInvoiceItem)
                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                    }
                    if ((invoiceItem.getIsGifted() == null || !invoiceItem.getIsGifted())) {
                        tempInvoiceItem.setPreTaxPrice(tempInvoiceItem.getPreTaxPriceReinvoiced());
                        tempInvoiceItem.setLabel(invoiceItem.getLabel().replace(" (offert)", ""));
                        tempInvoiceItem.setProvision(provision);
                        vatService.completeVatOnInvoiceItem(tempInvoiceItem, quotation);

                        if (persistInvoiceItem)
                            invoiceItemService.addOrUpdateInvoiceItem(tempInvoiceItem);
                    }
                }
            }

            // Manage domiciliation fees
            if (provision.getDomiciliation() != null && provision.getDomiciliation().getDomiciliationFees() != null) {
                for (DomiciliationFee domiciliationFee : provision.getDomiciliation().getDomiciliationFees()) {
                    if (domiciliationFee.getId() != null) {
                        boolean found = false;
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem domiciliationInvoiceItem : provision.getInvoiceItems()) {
                                if (domiciliationInvoiceItem.getDomiciliationFee() != null && domiciliationInvoiceItem
                                        .getDomiciliationFee().getId().equals(domiciliationFee.getId())) {
                                    found = true;
                                    break;
                                }
                            }

                        if (!found) {
                            InvoiceItem invoiceItem = new InvoiceItem();
                            provision.getInvoiceItems().add(invoiceItem);
                            invoiceItem
                                    .setBillingItem(
                                            getAppliableBillingItem(domiciliationFee.getBillingType(), quotation));
                            invoiceItem.setInvoice(null);
                            invoiceItem.setProvision(provision);
                            if (invoiceItem.getIsOverridePrice() == null)
                                invoiceItem.setIsOverridePrice(true);
                            setInvoiceItemPreTaxPriceAndLabel(invoiceItem, invoiceItem.getBillingItem(), provision,
                                    quotation);
                            BigDecimal preTaxPrice = domiciliationFee.getAmount();
                            if (domiciliationFee.getBillingType().getIsOverrideVat() != null
                                    && domiciliationFee.getBillingType().getIsOverrideVat()) {
                                if (domiciliationFee.getBillingType().getVat() != null
                                        && domiciliationFee.getBillingType().getVat().getRate()
                                                .compareTo(zeroValue) > 0)
                                    preTaxPrice = preTaxPrice.divide((BigDecimal.ONE.add(domiciliationFee
                                            .getBillingType().getVat().getRate().divide(oneHundredValue))));
                            } else {
                                preTaxPrice = preTaxPrice.divide(new BigDecimal(1.2));
                            }
                            invoiceItem.setPreTaxPrice(preTaxPrice);
                            invoiceItem.setLabel(invoiceItem.getLabel() + " le "
                                    + domiciliationFee.getFeeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            computeInvoiceItemsVatAndDiscount(invoiceItem, quotation, provision);
                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                        }
                    }
                }
            }
        }
    }

    private boolean hasOption(BillingType billingType, Provision provision) throws OsirisException {
        if (Boolean.TRUE.equals(provision.getIsLogo())
                && billingType.getId().equals(constantService.getBillingTypeLogo().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsRedactedByJss())
                && billingType.getId().equals(constantService.getBillingTypeRedactedByJss().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBaloPackage())
                && billingType.getId().equals(constantService.getBillingTypeBaloPackage().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBaloNormalization())
                && billingType.getId().equals(constantService.getBillingTypeBaloNormalization().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBaloPublicationFlag())
                && billingType.getId().equals(constantService.getBillingTypeBaloPublicationFlag().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsPublicationReceipt())
                && billingType.getId().equals(constantService.getBillingTypePublicationReceipt().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsPublicationFlag())
                && billingType.getId().equals(constantService.getBillingTypePublicationFlag().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBodaccFollowup())
                && billingType.getId().equals(constantService.getBillingTypeBodaccFollowup().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBodaccFollowupAndRedaction())
                && billingType.getId().equals(constantService.getBillingTypeBodaccFollowupAndRedaction().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsNantissementDeposit())
                && billingType.getId().equals(constantService.getBillingTypeNantissementDeposit().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsSocialShareNantissementRedaction())
                && billingType.getId().equals(constantService.getBillingTypeSocialShareNantissementRedaction().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBusinnessNantissementRedaction())
                && billingType.getId().equals(constantService.getBillingTypeBusinnessNantissementRedaction().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsSellerPrivilegeRedaction())
                && billingType.getId().equals(constantService.getBillingTypeSellerPrivilegeRedaction().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsTreatmentMultipleModiciation())
                && billingType.getId().equals(constantService.getBillingTypeTreatmentMultipleModiciation().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsVacationMultipleModification())
                && billingType.getId().equals(constantService.getBillingTypeVacationMultipleModification().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsRegisterPurchase())
                && billingType.getId().equals(constantService.getBillingTypeRegisterPurchase().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsRegisterInitials())
                && billingType.getId().equals(constantService.getBillingTypeRegisterInitials().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsRegisterShippingCosts())
                && billingType.getId().equals(constantService.getBillingTypeRegisterShippingCosts().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsDisbursement())
                && billingType.getId().equals(constantService.getBillingTypeDisbursement().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsFeasibilityStudy())
                && billingType.getId().equals(constantService.getBillingTypeFeasibilityStudy().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsChronopostFees())
                && billingType.getId().equals(constantService.getBillingTypeChronopostFees().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsApplicationFees())
                && billingType.getId().equals(constantService.getBillingTypeApplicationFees().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBankCheque())
                && billingType.getId().equals(constantService.getBillingTypeBankCheque().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsComplexeFile())
                && billingType.getId().equals(constantService.getBillingTypeComplexeFile().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsBilan())
                && billingType.getId().equals(constantService.getBillingTypeBilan().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsEmergency())
                && billingType.getId().equals(constantService.getBillingTypeEmergency().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsDocumentScanning())
                && billingType.getId().equals(constantService.getBillingTypeDocumentScanning().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsRneUpdate())
                && billingType.getId().equals(constantService.getBillingTypeRneUpdate().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsVacationUpdateBeneficialOwners())
                && billingType.getId().equals(constantService.getBillingtypeVacationUpdateBeneficialOwners().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsFormalityAdditionalDeclaration())
                && billingType.getId().equals(constantService.getBillingtypeFormalityAdditionalDeclaration().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsCorrespondenceFees())
                && billingType.getId().equals(constantService.getBillingtypeCorrespondenceFees().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsPublicationPaper())
                && billingType.getId().equals(constantService.getBillingTypePublicationPaper().getId()))
            return true;

        if (isNotJssConfrere(provision)
                && billingType.getId().equals(constantService.getBillingTypeConfrereFees().getId()))
            return true;

        if (getPublicationPaperNbr(provision) > 0
                && billingType.getId().equals(constantService.getBillingTypeShippingCosts().getId()))
            return true;

        if (Boolean.TRUE.equals(provision.getIsSupplyFullBeCopy())
                && billingType.getId().equals(constantService.getBillingTypeSupplyFullBeCopy().getId()))
            return true;

        // Domiciliation pricing
        if (provision.getDomiciliation() != null
                && provision.getDomiciliation().getDomiciliationContractType() != null) {
            DomiciliationContractType contract = provision.getDomiciliation().getDomiciliationContractType();
            if (billingType.getId().equals(constantService.getBillingTypeDomiciliationContractTypeKeepMail().getId())
                    && contract.getId().equals(constantService.getDomiciliationContractTypeKeepMail().getId()))
                return true;
            if (billingType.getId().equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmail().getId())
                    && contract.getId().equals(constantService.getDomiciliationContractTypeRouteEmail().getId()))
                return true;
            if (billingType.getId()
                    .equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmailAndMail().getId())
                    && contract.getId().equals(constantService.getDomiciliationContractTypeRouteEmailAndMail().getId()))
                return true;
            if (billingType.getId().equals(constantService.getBillingTypeDomiciliationContractTypeRouteMail().getId())
                    && contract.getId().equals(constantService.getDomiciliationContractTypeRouteMail().getId()))
                return true;
        }

        return false;
    }

    private void computeInvoiceItemsVatAndDiscount(InvoiceItem invoiceItem, IQuotation quotation, Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        AssoSpecialOfferBillingType assoSpecialOfferBillingType = getAppliableSpecialOfferForProvision(
                invoiceItem.getBillingItem().getBillingType(), quotation);

        if (assoSpecialOfferBillingType != null) {
            if (assoSpecialOfferBillingType.getDiscountAmount() != null
                    && assoSpecialOfferBillingType.getDiscountAmount().compareTo(zeroValue) > 0)
                invoiceItem
                        .setDiscountAmount(assoSpecialOfferBillingType.getDiscountAmount().multiply(oneHundredValue)
                                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue));
            if (assoSpecialOfferBillingType.getDiscountRate() != null
                    && assoSpecialOfferBillingType.getDiscountRate().compareTo(zeroValue) > 0)
                invoiceItem.setDiscountAmount(
                        invoiceItem.getPreTaxPrice().multiply(assoSpecialOfferBillingType.getDiscountRate())
                                .divide(oneHundredValue).multiply(oneHundredValue)
                                .setScale(0, RoundingMode.HALF_EVEN)
                                .divide(oneHundredValue));
        }
        if (quotation.getVoucher() != null) {
            if (invoiceItem.getBillingItem() != null
                    && invoiceItem.getBillingItem().getBillingType() != null
                    && (Boolean.TRUE.equals(invoiceItem.getBillingItem().getBillingType().getIsVacation())
                            || Boolean.TRUE.equals(invoiceItem.getBillingItem().getBillingType().getIsTraitement()))) {
                // TODO créer un invoice item pour les coupons avec discount amount + condition
                // istraitement et isvacation ko
                // if (quotation.getVoucher().getDiscountAmount() != null
                // && quotation.getVoucher().getDiscountAmount().compareTo(zeroValue) > 0) {
                // BigDecimal voucherDiscount = quotation.getVoucher().getDiscountAmount()
                // .multiply(oneHundredValue)
                // .setScale(0, RoundingMode.HALF_EVEN)
                // .divide(oneHundredValue);

                // BigDecimal existingDiscount = invoiceItem.getDiscountAmount() != null
                // ? invoiceItem.getDiscountAmount()
                // : zeroValue;

                // invoiceItem.setDiscountAmount(existingDiscount.add(voucherDiscount));
                // }
                if (quotation.getVoucher().getDiscountRate() != null
                        && quotation.getVoucher().getDiscountRate().compareTo(zeroValue) > 0) {
                    invoiceItem.setDiscountAmount(
                            invoiceItem.getPreTaxPrice().multiply(quotation.getVoucher().getDiscountRate())
                                    .divide(oneHundredValue).multiply(oneHundredValue)
                                    .setScale(0, RoundingMode.HALF_EVEN)
                                    .divide(oneHundredValue));
                }
            } else {
                invoiceItem.setDiscountAmount(zeroValue);
            }
        }
        invoiceItem.setVat(invoiceItem.getBillingItem().getBillingType().getVat());
        vatService.completeVatOnInvoiceItem(invoiceItem, quotation);
    }

    public IQuotation completePricingOfIQuotation(IQuotation quotation, Boolean isEmergency)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0
                && quotation.getAssoAffaireOrders().get(0).getServices() != null
                && quotation.getAssoAffaireOrders().get(0).getServices().size() > 0
                && quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions() != null
                && quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().size() > 0) {
            quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().get(0)
                    .setIsEmergency(isEmergency);
        }
        quotation = getAndSetInvoiceItemsForQuotation(quotation, false);

        quotation.setResponsable(null);
        return quotation;
    }
}
