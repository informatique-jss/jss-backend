package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
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

    @Autowired
    EmployeeService employeeService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    CityService cityService;

    @Autowired
    CentralPayDelegateService centralPayDelegateService;

    @Value("${payment.cb.redirect.quotation.deposit.entry.point}")
    private String paymentCbRedirectDepositQuotation;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (quotation.isPresent())
            return quotation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationFromUser(Quotation quotation) throws OsirisException {
        return addOrUpdateQuotation(quotation);
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) throws OsirisException {
        quotation.setIsQuotation(true);

        if (quotation.getDocuments() != null)
            for (Document document : quotation.getDocuments()) {
                document.setQuotation(quotation);
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
            }

        // Complete provisions
        for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
            assoAffaireOrder.setQuotation(quotation);
            assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, quotation);
        }

        boolean isNewQuotation = quotation.getId() == null;
        if (isNewQuotation) {
            quotation.setCreatedDate(LocalDateTime.now());
            quotation = quotationRepository.save(quotation);
        }

        quotation = quotationRepository.save(quotation);

        getAndSetInvoiceItemsForQuotation(quotation, true);

        quotation = getQuotation(quotation.getId());

        indexEntityService.indexEntity(quotation, quotation.getId());

        if (isNewQuotation) {
            notificationService.notifyNewQuotation(quotation);

            if (quotation.getIsCreatedFromWebSite())
                mailHelper.sendQuotationCreationConfirmationToCustomer(quotation);
        }
        return quotation;
    }

    @Override
    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException {
        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions()) {
                        provision.setAssoAffaireOrder(assoAffaireOrder);
                        if (provision.getProvisionType() != null)
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

    private void setInvoiceItemPreTaxPriceAndLabel(InvoiceItem invoiceItem, BillingItem billingItem,
            Provision provision) throws OsirisException {
        invoiceItem.setLabel(billingItem.getBillingType().getLabel());
        if (billingItem.getBillingType().getIsPriceBasedOnCharacterNumber()) {
            CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
            if (characterPrice != null) {
                Float price = characterPrice.getPrice()
                        * characterPriceService.getCharacterNumber(provision);
                invoiceItem.setPreTaxPrice(Math.round(price * 100f) / 100f);

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
                            + characterPriceService.getCharacterNumber(provision)
                            + " caractères, rubrique " + noticeFamiliyType + ", sous-rubrique(s) "
                            + String.join(", ", noticeTypes) + ")");
                else
                    invoiceItem.setLabel(invoiceItem.getLabel() + " ("
                            + characterPriceService.getCharacterNumber(provision) + ")");

                if (provision.getAnnouncement().getDepartment() != null)
                    invoiceItem.setLabel(
                            invoiceItem.getLabel() + " - " + provision.getAnnouncement().getDepartment().getCode());

            } else {
                invoiceItem.setPreTaxPrice(0f);
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypePublicationPaper().getId())) {
            Integer nbr = getPublicationPaperNbr(provision);
            if (nbr > 0) {
                invoiceItem.setLabel(invoiceItem.getLabel() + " (quantité : " + nbr + ")");

                Confrere confrere = provision.getAnnouncement().getConfrere();
                if (isNotJssConfrere(provision) && confrere.getPaperPrice() != null) {
                    Float confrerePrice = confrere.getPaperPrice() * nbr;
                    if (confrere.getShippingCosts() != null)
                        confrerePrice += confrere.getShippingCosts();

                    invoiceItem.setPreTaxPrice(confrerePrice * (1 + confrere.getReinvoicing() / 100));
                } else
                    invoiceItem.setPreTaxPrice(
                            Math.round(billingItem.getPreTaxPrice() * nbr * 100f) / 100f);
            }
        } else {
            invoiceItem.setPreTaxPrice(Math.round(billingItem.getPreTaxPrice() * 100f) / 100f);
        }

        // If it's an announcement published by a Confrere, apply additionnal fees and
        // JSS markup
        if (isNotJssConfrere(provision)) {
            Float additionnalFees = 0f;
            Confrere confrere = provision.getAnnouncement().getConfrere();
            if (confrere.getAdministrativeFees() != null)
                additionnalFees += confrere.getAdministrativeFees();

            invoiceItem.setPreTaxPrice(
                    invoiceItem.getPreTaxPrice() + additionnalFees * (1 + confrere.getReinvoicing() / 100));

        }

        if (invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted()) {
            invoiceItem.setPreTaxPrice(0f);
            invoiceItem.setLabel(invoiceItem.getLabel() + " (offert)");
        }

    }

    private boolean isNotJssConfrere(Provision provision) throws OsirisException {
        return provision.getAnnouncement() != null && provision.getAnnouncement().getConfrere() != null
                && !provision.getAnnouncement().getConfrere().getId()
                        .equals(constantService.getConfrereJssPaper().getId())
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

    private void setInvoiceItemsForProvision(Provision provision, IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException {

        if (quotation != null && provision != null) {
            if (provision.getInvoiceItems() == null)
                provision.setInvoiceItems(new ArrayList<InvoiceItem>());

            // If billed, do not change items
            if (provision.getInvoiceItems().size() > 0 && provision.getInvoiceItems().get(0).getInvoice() != null
                    && !provision.getInvoiceItems().get(0).getInvoice().getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCancelled().getId()))
                return;

            ProvisionType provisionType = provisionTypeService.getProvisionType(provision.getProvisionType().getId());
            if (provisionType != null) {
                for (BillingType billingType : provisionType.getBillingTypes()) {
                    List<BillingItem> billingItems = billingItemService.getBillingItemByBillingType(billingType);
                    if (billingItems != null && billingItems.size() > 0) {
                        BillingItem billingItem = getAppliableBillingItem(billingItems);

                        if (billingItem != null && billingType.getAccountingAccountProduct() != null
                                && (!billingItem.getBillingType().getIsOptionnal()
                                        || hasOption(billingItem.getBillingType(), provision))) {

                            InvoiceItem invoiceItem = null;

                            for (InvoiceItem invoiceItemProvision : provision.getInvoiceItems())
                                if (invoiceItemProvision.getBillingItem() != null
                                        && invoiceItemProvision.getBillingItem().getBillingType().getId()
                                                .equals(billingType.getId()))
                                    invoiceItem = invoiceItemProvision;

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

                            if (!invoiceItem.getIsOverridePrice() || invoiceItem.getPreTaxPrice() == null
                                    || invoiceItem.getPreTaxPrice() <= 0
                                    || invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted())
                                setInvoiceItemPreTaxPriceAndLabel(invoiceItem, billingItem, provision);
                            computeInvoiceItemsVatAndDiscount(invoiceItem, quotation);

                            if (persistInvoiceItem)
                                invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                        }
                    }
                }

                // Delete unused item
                ArrayList<InvoiceItem> invoiceItemsDeleted = new ArrayList<InvoiceItem>();
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    boolean found = false;
                    for (BillingType billingType : provisionType.getBillingTypes()) {
                        if (invoiceItem.getBillingItem() != null
                                && billingType.getId().equals(invoiceItem.getBillingItem().getBillingType().getId())
                                && (!billingType.getIsOptionnal()
                                        || hasOption(billingType, provision)))
                            found = true;
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
                provision.getInvoiceItems().removeAll(invoiceItemsDeleted);
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
        if (billingType.getId().equals(constantService.getBillingTypeDocumentScanning().getId())
                && provision.getIsDocumentScanning() != null && provision.getIsDocumentScanning())
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
        if (billingType.getId().equals(constantService.getBillingTypeEmergency().getId())
                && provision.getIsEmergency() != null && provision.getIsEmergency())
            return true;
        if (billingType.getId().equals(constantService.getBillingTypePublicationPaper().getId())
                && provision.getIsPublicationPaper() != null && provision.getIsPublicationPaper())
            return true;

        return false;
    }

    private void computeInvoiceItemsVatAndDiscount(InvoiceItem invoiceItem, IQuotation quotation)
            throws OsirisException {
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
                City city = cityService.getCity(customerOrder.getCity().getId());
                vat = vatService.getGeographicalApplicableVat(customerOrder.getCountry(),
                        city.getDepartment(),
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
            Float vatPrice = vat.getRate() / 100
                    * ((invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0)
                            - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0));
            invoiceItem.setVatPrice(Math.round(vatPrice * 100f) / 100f);
            invoiceItem.setVat(vat);
        } else {
            invoiceItem.setVatPrice(0f);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode)
            throws OsirisException {
        QuotationStatus targetQuotationStatus = quotationStatusService.getQuotationStatusByCode(targetStatusCode);
        if (targetQuotationStatus == null)
            throw new OsirisException(null, "Quotation status not found for code " + targetStatusCode);
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN)
                && targetQuotationStatus.getCode().equals(QuotationStatus.TO_VERIFY))
            notificationService.notifyQuotationToVerify(quotation);
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.TO_VERIFY)
                && targetQuotationStatus.getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            mailHelper.sendQuotationToCustomer(quotation, false);
            notificationService.notifyQuotationSent(quotation);
        }
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                && targetQuotationStatus.getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)) {
            CustomerOrder customerOrder = customerOrderService.createNewCustomerOrderFromQuotation(quotation);
            if (customerOrder == null)
                throw new OsirisException(null,
                        "Erreur when createing customer order from quotation " + quotation.getId());
            if (quotation.getCustomerOrders() == null)
                quotation.setCustomerOrders(new ArrayList<CustomerOrder>());
            quotation.getCustomerOrders().add(customerOrder);
            notificationService.notifyQuotationValidatedByCustomer(quotation);
            customerOrder.setQuotations(new ArrayList<Quotation>());
            customerOrder.getQuotations().add(quotation);
            customerOrderService.generateStoreAndSendPublicationReceipt(customerOrder);
        }
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                && targetQuotationStatus.getCode().equals(QuotationStatus.REFUSED_BY_CUSTOMER))
            notificationService.notifyQuotationRefusedByCustomer(quotation);

        quotation.setLastStatusUpdate(LocalDateTime.now());
        quotation.setQuotationStatus(targetQuotationStatus);
        this.addOrUpdateQuotation(quotation);

        return quotation;
    }

    @Override
    public ITiers getCustomerOrderOfQuotation(IQuotation quotation) throws OsirisException {
        if (quotation.getConfrere() != null)
            return quotation.getConfrere();
        if (quotation.getResponsable() != null)
            return quotation.getResponsable();
        if (quotation.getTiers() != null)
            return quotation.getTiers();

        throw new OsirisException(null, "No customer order declared on IQuotation " + quotation.getId());
    }

    @Override
    public List<QuotationSearchResult> searchQuotations(QuotationSearch quotationSearch) {
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        if (quotationSearch.getQuotationStatus() != null && quotationSearch.getQuotationStatus().size() > 0) {
            for (QuotationStatus customerOrderStatus : quotationSearch.getQuotationStatus())
                statusId.add(customerOrderStatus.getId());
        } else {
            statusId.add(0);
        }

        ArrayList<Integer> salesEmployeeId = new ArrayList<Integer>();
        if (quotationSearch.getSalesEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(quotationSearch.getSalesEmployee()))
                salesEmployeeId.add(employee.getId());
        } else {
            salesEmployeeId.add(0);
        }

        ArrayList<Integer> customerOrderId = new ArrayList<Integer>();
        if (quotationSearch.getCustomerOrders() != null && quotationSearch.getCustomerOrders().size() > 0) {
            for (ITiers tiers : quotationSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        ArrayList<Integer> affaireId = new ArrayList<Integer>();
        if (quotationSearch.getAffaires() != null && quotationSearch.getAffaires().size() > 0) {
            for (Affaire affaire : quotationSearch.getAffaires())
                affaireId.add(affaire.getId());
        } else {
            affaireId.add(0);
        }

        return quotationRepository.findQuotations(
                salesEmployeeId,
                statusId,
                quotationSearch.getStartDate().withHour(0).withMinute(0),
                quotationSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId);
    }

    @Override
    public void reindexQuotation() {
        List<Quotation> quotations = IterableUtils.toList(quotationRepository.findAll());
        if (quotations != null)
            for (Quotation quotation : quotations)
                indexEntityService.indexEntity(quotation, quotation.getId());
    }

    @Override
    public String getCardPaymentLinkForQuotationDeposit(Quotation quotation, String mail, String subject)
            throws OsirisException {
        return getCardPaymentLinkForQuotationPayment(quotation, mail, subject, paymentCbRedirectDepositQuotation);
    }

    @Override
    public Boolean validateCardPaymentLinkForQuotationDeposit(Quotation quotation)
            throws OsirisException {
        if (quotation.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(quotation.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (quotation.getQuotationStatus().getCode()
                            .equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        unlockQuotationFromDeposit(quotation, centralPayPaymentRequest.getTotalAmount() / 100f);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private String getCardPaymentLinkForQuotationPayment(Quotation quotation, String mail, String subject,
            String redirectEntrypoint)
            throws OsirisException {

        if (!quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER))
            throw new OsirisException(null, "Wrong status to pay for quotation n°" + quotation.getId());

        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
            if (quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0)
                return customerOrderService
                        .getCardPaymentLinkForCustomerOrderDeposit(quotation.getCustomerOrders().get(0), mail, subject);
            else
                return "ok";

        if (quotation.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(quotation.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                    centralPayDelegateService.cancelPaymentRequest(quotation.getCentralPayPaymentRequestId());

                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {
                    unlockQuotationFromDeposit(quotation, centralPayPaymentRequest.getTotalAmount() / 100f);
                    return "ok";
                }
            }
        }

        Float remainingToPay = computeQuotationTotalPrice(quotation);

        if (remainingToPay > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    remainingToPay, mail,
                    quotation.getId() + "", subject);

            quotation.setCentralPayPaymentRequestId(paymentRequest.getPaymentRequestId());
            addOrUpdateQuotation(quotation);
            return paymentRequest.getBreakdowns().get(0).getEndpoint()
                    + "?urlRedirect=" + redirectEntrypoint + "?quotationId=" + quotation.getId() + "&delay=0";
        }
        return "ok";
    }

    private Quotation unlockQuotationFromDeposit(Quotation quotation, Float effectivePayment)
            throws OsirisException {
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            addOrUpdateQuotationStatus(quotation, QuotationStatus.VALIDATED_BY_CUSTOMER);
            notificationService.notifyQuotationValidatedByCustomer(quotation);

            quotation.getCustomerOrders().get(0).setCentralPayPendingPaymentAmount(effectivePayment);
            customerOrderService.addOrUpdateCustomerOrder(quotation.getCustomerOrders().get(0));
        }
        return quotation;
    }

    private Float computeQuotationTotalPrice(IQuotation quotation) {
        // Compute prices
        Float preTaxPriceTotal = 0f;
        Float discountTotal = null;
        Float vatTotal = 0f;

        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            for (Provision provision : asso.getProvisions()) {
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    preTaxPriceTotal += invoiceItem.getPreTaxPrice();
                    if (invoiceItem.getDiscountAmount() != null && invoiceItem.getDiscountAmount() > 0) {
                        if (discountTotal == null)
                            discountTotal = invoiceItem.getDiscountAmount();
                        else
                            discountTotal += invoiceItem.getDiscountAmount();
                    }
                    if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                            && invoiceItem.getVatPrice() > 0) {
                        vatTotal += invoiceItem.getVatPrice();
                    }
                }
            }
        }

        return preTaxPriceTotal - (discountTotal != null ? discountTotal : 0) + vatTotal;
    }

    public void sendRemindersForQuotation() throws OsirisException {
        List<Quotation> quotations = quotationRepository.findQuotationForReminder(
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        if (quotations != null && quotations.size() > 0)
            for (Quotation quotation : quotations) {
                // if only annonce légale
                boolean isOnlyAnnonceLegal = true;
                if (quotation.getAssoAffaireOrders() != null)
                    loopAsso: for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                        if (asso.getProvisions() != null)
                            for (Provision provision : asso.getProvisions())
                                if (provision.getAnnouncement() == null) {
                                    isOnlyAnnonceLegal = false;
                                    break loopAsso;
                                }

                boolean toSend = false;
                if (isOnlyAnnonceLegal) {
                    if (quotation.getFirstReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1))) {
                        toSend = true;
                        quotation.setFirstReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getSecondReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(2))) {
                        toSend = true;
                        quotation.setSecondReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(7))) {
                        toSend = true;
                        quotation.setThirdReminderDateTime(LocalDateTime.now());
                    }
                } else {
                    if (quotation.getFirstReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                        toSend = true;
                        quotation.setFirstReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getSecondReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(3 * 7))) {
                        toSend = true;
                        quotation.setSecondReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(6 * 7))) {
                        toSend = true;
                        quotation.setThirdReminderDateTime(LocalDateTime.now());
                    }
                }

                if (toSend) {
                    mailHelper.sendQuotationToCustomer(quotation, false);
                    addOrUpdateQuotation(quotation);
                }
            }

    }
}
