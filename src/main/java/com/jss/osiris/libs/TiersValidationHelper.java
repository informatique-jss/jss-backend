package com.jss.osiris.libs;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@Service
public class TiersValidationHelper {

    @Autowired
    private ValidationHelper validationHelper;

    @Autowired
    private ConstantService constantService;

    public Boolean validateTiers(Tiers tiers) throws OsirisException {
        validationHelper.validateReferential(tiers.getTiersType(), true, "TiersType");

        if (tiers.getIsNewTiers() == null)
            tiers.setIsNewTiers(false);

        if (tiers.getIsIndividual()) {
            validationHelper.validateReferential(tiers.getCivility(), true, "Civility");
            tiers.setDenomination(null);
            validationHelper.validateString(tiers.getFirstname(), true, 40, "Firstname");
            validationHelper.validateString(tiers.getLastname(), true, 40, "Lastname");
            if (tiers.getLastname() != null)
                tiers.setLastname(tiers.getLastname().toUpperCase());
        } else {
            tiers.setFirstname(null);
            tiers.setLastname(null);
            if (!validationHelper.validateSiret(tiers.getSiret()))
                tiers.setSiret(null);

            validationHelper.validateString(tiers.getDenomination(), true, 80, "Denomination");
            if (tiers.getIntercommunityVat() != null && tiers.getIntercommunityVat().length() > 20)
                throw new OsirisValidationException("IntercommunityVat");
        }

        // validationHelper.validateReferential(tiers.getTiersCategory(), true,
        // "TiersCategory");
        validationHelper.validateReferential(tiers.getSalesEmployee(), true,
                "SalesEmployee");
        validationHelper.validateReferential(tiers.getDefaultCustomerOrderEmployee(),
                false,
                "DefaultCustomerOrderEmployee");
        validationHelper.validateReferential(tiers.getFormalisteEmployee(), false,
                "FormalisteEmployee");
        validationHelper.validateReferential(tiers.getInsertionEmployee(), false,
                "InsertionEmployee");
        validationHelper.validateReferential(tiers.getLanguage(), true, "Language");
        validationHelper.validateReferential(tiers.getDeliveryService(), true,
                "DeliveryService");

        validationHelper.validateString(tiers.getAddress(), true, 100, "Address");
        validationHelper.validateReferential(tiers.getCountry(), true, "Country");
        if (tiers.getCountry() != null
                && tiers.getCountry().getId().equals(constantService.getCountryFrance().getId()))
            validationHelper.validateString(tiers.getPostalCode(), true, 10, "PostalCode");
        validationHelper.validateString(tiers.getCedexComplement(), false, 20, "CedexComplement");
        validationHelper.validateReferential(tiers.getCity(), true, "City");

        validationHelper.validateString(tiers.getIntercom(), false, 12, "Intercom");

        validationHelper.validateReferential(tiers.getRffFrequency(),
                tiers.getRffFormaliteRate() != null && tiers.getRffFormaliteRate().compareTo(BigDecimal.ZERO) > 0
                        || tiers.getRffInsertionRate() != null
                                && tiers.getRffInsertionRate().compareTo(BigDecimal.ZERO) > 0,
                "rffFrequency");

        if (tiers.getRffFrequency() != null
                && !tiers.getRffFrequency().getId().equals(constantService.getRffFrequencyAnnual().getId()))
            if (tiers.getTiersCategory() == null
                    || !tiers.getTiersCategory().getId().equals(constantService.getTiersCategoryPresse().getId()))
                throw new OsirisClientMessageException("La périodicité des RFF ne peut être que annuelle");

        if (tiers.getSpecialOffers() != null) {
            for (SpecialOffer specialOffer : tiers.getSpecialOffers()) {
                validationHelper.validateReferential(specialOffer, false, "specialOffer");
            }
        }

        if (tiers.getMails() != null && tiers.getMails().size() > 0) {
            if (!validationHelper.validateMailList(tiers.getMails()))
                throw new OsirisValidationException("Mails");
        }

        if (tiers.getPhones() != null && tiers.getPhones().size() > 0) {
            for (Phone phone : tiers.getPhones()) {
                if (!validationHelper.validateFrenchPhone(phone.getPhoneNumber())
                        && !validationHelper.validateInternationalPhone(phone.getPhoneNumber()))
                    throw new OsirisValidationException("Phones");
            }
        }

        if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
            for (Document document : tiers.getDocuments()) {

                validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

                if (document.getMailsAffaire() != null
                        && !validationHelper.validateMailList(document.getMailsAffaire()))
                    throw new OsirisValidationException("MailsAffaire");
                if (document.getMailsClient() != null && document.getMailsClient() != null
                        && document.getMailsClient().size() > 0)
                    if (!validationHelper.validateMailList(document.getMailsClient()))
                        throw new OsirisValidationException("MailsClient");

                validationHelper.validateString(document.getAffaireAddress(), false, 200, "AffaireAddress");
                validationHelper.validateString(document.getClientAddress(), false, 100, "ClientAddress");
                validationHelper.validateString(document.getAffaireRecipient(), false, 100, "AffaireRecipient");
                validationHelper.validateString(document.getClientRecipient(), false, 200, "ClientRecipient");
                validationHelper.validateString(document.getCommandNumber(), false, 40, "CommandNumber");
                validationHelper.validateReferential(document.getPaymentDeadlineType(), false, "PaymentDeadlineType");
                validationHelper.validateReferential(document.getRefundType(), false, "RefundType");
                validationHelper.validateIban(document.getRefundIBAN(), false, "RefundIBAN");
                validationHelper.validateBic(document.getRefundBic(), false, "RefundBic");
                validationHelper.validateReferential(document.getBillingClosureType(), false, "BillingClosureType");
                validationHelper.validateReferential(document.getBillingClosureRecipientType(), false,
                        "BillingClosureRecipientType");
                validationHelper.validateReferential(document.getBillingLabelCity(), false, "BillingLabelCity");
                validationHelper.validateReferential(document.getBillingLabelCountry(), false, "BillingLabelCountry");
                validationHelper.validateString(document.getBillingAddress(), false, 200, "BillingAddress");
                validationHelper.validateString(document.getBillingLabel(), false, 200, "BillingLabel");
                validationHelper.validateString(document.getBillingPostalCode(), false, 10, "BillingPostalCode");
                validationHelper.validateString(document.getCedexComplement(), false, 20, "CedexComplement");

                if (document.getIsRecipientAffaire() == null)
                    document.setIsRecipientAffaire(false);
                if (document.getIsRecipientClient() == null)
                    document.setIsRecipientClient(false);

                if (document.getPaymentDeadlineType() == null) {
                    document.setPaymentDeadlineType(constantService.getPaymentDeadLineType30());
                }

                if (document.getDocumentType() != null
                        && document.getDocumentType().getId() == constantService.getDocumentTypeBilling().getId()
                        && document.getBillingLabelType() == null) {
                    document.setBillingLabelType(constantService.getBillingLabelTypeCodeAffaire());
                }

                if (document.getDocumentType() != null
                        && document.getDocumentType().getId() == constantService.getDocumentTypeRefund().getId()
                        && document.getRefundType() == null) {
                    document.setRefundType(constantService.getRefundTypeVirement());
                }
            }
        }

        validationHelper.validateReferential(tiers.getPaymentType(),
                !tiers.getTiersType().getId().equals(constantService.getTiersTypeProspect().getId()), "PaymentType");
        validationHelper.validateIban(tiers.getPaymentIban(), false, "PaymentIBAN");
        validationHelper.validateBic(tiers.getPaymentBic(), false, "PaymentBic");
        validationHelper.validateIban(tiers.getRffIban(), false, "RffIBAN");
        validationHelper.validateBic(tiers.getRffBic(), false, "RffBic");
        if (tiers.getRffMail() != null && tiers.getRffMail().length() > 0
                && !validationHelper.validateMail(tiers.getRffMail()))
            throw new OsirisValidationException("Mails rff");

        if (tiers.getPaymentType() != null
                && tiers.getPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId())) {
            validationHelper.validateDate(tiers.getSepaMandateSignatureDate(), true, "SepaMandateSignatureDate");
        }

        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {

                if (responsable.getMail() != null) {
                    if (!validationHelper.validateMail(responsable.getMail()))
                        throw new OsirisValidationException("Mails");
                }

                if (responsable.getPhones() != null && responsable.getPhones().size() > 0) {
                    for (Phone phone : responsable.getPhones()) {
                        if (!validationHelper.validateFrenchPhone(phone.getPhoneNumber())
                                && !validationHelper.validateInternationalPhone(phone.getPhoneNumber()))
                            throw new OsirisValidationException("Phones");
                    }
                }

                validationHelper.validateReferential(responsable.getCivility(), true, "Civility");
                validationHelper.validateString(responsable.getFirstname(), true, 40, "Firstname");
                validationHelper.validateString(responsable.getLastname(), true, 40, "Lastname");
                if (responsable.getLastname() != null)
                    responsable.setLastname(responsable.getLastname().toUpperCase());
                validationHelper.validateReferential(responsable.getTiersType(), true, "TiersType");
                validationHelper.validateReferential(responsable.getTiersCategory(), false, "TiersCategory");
                validationHelper.validateReferential(responsable.getSalesEmployee(), true, "SalesEmployee");
                validationHelper.validateReferential(responsable.getDefaultCustomerOrderEmployee(), false,
                        "DefaultCustomerOrderEmployee");
                validationHelper.validateReferential(responsable.getFormalisteEmployee(), false, "FormalisteEmployee");
                validationHelper.validateReferential(responsable.getInsertionEmployee(), false, "InsertionEmployee");
                validationHelper.validateReferential(responsable.getLanguage(), true, "Language");
                validationHelper.validateString(responsable.getAddress(), false, 100, "Address");
                validationHelper.validateReferential(responsable.getCountry(), false, "Country");
                validationHelper.validateReferential(responsable.getCity(), false, "City");
                validationHelper.validateString(responsable.getPostalCode(), false, 10, "PostalCode");
                validationHelper.validateString(responsable.getCedexComplement(), false, 20, "CedexComplement");
                validationHelper.validateString(responsable.getFunction(), false, 100, "Function");
                validationHelper.validateString(responsable.getBuilding(), false, 20, "Building");
                validationHelper.validateString(responsable.getFloor(), false, 20, "Floor");
                validationHelper.validateReferential(responsable.getSubscriptionPeriodType(), false,
                        "SubscriptionPeriodType");
                validationHelper.validateIban(responsable.getRffIban(), false, "RffIBAN");
                validationHelper.validateBic(responsable.getRffBic(), false, "RffBic");
                if (responsable.getRffMail() != null && responsable.getRffMail().length() > 0
                        && !validationHelper.validateMail(responsable.getRffMail()))
                    throw new OsirisValidationException("Mails rff");

                if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
                    for (Document document : responsable.getDocuments()) {
                        validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

                        if (document.getMailsAffaire() != null
                                && !validationHelper.validateMailList(document.getMailsAffaire()))
                            throw new OsirisValidationException("MailsAffaire");
                        if (document.getMailsClient() != null && document.getMailsClient() != null
                                && document.getMailsClient().size() > 0)
                            if (!validationHelper.validateMailList(document.getMailsClient()))
                                throw new OsirisValidationException("MailsClient");

                        validationHelper.validateString(document.getAffaireAddress(), false, 200, "AffaireAddress");
                        validationHelper.validateString(document.getClientAddress(), false, 100, "ClientAddress");
                        validationHelper.validateString(document.getAffaireRecipient(), false, 100, "AffaireRecipient");
                        validationHelper.validateString(document.getClientRecipient(), false, 200, "ClientRecipient");
                        validationHelper.validateString(document.getCommandNumber(), false, 40, "CommandNumber");
                        validationHelper.validateReferential(document.getBillingLabelCity(), false, "BillingLabelCity");
                        validationHelper.validateReferential(document.getBillingLabelCountry(), false,
                                "BillingLabelCountry");
                        validationHelper.validateString(document.getBillingAddress(), false, 200, "BillingAddress");
                        validationHelper.validateString(document.getBillingLabel(), false, 200, "BillingLabel");
                        validationHelper.validateString(document.getBillingPostalCode(), false, 10,
                                "BillingPostalCode");
                        validationHelper.validateString(document.getCedexComplement(), false, 20, "CedexComplement");

                    }
                }
            }
        }
        return true;
    }
}
