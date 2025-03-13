package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CharacterPrice;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationFee;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceTypeChosen;
import com.jss.osiris.modules.osiris.quotation.model.UserCustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

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
    DocumentService documentService;

    @Autowired
    VatService vatService;

    @Autowired
    CityService cityService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    AuditService auditService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Transactional
    public IQuotation getAndSetInvoiceItemsForQuotationForFront(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return getAndSetInvoiceItemsForQuotation(quotation, persistInvoiceItem);
    }

    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
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
                if (customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0) {
                    for (Quotation orderQuotation : customerOrder.getQuotations())
                        if (orderQuotation.getQuotationStatus().getCode()
                                .equals(QuotationStatus.VALIDATED_BY_CUSTOMER)) {
                            billingDate = orderQuotation.getCreatedDate().toLocalDate();
                        }
                }

                if (billingDate == null) {
                    List<Audit> audits = auditService.getAuditForEntity(CustomerOrder.class.getSimpleName(),
                            quotation.getId());
                    if (audits != null && audits.size() > 0) {
                        List<LocalDateTime> toBilledDate = new ArrayList<LocalDateTime>();
                        for (Audit audit : audits) {
                            if (audit.getFieldName().equals("customerOrderStatus")
                                    && audit.getNewValue().equals(CustomerOrderStatus.TO_BILLED)) {
                                toBilledDate.add(audit.getDatetime());
                            }
                        }
                        if (toBilledDate.size() > 0) {
                            toBilledDate.sort(new Comparator<LocalDateTime>() {
                                @Override
                                public int compare(LocalDateTime o1, LocalDateTime o2) {
                                    return o1.compareTo(o2);
                                }
                            });
                            billingDate = toBilledDate.get(0).toLocalDate();
                        }
                    }
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
            if (nbr > 0) {
                Confrere confrere = provision.getAnnouncement().getConfrere();
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
        } else if (invoiceItem.getId() == null && billingItem.getBillingType().getIsDebour()) {
            if (billingItem.getBillingType().getIsNonTaxable() == false
                    && provision.getService().getServiceType().getDefaultDeboursPrice() != null) {
                invoiceItem.setPreTaxPrice(provision.getService().getServiceType().getDefaultDeboursPrice());
            } else if (billingItem.getBillingType().getIsNonTaxable() == true
                    && provision.getService().getServiceType().getDefaultDeboursPriceNonTaxable() != null) {
                invoiceItem.setPreTaxPrice(provision.getService().getServiceType().getDefaultDeboursPriceNonTaxable());
            }
        } else {
            invoiceItem.setPreTaxPrice(billingItem.getPreTaxPrice());
        }

        // Domiciliation pricing
        // First one => double price for base
        if (quotation instanceof CustomerOrder && provision.getDomiciliation() != null && quotation.getId() != null) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotation.getId());
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

                        // If vacation multiple option, bypass vacation and traitement billing type
                        if (provision.getIsVacationMultipleModification() != null
                                && provision.getIsVacationMultipleModification() && billingType.getIsVacation() != null
                                && billingType.getIsVacation()) {
                            continue;
                        }
                        if (provision.getIsTreatmentMultipleModiciation() != null
                                && provision.getIsTreatmentMultipleModiciation()
                                && billingType.getIsTraitement() != null
                                && billingType.getIsTraitement()) {
                            continue;
                        }

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
        if (billingType.getId().equals(constantService.getBillingTypeLogo().getId()) && provision.getIsLogo() != null
                && provision.getIsLogo())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeRedactedByJss().getId())
                && provision.getIsRedactedByJss() != null && provision.getIsRedactedByJss())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBaloPackage().getId())
                && provision.getIsBaloPackage() != null && provision.getIsBaloPackage())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBaloNormalization().getId())
                && provision.getIsBaloNormalization() != null && provision.getIsBaloNormalization())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBaloPublicationFlag().getId())
                && provision.getIsBaloPublicationFlag() != null && provision.getIsBaloPublicationFlag())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypePublicationReceipt().getId())
                && provision.getIsPublicationReceipt() != null && provision.getIsPublicationReceipt())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypePublicationFlag().getId())
                && provision.getIsPublicationFlag() != null && provision.getIsPublicationFlag())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBodaccFollowup().getId())
                && provision.getIsBodaccFollowup() != null && provision.getIsBodaccFollowup())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBodaccFollowupAndRedaction().getId())
                && provision.getIsBodaccFollowupAndRedaction() != null && provision.getIsBodaccFollowupAndRedaction())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeNantissementDeposit().getId())
                && provision.getIsNantissementDeposit() != null && provision.getIsNantissementDeposit())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeSocialShareNantissementRedaction().getId())
                && provision.getIsSocialShareNantissementRedaction() != null
                && provision.getIsSocialShareNantissementRedaction())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBusinnessNantissementRedaction().getId())
                && provision.getIsBusinnessNantissementRedaction() != null
                && provision.getIsBusinnessNantissementRedaction())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeSellerPrivilegeRedaction().getId())
                && provision.getIsSellerPrivilegeRedaction() != null && provision.getIsSellerPrivilegeRedaction())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeTreatmentMultipleModiciation().getId())
                && provision.getIsTreatmentMultipleModiciation() != null
                && provision.getIsTreatmentMultipleModiciation())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeVacationMultipleModification().getId())
                && provision.getIsVacationMultipleModification() != null
                && provision.getIsVacationMultipleModification())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeRegisterPurchase().getId())
                && provision.getIsRegisterPurchase() != null && provision.getIsRegisterPurchase())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeRegisterInitials().getId())
                && provision.getIsRegisterInitials() != null && provision.getIsRegisterInitials())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeRegisterShippingCosts().getId())
                && provision.getIsRegisterShippingCosts() != null && provision.getIsRegisterShippingCosts())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeDisbursement().getId())
                && provision.getIsDisbursement() != null && provision.getIsDisbursement())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeFeasibilityStudy().getId())
                && provision.getIsFeasibilityStudy() != null && provision.getIsFeasibilityStudy())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeChronopostFees().getId())
                && provision.getIsChronopostFees() != null && provision.getIsChronopostFees())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeApplicationFees().getId())
                && provision.getIsApplicationFees() != null && provision.getIsApplicationFees())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBankCheque().getId())
                && provision.getIsBankCheque() != null && provision.getIsBankCheque())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeComplexeFile().getId())
                && provision.getIsComplexeFile() != null && provision.getIsComplexeFile())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeBilan().getId())
                && provision.getIsBilan() != null && provision.getIsBilan())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeEmergency().getId())
                && provision.getIsEmergency() != null && provision.getIsEmergency())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeComplexeFile().getId())
                && provision.getIsComplexeFile() != null && provision.getIsComplexeFile())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeDocumentScanning().getId())
                && provision.getIsDocumentScanning() != null && provision.getIsDocumentScanning())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeRneUpdate().getId())
                && provision.getIsRneUpdate() != null && provision.getIsRneUpdate())
            return true;
        if (billingType.getId().equals(constantService.getBillingtypeVacationUpdateBeneficialOwners().getId())
                && provision.getIsVacationUpdateBeneficialOwners() != null
                && provision.getIsVacationUpdateBeneficialOwners())
            return true;
        if (billingType.getId().equals(constantService.getBillingtypeFormalityAdditionalDeclaration().getId())
                && provision.getIsFormalityAdditionalDeclaration() != null
                && provision.getIsFormalityAdditionalDeclaration())
            return true;
        if (billingType.getId().equals(constantService.getBillingtypeCorrespondenceFees().getId())
                && provision.getIsCorrespondenceFees() != null && provision.getIsCorrespondenceFees())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypePublicationPaper().getId())
                && provision.getIsPublicationPaper() != null && provision.getIsPublicationPaper())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeConfrereFees().getId())
                && isNotJssConfrere(provision))
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeShippingCosts().getId())
                && getPublicationPaperNbr(provision) > 0)
            return true;
        if (billingType.getId().equals(constantService.getBillingTypeSupplyFullBeCopy().getId())
                && provision.getIsSupplyFullBeCopy() != null && provision.getIsSupplyFullBeCopy())
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
                                .divide(oneHundredValue).multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                                .divide(oneHundredValue));
        } else {
            invoiceItem.setDiscountAmount(zeroValue);
        }

        invoiceItem.setVat(invoiceItem.getBillingItem().getBillingType().getVat());
        vatService.completeVatOnInvoiceItem(invoiceItem, quotation);
    }

    public UserCustomerOrder completePricingOfUserCustomerOrder(UserCustomerOrder order)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        CustomerOrder customerOrder = new CustomerOrder();
        Responsable user = employeeService.getCurrentMyJssUser();
        ProvisionScreenType provisionScreenTypeAnnouncement = constantService.getProvisionScreenTypeAnnouncement();
        if (user == null)
            user = constantService.getResponsableDummyCustomerFrance();

        customerOrder.setResponsable(user);
        customerOrder.setAssoAffaireOrders(new ArrayList<AssoAffaireOrder>());
        customerOrder.getAssoAffaireOrders().add(new AssoAffaireOrder());
        customerOrder.getAssoAffaireOrders().get(0).setAffaire(order.getServiceTypes().get(0).getAffaire());

        if (user.getTiers() != null && user.getTiers().getSpecialOffers() != null)
            customerOrder.setSpecialOffers(user.getTiers().getSpecialOffers());

        List<InvoiceItem> invoiceItemsToConsider = new ArrayList<InvoiceItem>();

        for (ServiceTypeChosen serviceTypeChosen : order.getServiceTypes()) {
            serviceTypeChosen.setService(serviceTypeService.getServiceType(serviceTypeChosen.getService().getId()));
            Service service = serviceService.getServiceForMultiServiceTypesAndAffaire(
                    Arrays.asList(serviceTypeChosen.getService()), serviceTypeChosen.getAffaire());

            if (order.getIsEmergency() != null && order.getIsEmergency() && service.getProvisions() != null
                    && service.getProvisions().size() > 0
                    && order.getServiceTypes().indexOf(serviceTypeChosen) == 0)
                service.getProvisions().get(0).setIsEmergency(true);

            if (service != null && service.getProvisions() != null)
                for (Provision provision : service.getProvisions()) {
                    // map announcement
                    if (provision.getProvisionType().getProvisionScreenType().getId()
                            .equals(provisionScreenTypeAnnouncement.getId())) {
                        provision.setAnnouncement(new Announcement());
                        if (serviceTypeChosen.getAnnouncementProofReading() != null
                                && serviceTypeChosen.getAnnouncementProofReading())
                            provision.getAnnouncement().setIsProofReadingDocument(true);

                        if (serviceTypeChosen.getAnnouncementNoticeType() != null)
                            provision.getAnnouncement()
                                    .setNoticeTypes(Arrays.asList(serviceTypeChosen.getAnnouncementNoticeType()));

                        if (serviceTypeChosen.getAnnouncementNoticeFamily() != null)
                            provision.getAnnouncement()
                                    .setNoticeTypeFamily(serviceTypeChosen.getAnnouncementNoticeFamily());

                        if (serviceTypeChosen.getAnnouncementNotice() != null
                                && (serviceTypeChosen.getAnnouncementRedactedByJss() == null
                                        || serviceTypeChosen.getAnnouncementRedactedByJss()))
                            provision.getAnnouncement().setNotice(serviceTypeChosen.getAnnouncementNotice());
                        ;

                        if (serviceTypeChosen.getAnnouncementPublicationDate() != null)
                            provision.getAnnouncement()
                                    .setPublicationDate(serviceTypeChosen.getAnnouncementPublicationDate());

                        if (serviceTypeChosen.getAnnouncementRedactedByJss() != null
                                && serviceTypeChosen.getAnnouncementRedactedByJss())
                            provision.setIsRedactedByJss(true);
                    }

                    setInvoiceItemsForProvision(provision, customerOrder, false);
                    invoiceItemsToConsider.addAll(provision.getInvoiceItems());
                }
            serviceTypeChosen.setDiscountedAmount(assoAffaireOrderService.getServicePrice(service, true, false));
            serviceTypeChosen.setPreTaxPrice(assoAffaireOrderService.getServicePrice(service, false, false));
        }

        BigDecimal discountTotal = new BigDecimal(0);
        BigDecimal preTaxPriceTotal = new BigDecimal(0);
        BigDecimal vatTotal = new BigDecimal(0);
        if (invoiceItemsToConsider != null)
            for (InvoiceItem invoiceItem : invoiceItemsToConsider) {
                if (invoiceItem.getPreTaxPriceReinvoiced() != null) {
                    preTaxPriceTotal = preTaxPriceTotal
                            .add(invoiceItem.getPreTaxPriceReinvoiced());
                } else if (invoiceItem.getPreTaxPrice() != null) {
                    preTaxPriceTotal = preTaxPriceTotal.add(invoiceItem.getPreTaxPrice());
                }

                if (invoiceItem.getVatPrice() != null) {
                    vatTotal = vatTotal.add(invoiceItem.getVatPrice());
                }
                if (invoiceItem.getDiscountAmount() != null) {
                    discountTotal = discountTotal.add(invoiceItem.getDiscountAmount());
                }
            }

        order.setPreTaxPrice(preTaxPriceTotal.subtract(discountTotal));
        order.setTotalPrice(preTaxPriceTotal.add(vatTotal).subtract(discountTotal));
        order.setVatPrice(vatTotal);

        return order;
    }
}
