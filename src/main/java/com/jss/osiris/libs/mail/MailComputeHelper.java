package com.jss.osiris.libs.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.transaction.Transactional;

@Service
public class MailComputeHelper {

    @Autowired
    DocumentService documentService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    MailService mailService;

    public MailComputeResult computeMailForGenericDigitalDocument(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForMissingAttachmentQueryToCustomer(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), true);
    }

    public MailComputeResult computeMailForQuotationMail(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForQuotationCreationConfirmation(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForCustomerOrderCreationConfirmation(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForDepositRequest(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeBilling(), false);
    }

    @Transactional(rollbackOn = Exception.class)
    public MailComputeResult computeMailForCustomerOrderFinalizationAndInvoice(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        if (quotation.getId() != null) {
            IQuotation fetchedQuotation = customerOrderService.getCustomerOrder(quotation.getId());
            if (fetchedQuotation == null)
                fetchedQuotation = quotationService.getQuotation(quotation.getId());
            if (fetchedQuotation != null)
                quotation = fetchedQuotation;
        }
        return computeMailForDocument(quotation, constantService.getDocumentTypeBilling(), false);
    }

    public MailComputeResult computeMailForPublicationReceipt(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForReadingProof(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForPublicationFlag(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForSendNumericAttachment(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForBillingClosure(Tiers tiers, Responsable responsable)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForITiers(tiers, responsable);
    }

    public MailComputeResult computeMailForRff(Rff rff)
            throws OsirisException, OsirisClientMessageException {
        if (rff == null)
            throw new OsirisException(null, "Rff not provided");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        Mail mail = new Mail();
        mail.setMail(rff.getRffMail());
        mailComputeResult.getRecipientsMailTo().addAll(mailService.populateMailIds(Arrays.asList(mail)));

        return mailComputeResult;
    }

    public MailComputeResult computeMailForSendAnnouncementToConfrere(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForConfrereAnnouncementRequest(announcement);
    }

    private MailComputeResult computeMailForDocument(IQuotation quotation, DocumentType documentType,
            boolean isForcedClient)
            throws OsirisException, OsirisClientMessageException {

        if (quotation == null)
            throw new OsirisException(null, "Quotation not provided");

        if (documentType == null)
            throw new OsirisException(null, "Document Type not provided");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        Document quotationDocument = documentService.getDocumentByDocumentType(quotation.getDocuments(), documentType);
        Responsable responsable = quotation.getResponsable();

        if (quotationDocument == null)
            throw new OsirisException(null,
                    "Document " + documentType.getLabel() + " not found in IQuoation " + quotation.getId());

        if (responsable == null)
            throw new OsirisException(null,
                    "Customer order not found for IQuoation " + quotation.getId());

        if (quotationDocument != null) {
            boolean hasAlreadyAddMails = false;
            if (quotationDocument.getIsRecipientAffaire() && !isForcedClient) {
                mailComputeResult.setIsSendToAffaire(true);
                if (quotationDocument.getMailsAffaire() != null && quotationDocument.getMailsAffaire().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsAffaire());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués dans la commande");
                    hasAlreadyAddMails = true;
                }

                if (hasAlreadyAddMails && !quotationDocument.getAddToAffaireMailList()) {
                    // do nothing
                } else if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails() != null
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo()
                            .addAll(quotation.getAssoAffaireOrders().get(0).getAffaire().getMails());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués sur l'affaire");
                } else
                    throw new OsirisClientMessageException("Aucun mail trouvé pour l'affaire");
            }

            if (quotationDocument.getIsRecipientClient()
                    || !quotationDocument.getIsRecipientClient() && !quotationDocument.getIsRecipientAffaire()
                    || isForcedClient) {
                hasAlreadyAddMails = false;
                mailComputeResult.setIsSendToClient(true);
                if (quotationDocument.getMailsClient() != null
                        && quotationDocument.getMailsClient().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsClient());
                    mailComputeResult.setMailToClientOrigin("mails indiqués dans la commande");
                    hasAlreadyAddMails = true;
                }

                if (isForcedClient) {
                    if (quotationDocument.getMailsAffaire() != null && quotationDocument.getMailsAffaire().size() > 0) {
                        mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsAffaire());
                    }
                }
                if (hasAlreadyAddMails && !quotationDocument.getAddToClientMailList()) {
                    // do nothing
                } else if (responsable.getMail() != null) {
                    mailComputeResult.getRecipientsMailTo().add(responsable.getMail());
                    mailComputeResult.setMailToClientOrigin("mail du responsable");
                } else if (responsable.getTiers().getMails() != null
                        && responsable.getTiers().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(responsable.getTiers().getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
                } else if (responsable.getMail() != null) {
                    mailComputeResult.getRecipientsMailTo().add(responsable.getMail());
                    mailComputeResult.setMailToClientOrigin("mails du tiers");
                } else
                    throw new OsirisClientMessageException("Aucun mail trouvé pour le client");

            }
            if (quotationDocument.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId())
                    && (quotationDocument.getIsCbLinkDisabled() == null
                            || (quotationDocument.getIsCbLinkDisabled() != null
                                    && !quotationDocument.getIsCbLinkDisabled())))
                mailComputeResult.setIsCbLinkDisabled(false);
            else if (quotationDocument.getDocumentType().getId()
                    .equals(constantService.getDocumentTypeBilling().getId()))
                mailComputeResult.setIsCbLinkDisabled(true);
        }
        return mailComputeResult;
    }

    private MailComputeResult computeMailForConfrereAnnouncementRequest(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {

        if (announcement == null)
            throw new OsirisException(null, "Announcement not provided");

        if (announcement.getConfrere() == null)
            throw new OsirisException(null, "Confrere not found in announce " + announcement.getId());

        if (announcement.getConfrere().getMails() == null || announcement.getConfrere().getMails().size() == 0)
            throw new OsirisClientMessageException("Aucune adresse mail renseignée sur le confrère "
                    + announcement.getConfrere().getLabel() + " (" + announcement.getConfrere().getId() + ")");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        mailComputeResult.getRecipientsMailTo().addAll(announcement.getConfrere().getMails());

        return mailComputeResult;
    }

    private MailComputeResult computeMailForITiers(Tiers tiers, Responsable responsable)
            throws OsirisException, OsirisClientMessageException {

        if (tiers == null && responsable == null)
            throw new OsirisException(null, "ITiers not provided");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(true);
        mailComputeResult.setIsSendToAffaire(false);

        Document billingClosureDocument = null;
        if (tiers != null) {
            billingClosureDocument = documentService.getDocumentByDocumentType(tiers.getDocuments(),
                    constantService.getDocumentTypeBillingClosure());
        } else if (responsable != null) {
            billingClosureDocument = documentService.getDocumentByDocumentType(responsable.getDocuments(),
                    constantService.getDocumentTypeBillingClosure());
        }
        if (billingClosureDocument != null && billingClosureDocument.getMailsClient() != null
                && billingClosureDocument.getMailsClient().size() > 0
                && billingClosureDocument.getBillingClosureRecipientType().getId()
                        .equals(constantService.getBillingClosureRecipientTypeOther().getId())) {
            mailComputeResult.getRecipientsMailTo().addAll(billingClosureDocument.getMailsClient());
            mailComputeResult.setMailToClientOrigin("mails Autres du paramétrage du relevé de compte");
        } else if (responsable != null
                && responsable.getMail() != null) {
            mailComputeResult.getRecipientsMailTo().add(responsable.getMail());
            mailComputeResult.setMailToClientOrigin("mail du responsable");
        } else if (responsable != null
                && responsable.getTiers().getMails() != null
                && responsable.getTiers().getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(responsable.getTiers().getMails());
            mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
        } else if (tiers != null && tiers.getMails() != null
                && tiers.getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(tiers.getMails());
            mailComputeResult.setMailToClientOrigin("mails du tiers/confrère");
        } else
            throw new OsirisClientMessageException("Aucun mail trouvé pour le client");

        // Add other mails
        if (billingClosureDocument != null && billingClosureDocument.getMailsClient() != null
                && billingClosureDocument.getMailsClient().size() > 0
                && !billingClosureDocument.getBillingClosureRecipientType().getId()
                        .equals(constantService.getBillingClosureRecipientTypeOther().getId())) {
            mailComputeResult.getRecipientsMailTo().addAll(billingClosureDocument.getMailsClient());
            mailComputeResult.setMailToClientOrigin(
                    mailComputeResult.getMailToClientOrigin() + " et mails Autres du paramétrage du relevé de compte");
        }

        return mailComputeResult;
    }

    public InvoiceLabelResult computeCompetentAuthorityLabelResult(CompetentAuthority competentAuthority)
            throws OsirisClientMessageException {
        InvoiceLabelResult invoiceLabelResult = new InvoiceLabelResult();
        if (competentAuthority.getLabel() != null && !competentAuthority.getLabel().equals("")
                && competentAuthority.getAddress() != null &&
                !competentAuthority.getAddress().equals("") && competentAuthority.getCity() != null
                && competentAuthority.getPostalCode() != null && !competentAuthority.getPostalCode().equals("")) {
            invoiceLabelResult.setBillingLabel(competentAuthority.getLabel());
            invoiceLabelResult.setBillingLabelAddress(competentAuthority.getAddress());
            invoiceLabelResult.setBillingLabelCity(competentAuthority.getCity());
            invoiceLabelResult.setBillingLabelPostalCode(competentAuthority.getPostalCode());
            invoiceLabelResult.setLabelOrigin("adresse de l'autorité compétente");
        } else
            throw new OsirisClientMessageException("Aucune adresse postale trouvée pour l'autorité compétente");
        return invoiceLabelResult;
    }

    public InvoiceLabelResult computePaperLabelResult(IQuotation customerOrder)
            throws OsirisException, OsirisClientMessageException {
        Document paperDocument = documentService.getDocumentByDocumentType(customerOrder.getDocuments(),
                constantService.getDocumentTypePaper());

        InvoiceLabelResult invoiceLabelResult = new InvoiceLabelResult();

        if (paperDocument == null)
            throw new OsirisException(null, "Paper document not found in iQuotation n°" + customerOrder.getId());

        if (paperDocument.getIsRecipientAffaire()) {
            if (paperDocument.getAffaireRecipient() != null && !paperDocument.getAffaireRecipient().equals("")
                    || paperDocument.getAffaireAddress() != null && !paperDocument.getAffaireAddress().equals("")) {
                invoiceLabelResult.setBillingLabel(paperDocument.getAffaireRecipient());
                invoiceLabelResult.setBillingLabelAddress(paperDocument.getAffaireAddress());
                invoiceLabelResult.setBillingLabelCity(null);
                invoiceLabelResult.setLabelOrigin("adresse indiquée dans la commande");
            } else if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0
                    && customerOrder.getAssoAffaireOrders().get(0) != null
                    && customerOrder.getAssoAffaireOrders().get(0).getAffaire() != null
                    && customerOrder.getAssoAffaireOrders().get(0).getAffaire().getAddress() != null
                    && customerOrder.getAssoAffaireOrders().get(0).getAffaire().getCity() != null) {
                Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
                invoiceLabelResult.setBillingLabel(affaire.getDenomination() != null ? affaire.getDenomination()
                        : (affaire.getCivility() != null ? affaire.getCivility().getLabel() + " " : "")
                                + affaire.getFirstname() + " " + affaire.getLastname());
                invoiceLabelResult.setBillingLabelAddress(affaire.getAddress());
                invoiceLabelResult.setBillingLabelCity(affaire.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(affaire.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(affaire.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(affaire.getPostalCode());
                invoiceLabelResult.setBillingLabelIntercommunityVat(affaire.getIntercommunityVat());
                invoiceLabelResult.setLabelOrigin("adresse de l'affaire");
            } else
                throw new OsirisClientMessageException("Aucune adresse postale trouvée pour l'affaire");
        }

        if (paperDocument.getIsRecipientClient()
                || !paperDocument.getIsRecipientClient() && !paperDocument.getIsRecipientAffaire()) {
            Responsable responsable = customerOrder.getResponsable();
            if (paperDocument.getClientRecipient() != null && !paperDocument.getClientRecipient().equals("")
                    || paperDocument.getClientAddress() != null && !paperDocument.getClientAddress().equals("")) {
                invoiceLabelResult.setBillingLabel(paperDocument.getClientRecipient());
                invoiceLabelResult.setBillingLabelAddress(paperDocument.getClientAddress());
                invoiceLabelResult.setLabelOrigin("l'adresse indiquée dans la commande");
            } else if (responsable != null) {

                Tiers tiers = responsable.getTiers();
                if (tiers.getMailRecipient() == null || tiers.getMailRecipient().length() == 0)
                    invoiceLabelResult.setBillingLabel((tiers.getDenomination() != null ? tiers.getDenomination()
                            : tiers.getFirstname() + " " + tiers.getLastname()) + "\r\n"
                            + (responsable.getCivility() != null ? responsable.getCivility().getLabel() + " " : "")
                            + responsable.getFirstname() + " " + responsable.getLastname());
                else {
                    invoiceLabelResult.setBillingLabel(tiers.getMailRecipient());
                }
                invoiceLabelResult.setBillingLabelAddress(tiers.getAddress());
                invoiceLabelResult.setBillingLabelCity(tiers.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(tiers.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(tiers.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(tiers.getPostalCode());
                invoiceLabelResult.setBillingLabelIntercommunityVat(tiers.getIntercommunityVat());
                invoiceLabelResult.setLabelOrigin("l'adresse du tiers");
            } else {
                throw new OsirisClientMessageException("Aucune adresse postale trouvée pour le client");
            }
        }
        return invoiceLabelResult;
    }

    public MailComputeResult computeMailForMailList(List<Mail> mails)
            throws OsirisException, OsirisClientMessageException {
        if (mails == null)
            throw new OsirisException(null, "No mail provided");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        mailComputeResult.getRecipientsMailTo().addAll(mails);

        return mailComputeResult;
    }
}
