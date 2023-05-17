package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class PricingHelper {

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

    @Transactional
    public IQuotation getAndSetInvoiceItemsForQuotationForFront(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException {
        return getAndSetInvoiceItemsForQuotation(quotation, persistInvoiceItem);
    }

    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException {
        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions()) {
                        provision.setAssoAffaireOrder(assoAffaireOrder);
                        if (provision.getProvisionType() != null && provision.getProvisionType().getId() != null)
                            setInvoiceItemsForProvision(provision, quotation, persistInvoiceItem);
                    }
            }
        }
        return quotation;
    }

    private List<SpecialOffer> getAppliableSpecialOffersForQuotation(IQuotation quotation) {
        if (quotation != null) {
            if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0
                    || quotation.getOverrideSpecialOffer() != null && quotation.getOverrideSpecialOffer())
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

    public BillingItem getAppliableBillingItem(List<BillingItem> billingItems) {
        if (billingItems == null)
            return null;

        if (billingItems != null && billingItems.size() > 0)
            billingItems.sort(new Comparator<BillingItem>() {

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
                            + characterPriceService.getCharacterNumber(provision)
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
                Confrere confrere = provision.getAnnouncement().getConfrere();
                invoiceItem.setLabel(invoiceItem.getLabel() + " (quantité : " + nbr + ")");

                invoiceItem.setPreTaxPrice((confrere.getPaperPrice() != null ? confrere.getPaperPrice() : 0f) * nbr);
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypeConfrereFees().getId())) {
            // If it's an announcement published by a Confrere, apply additionnal fees and
            // JSS markup
            invoiceItem.setPreTaxPrice(0f);

            // Check if we have a character based price announcement
            boolean hasPriceBasedProvisionType = false;
            if (provision.getProvisionType() != null && provision.getProvisionType().getBillingTypes() != null)
                for (BillingType otherBillingType : provision.getProvisionType().getBillingTypes())
                    if (otherBillingType.getIsPriceBasedOnCharacterNumber())
                        hasPriceBasedProvisionType = true;

            if (isNotJssConfrere(provision) && hasPriceBasedProvisionType) {
                CharacterPrice characterPrice = characterPriceService.getCharacterPrice(provision);
                Float price = 0f;
                if (characterPrice != null) {
                    price = characterPrice.getPrice()
                            * characterPriceService.getCharacterNumber(provision);
                }

                Float additionnalFees = 0f;
                Confrere confrere = provision.getAnnouncement().getConfrere();
                if (confrere.getAdministrativeFees() != null)
                    additionnalFees += confrere.getAdministrativeFees();

                invoiceItem.setPreTaxPrice(
                        (price != null ? price : 0f)
                                * ((confrere.getReinvoicing() != null ? confrere.getReinvoicing() : 0f) / 100)
                                + additionnalFees);
            }
        } else if (billingItem.getBillingType().getId()
                .equals(constantService.getBillingTypeShippingCosts().getId())) {
            invoiceItem.setPreTaxPrice(0f);
            Integer nbr = getPublicationPaperNbr(provision);
            Confrere confrere = provision.getAnnouncement().getConfrere();
            if (nbr > 0)
                invoiceItem.setPreTaxPrice(Math
                        .round(((confrere.getShippingCosts() != null ? confrere.getShippingCosts() : 0f)) * 100f)
                        / 100f);
        } else if ((billingItem.getBillingType().getIsDebour() || billingItem.getBillingType().getIsFee())
                && provision.getDebours() != null && provision.getDebours().size() > 0) {
            // Compute debour prices
            Float total = 0f;
            for (Debour debour : provision.getDebours()) {
                Float debourAmount = debour.getInvoicedAmount() != null ? debour.getInvoicedAmount()
                        : debour.getDebourAmount();
                if (debour.getBillingType().getIsNonTaxable())
                    total += debourAmount;
                else
                    total += debourAmount / ((100 + constantService.getVatDeductible().getRate()) / 100f);
            }
            invoiceItem.setPreTaxPrice(total);
        } else {
            invoiceItem.setPreTaxPrice(billingItem.getPreTaxPrice());
        }

        if (invoiceItem.getPreTaxPrice() != null)
            invoiceItem.setPreTaxPrice(Math.round(invoiceItem.getPreTaxPrice() * 100f) / 100f);

        if (invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted()) {
            invoiceItem.setPreTaxPrice(0f);
            invoiceItem.setDiscountAmount(0f);
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

    private void setInvoiceItemsForProvision(Provision provision, IQuotation quotation, boolean persistInvoiceItem)
            throws OsirisException, OsirisClientMessageException {

        if (quotation != null && provision != null) {
            if (provision.getInvoiceItems() == null)
                provision.setInvoiceItems(new ArrayList<InvoiceItem>());

            // If billed, do not change items
            if (provision.getInvoiceItems().size() > 0 && provision.getInvoiceItems().get(0).getInvoice() != null
                    && !provision.getInvoiceItems().get(0).getInvoice().getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCancelled().getId())
                    && !provision.getInvoiceItems().get(0).getInvoice().getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCreditNoteEmited().getId()))
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

                            if (!invoiceItem.getIsOverridePrice() || !billingType.getCanOverridePrice()
                                    || invoiceItem.getPreTaxPrice() == null
                                    || invoiceItem.getPreTaxPrice() <= 0
                                    || invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted()
                                    || (billingItem.getBillingType().getIsDebour()
                                            || billingItem.getBillingType().getIsFee())
                                            && provision.getDebours() != null && provision.getDebours().size() > 0)
                                setInvoiceItemPreTaxPriceAndLabel(invoiceItem, billingItem, provision);
                            computeInvoiceItemsVatAndDiscount(invoiceItem, quotation, provision);

                            if (invoiceItem.getPreTaxPrice() == null)
                                invoiceItem.setPreTaxPrice(0f);

                            if (persistInvoiceItem)
                                invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                        }
                    }
                }

                // Delete unused item
                ArrayList<InvoiceItem> invoiceItemsDeleted = new ArrayList<InvoiceItem>();
                ArrayList<Integer> billingTypeAlreadyFound = new ArrayList<Integer>();
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
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
        if (billingType.getId().equals(constantService.getBillingTypeEmergency().getId())
                && provision.getIsEmergency() != null && provision.getIsEmergency())
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

        return false;
    }

    private void computeInvoiceItemsVatAndDiscount(InvoiceItem invoiceItem, IQuotation quotation, Provision provision)
            throws OsirisException, OsirisClientMessageException {
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

        if (customerOrder.getCity() == null)
            throw new OsirisClientMessageException(
                    "Aucune ville trouvée sur le donneur d'ordre pour générer le prix. Merci de compléter le donneur d'ordre");

        // If document not found or document indicate to use it, take customer order as
        // default

        // No VAT abroad (France and Monaco)
        Country country = null;
        City city = null;
        if (billingDocument == null || billingDocument.getBillingLabelType() == null
                || billingDocument.getBillingLabelType().getId()
                        .equals(constantService.getBillingLabelTypeCustomer().getId())) {
            country = customerOrder.getCountry();
            city = cityService.getCity(customerOrder.getCity().getId());
        } else if (billingDocument.getBillingLabelType().getId()
                .equals(constantService.getBillingLabelTypeCodeAffaire().getId())) {
            Affaire affaire = invoiceItem.getProvision().getAssoAffaireOrder().getAffaire();
            city = affaire.getCity();
            country = affaire.getCountry();
        } else {
            if (billingDocument.getBillingLabelCountry() == null)
                throw new OsirisClientMessageException(
                        "Pays non trouvé dans l'adresse indiquée dans la configuration de facturation de la commande");
            if (billingDocument.getBillingLabelCountry() == null)
                throw new OsirisClientMessageException(
                        "Pays non trouvé dans l'adresse indiquée dans la configuration de facturation de la commande");
            city = billingDocument.getBillingLabelCity();
            if (city != null && city.getId() != null)
                city = cityService.getCity(city.getId());
            country = billingDocument.getBillingLabelCountry();
        }

        if (country != null && !country.getId().equals(constantService.getCountryFrance().getId())
                && !country.getId().equals(constantService.getCountryMonaco().getId())) {
            vat = null;
        } else if (invoiceItem.getBillingItem() != null && invoiceItem.getBillingItem().getBillingType() != null
                && invoiceItem.getBillingItem().getBillingType().getIsOverrideVat()) {
            vat = invoiceItem.getBillingItem().getBillingType().getVat();
        } else if (city != null) {
            vat = vatService.getGeographicalApplicableVat(country, city.getDepartment());
        }

        if (vat != null && (invoiceItem.getIsGifted() == null || !invoiceItem.getIsGifted())) {
            Float vatPrice = 0f;
            if (provision.getDebours() != null && provision.getDebours().size() > 0
                    && (invoiceItem.getBillingItem().getBillingType().getIsDebour()
                            || invoiceItem.getBillingItem().getBillingType().getIsFee())) {
                vatPrice = 0f;
                for (Debour debour : provision.getDebours())
                    if (!debour.getBillingType().getIsNonTaxable()) {
                        Float debourAmount = debour.getInvoicedAmount() != null ? debour.getInvoicedAmount()
                                : debour.getDebourAmount();
                        vatPrice += (constantService.getVatDeductible().getRate() / 100f) * debourAmount
                                / ((100 + constantService.getVatDeductible().getRate()) / 100f);
                    }
            } else {
                vatPrice = vat.getRate() / 100
                        * ((invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0)
                                - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0));
            }
            invoiceItem.setVatPrice(Math.round(vatPrice * 100f) / 100f);
            invoiceItem.setVat(vat);
        } else {
            invoiceItem.setVatPrice(0f);
        }

    }
}
