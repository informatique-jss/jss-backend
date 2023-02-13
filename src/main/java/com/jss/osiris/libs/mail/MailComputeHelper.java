package com.jss.osiris.libs.mail;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Regie;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class MailComputeHelper {

    @Autowired
    DocumentService documentService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    ConstantService constantService;

    public MailComputeResult computeMailForGenericDigitalDocument(IQuotation quotation)
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

    public MailComputeResult computeMailForDepositConfirmation(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForCustomerOrderFinalizationAndInvoice(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeBilling(), true);
    }

    public MailComputeResult computeMailForPublicationReceipt(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), true);
    }

    public MailComputeResult computeMailForReadingProof(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), true);
    }

    public MailComputeResult computeMailForPublicationFlag(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForSendNumericAttachment(IQuotation quotation)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForDocument(quotation, constantService.getDocumentTypeDigital(), false);
    }

    public MailComputeResult computeMailForBillingClosure(ITiers tiers)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForITiers(tiers);
    }

    public MailComputeResult computeMailForSendAnnouncementToConfrere(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {
        return computeMailForConfrereAnnouncementRequest(announcement);
    }

    private MailComputeResult computeMailForDocument(IQuotation quotation, DocumentType documentType, boolean useRegie)
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
        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);

        if (quotationDocument == null)
            throw new OsirisException(null,
                    "Document " + documentType.getLabel() + " not found in IQuoation " + quotation.getId());

        if (customerOrder == null)
            throw new OsirisException(null,
                    "Customer order not found for IQuoation " + quotation.getId());

        if (quotationDocument != null) {
            if (quotationDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToAffaire(true);
                if (quotationDocument.getMailsAffaire() != null && quotationDocument.getMailsAffaire().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsAffaire());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués dans la commande");
                } else if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails() != null
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo()
                            .addAll(quotation.getAssoAffaireOrders().get(0).getAffaire().getMails());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués sur l'affaire");
                } else
                    throw new OsirisClientMessageException("Aucun mail trouvé pour l'affaire");

                if (quotationDocument.getMailsCCResponsableAffaire() != null
                        && quotationDocument.getMailsCCResponsableAffaire().size() > 0) {
                    for (Responsable responsable : quotationDocument.getMailsCCResponsableAffaire())
                        if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcAffaireOrigin("mails des responsables choisis");
                        }
                }
            }

            if (quotationDocument.getIsRecipientClient()
                    || !quotationDocument.getIsRecipientClient() && !quotationDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToClient(true);
                if (quotationDocument.getMailsClient() != null
                        && quotationDocument.getMailsClient().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsClient());
                    mailComputeResult.setMailToClientOrigin("mails indiqués dans la commande");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getMails() != null
                        && ((Responsable) customerOrder).getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getMails());
                    mailComputeResult.setMailToClientOrigin("mails du responsable");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getTiers().getMails() != null
                        && ((Responsable) customerOrder).getTiers().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getTiers().getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
                } else if (useRegie && customerOrder instanceof Confrere
                        && ((Confrere) customerOrder).getRegie() != null
                        && ((Confrere) customerOrder).getRegie().getMails() != null
                        && ((Confrere) customerOrder).getRegie().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Confrere) customerOrder).getRegie().getMails());
                    mailComputeResult.setMailToClientOrigin("mails de la régie du confrère");
                } else if (customerOrder.getMails() != null
                        && customerOrder.getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(customerOrder.getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers/confrère");
                } else
                    throw new OsirisClientMessageException("Aucun mail trouvé pour le client");

                if (quotationDocument.getMailsCCResponsableClient() != null
                        && quotationDocument.getMailsCCResponsableClient().size() > 0) {
                    for (Responsable responsable : quotationDocument.getMailsCCResponsableClient())
                        if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcClientOrigin("mails des responsables choisis");
                        }
                }
            }
        }
        return mailComputeResult;
    }

    private MailComputeResult computeMailForConfrereAnnouncementRequest(Announcement announcement)
            throws OsirisException, OsirisClientMessageException {

        if (announcement == null)
            throw new OsirisException(null, "Announcement not provided");

        if (announcement.getConfrere() == null)
            throw new OsirisException(null, "Confrere not found in announce " + announcement.getId());

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        mailComputeResult.getRecipientsMailTo().addAll(announcement.getConfrere().getMails());

        return mailComputeResult;
    }

    private MailComputeResult computeMailForITiers(ITiers tiers) throws OsirisException, OsirisClientMessageException {

        if (tiers == null)
            throw new OsirisException(null, "ITiers not provided");

        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(true);
        mailComputeResult.setIsSendToAffaire(false);

        Document billingClosureDocument = documentService.getDocumentByDocumentType(tiers.getDocuments(),
                constantService.getDocumentTypeBillingClosure());
        if (billingClosureDocument != null && billingClosureDocument.getMailsClient() != null
                && billingClosureDocument.getMailsClient().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(billingClosureDocument.getMailsClient());
            mailComputeResult.setMailToClientOrigin("mails Autres du paramétrage du relevé de compte");
        } else if (tiers instanceof Responsable
                && ((Responsable) tiers).getMails() != null
                && ((Responsable) tiers).getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(((Responsable) tiers).getMails());
            mailComputeResult.setMailToClientOrigin("mails du responsable");
        } else if (tiers instanceof Responsable
                && ((Responsable) tiers).getTiers().getMails() != null
                && ((Responsable) tiers).getTiers().getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(((Responsable) tiers).getTiers().getMails());
            mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
        } else if (tiers instanceof Confrere
                && ((Confrere) tiers).getRegie() != null
                && ((Confrere) tiers).getRegie().getMails() != null
                && ((Confrere) tiers).getRegie().getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(((Confrere) tiers).getRegie().getMails());
            mailComputeResult.setMailToClientOrigin("mails de la régie du confrère");
        } else if (tiers.getMails() != null
                && tiers.getMails().size() > 0) {
            mailComputeResult.getRecipientsMailTo().addAll(tiers.getMails());
            mailComputeResult.setMailToClientOrigin("mails du tiers/confrère");
        } else
            throw new OsirisClientMessageException("Aucun mail trouvé pour le client");

        return mailComputeResult;
    }

    public InvoiceLabelResult computePaperLabelResult(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        Document paperDocument = documentService.getDocumentByDocumentType(customerOrder.getDocuments(),
                constantService.getDocumentTypePaper());

        InvoiceLabelResult invoiceLabelResult = new InvoiceLabelResult();

        if (paperDocument == null)
            throw new OsirisException(null, "Paper document not found in iQuotation n°" + customerOrder.getId());

        if (paperDocument.getIsRecipientAffaire()) {
            if (paperDocument.getAffaireRecipient() != null || paperDocument.getAffaireAddress() != null) {
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
                        : (affaire.getCivility() != null ? affaire.getCivility().getLabel() : "")
                                + affaire.getFirstname() + " " + affaire.getLastname());
                invoiceLabelResult.setBillingLabelAddress(affaire.getAddress());
                invoiceLabelResult.setBillingLabelCity(affaire.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(affaire.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(affaire.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(affaire.getPostalCode());
                invoiceLabelResult.setLabelOrigin("adresse de l'affaire");
            } else
                throw new OsirisClientMessageException("Aucune adresse postale trouvée pour l'affaire");
        }

        if (paperDocument.getIsRecipientClient()
                || !paperDocument.getIsRecipientClient() && !paperDocument.getIsRecipientAffaire()) {
            ITiers customer = quotationService.getCustomerOrderOfQuotation(customerOrder);
            if (paperDocument.getClientRecipient() != null || paperDocument.getClientAddress() != null) {
                invoiceLabelResult.setBillingLabel(paperDocument.getClientRecipient());
                invoiceLabelResult.setBillingLabelAddress(paperDocument.getClientAddress());
                invoiceLabelResult.setLabelOrigin("l'adresse indiquée dans la commande");
            } else if (customer instanceof Responsable
                    && ((Responsable) customer).getTiers().getAddress() != null
                    && ((Responsable) customer).getTiers().getCity() != null) {
                Tiers tiers = ((Responsable) customer).getTiers();
                Responsable responsable = ((Responsable) customer);
                if (tiers.getMailRecipient() == null || tiers.getMailRecipient().length() == 0)
                    invoiceLabelResult.setBillingLabel((tiers.getDenomination() != null ? tiers.getDenomination()
                            : tiers.getFirstname() + " " + tiers.getLastname()) + "\r\n"
                            + (responsable.getCivility() != null ? responsable.getCivility().getLabel() : "")
                            + responsable.getFirstname() + " " + responsable.getLastname());
                else
                    invoiceLabelResult.setBillingLabel(tiers.getMailRecipient());
                invoiceLabelResult.setBillingLabelAddress(tiers.getAddress());
                invoiceLabelResult.setBillingLabelCity(tiers.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(tiers.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(tiers.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(tiers.getPostalCode());
                invoiceLabelResult.setLabelOrigin("l'adresse du tiers");
            } else if (customer instanceof Confrere && ((Confrere) customer).getRegie() != null
                    && ((Confrere) customer).getRegie().getAddress() != null
                    && ((Confrere) customer).getRegie().getCity() != null) {
                Regie regie = ((Confrere) customer).getRegie();

                invoiceLabelResult.setBillingLabel(regie.getLabel());
                invoiceLabelResult.setBillingLabelAddress(regie.getAddress());
                invoiceLabelResult.setBillingLabelCity(regie.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(regie.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(regie.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(regie.getPostalCode());
                invoiceLabelResult.setLabelOrigin("l'adresse de la régie du confrère");
            } else if (customer instanceof Confrere
                    && ((Confrere) customer).getAddress() != null
                    && ((Confrere) customer).getCity() != null) {
                Confrere confrere = ((Confrere) customer);

                invoiceLabelResult.setBillingLabel(confrere.getLabel());
                invoiceLabelResult.setBillingLabelAddress(confrere.getAddress());
                invoiceLabelResult.setBillingLabelCity(confrere.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(confrere.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(confrere.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(confrere.getPostalCode());
                invoiceLabelResult.setLabelOrigin("l'adresse du confrère");
            } else if (customer instanceof Tiers
                    && ((Tiers) customer).getAddress() != null
                    && ((Tiers) customer).getCity() != null) {
                Tiers tiers = (Tiers) customer;
                if (tiers.getMailRecipient() == null || tiers.getMailRecipient().length() == 0)
                    invoiceLabelResult.setBillingLabel(
                            tiers.getDenomination() != null ? tiers.getDenomination()
                                    : tiers.getFirstname() + " " + tiers.getLastname());
                else
                    invoiceLabelResult.setBillingLabel(tiers.getMailRecipient());
                invoiceLabelResult.setBillingLabelAddress(tiers.getAddress());
                invoiceLabelResult.setBillingLabelCity(tiers.getCity());
                invoiceLabelResult.setBillingLabelComplementCedex(tiers.getCedexComplement());
                invoiceLabelResult.setBillingLabelCountry(tiers.getCountry());
                invoiceLabelResult.setBillingLabelPostalCode(tiers.getPostalCode());
                invoiceLabelResult.setLabelOrigin("l'adresse du tiers");
            } else
                throw new OsirisClientMessageException("Aucune adresse postale trouvée pour le client");
        }
        return invoiceLabelResult;
    }
}
