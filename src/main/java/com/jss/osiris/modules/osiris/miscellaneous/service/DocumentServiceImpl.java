package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.repository.DocumentRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    MailService mailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document addOrUpdateDocument(Document document) {
        document.setMailsAffaire(mailService.populateMailIds(document.getMailsAffaire()));
        document.setMailsClient(mailService.populateMailIds(document.getMailsClient()));
        return documentRepository.save(document);
    }

    @Override
    public List<Document> getDocuments() {
        return IterableUtils.toList(documentRepository.findAll());
    }

    @Override
    public Document getDocument(Integer id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent())
            return document.get();
        return null;
    }

    @Override
    public Document getDocumentByDocumentType(List<Document> documents, DocumentType documentType)
            throws OsirisException {
        if (documents != null && documents.size() > 0)
            for (Document document : documents)
                if (document.getDocumentType() != null && document.getDocumentType().getId() != null
                        && document.getDocumentType().getId().equals(documentType.getId()))
                    return document;
        return null;
    }

    @Override
    public Document getBillingDocument(List<Document> documents) throws OsirisException {
        if (documents != null && documents.size() > 0)
            for (Document document : documents)
                if (document.getDocumentType() != null && document.getDocumentType().getCode() != null
                        && document.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId()))
                    return document;
        return null;
    }

    @Override
    public Document getBillingClosureDocument(List<Document> documents) throws OsirisException {
        if (documents != null && documents.size() > 0)
            for (Document document : documents)
                if (document.getDocumentType() != null && document.getDocumentType().getCode() != null
                        && document.getDocumentType().getId()
                                .equals(constantService.getDocumentTypeBillingClosure().getId()))
                    return document;
        return null;
    }

    @Override
    public Document getRefundDocument(List<Document> documents) throws OsirisException {
        if (documents != null && documents.size() > 0)
            for (Document document : documents)
                if (document.getDocumentType() != null && document.getDocumentType().getCode() != null
                        && document.getDocumentType().getId()
                                .equals(constantService.getDocumentTypeRefund().getId()))
                    return document;
        return null;
    }

    @Override
    public Document findDocumentByDocumentTypeAndResponsable(DocumentType documentType, Responsable responsable) {
        return documentRepository.findByDocumentTypeAndResponsable(documentType, responsable);
    }

    /**
     * Clone or merge a document to an existing or a new document
     * Set documentToMergeTo to merge to an existing document
     */
    @Override
    public Document cloneOrMergeDocument(Document document, Document documentToMergeTo) {
        if (documentToMergeTo == null)
            documentToMergeTo = new Document();
        documentToMergeTo.setTiers(document.getTiers());
        documentToMergeTo.setConfrere(document.getConfrere());
        documentToMergeTo.setResponsable(document.getResponsable());
        documentToMergeTo.setQuotation(document.getQuotation());
        documentToMergeTo.setCustomerOrder(document.getCustomerOrder());
        documentToMergeTo.setDocumentType(document.getDocumentType());
        documentToMergeTo.setIsRecipientClient(document.getIsRecipientClient());
        if (documentToMergeTo.getIsRecipientClient() == null)
            documentToMergeTo.setIsRecipientClient(false);
        documentToMergeTo.setIsRecipientAffaire(document.getIsRecipientAffaire());
        if (documentToMergeTo.getIsRecipientAffaire() == null)
            documentToMergeTo.setIsRecipientAffaire(false);
        documentToMergeTo.setAffaireAddress(document.getAffaireAddress());
        documentToMergeTo.setAffaireRecipient(document.getAffaireRecipient());
        documentToMergeTo.setClientAddress(document.getClientAddress());
        documentToMergeTo.setClientRecipient(document.getClientRecipient());
        documentToMergeTo.setNumberMailingAffaire(document.getNumberMailingAffaire());
        documentToMergeTo.setNumberMailingClient(document.getNumberMailingClient());
        documentToMergeTo.setBillingLabelType(document.getBillingLabelType());

        documentToMergeTo.setMailsClient(new ArrayList<Mail>());
        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
            for (Mail mail : document.getMailsClient())
                documentToMergeTo.getMailsClient().add(mail);

        documentToMergeTo.setMailsAffaire(new ArrayList<Mail>());
        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
            for (Mail mail : document.getMailsAffaire())
                documentToMergeTo.getMailsAffaire().add(mail);

        documentToMergeTo.setIsResponsableOnBilling(document.getIsResponsableOnBilling());
        documentToMergeTo.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
        documentToMergeTo.setCommandNumber(document.getCommandNumber());
        documentToMergeTo.setPaymentDeadlineType(document.getPaymentDeadlineType());
        documentToMergeTo.setRefundType(document.getRefundType());
        documentToMergeTo.setRefundIBAN(document.getRefundIBAN());
        documentToMergeTo.setBillingClosureType(document.getBillingClosureType());
        documentToMergeTo.setBillingClosureRecipientType(document.getBillingClosureRecipientType());
        documentToMergeTo.setBillingLabel(document.getBillingLabel());
        documentToMergeTo.setBillingAddress(document.getBillingAddress());
        documentToMergeTo.setBillingPostalCode(document.getBillingPostalCode());
        documentToMergeTo.setExternalReference(document.getExternalReference());
        documentToMergeTo.setCedexComplement(document.getCedexComplement());
        documentToMergeTo.setBillingLabelCity(document.getBillingLabelCity());
        documentToMergeTo.setBillingLabelCountry(document.getBillingLabelCountry());
        documentToMergeTo.setBillingLabelIsIndividual(document.getBillingLabelIsIndividual());
        documentToMergeTo.setAddToAffaireMailList(document.getAddToAffaireMailList());
        documentToMergeTo.setAddToClientMailList(document.getAddToClientMailList());
        return documentToMergeTo;
    }
}