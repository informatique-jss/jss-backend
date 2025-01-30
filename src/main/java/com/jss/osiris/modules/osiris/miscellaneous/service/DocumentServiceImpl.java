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

    @Override
    public Document cloneDocument(Document document, Document newDocument) {
        if (newDocument == null)
            newDocument = new Document();
        newDocument.setTiers(document.getTiers());
        newDocument.setConfrere(document.getConfrere());
        newDocument.setResponsable(document.getResponsable());
        newDocument.setQuotation(document.getQuotation());
        newDocument.setCustomerOrder(document.getCustomerOrder());
        newDocument.setDocumentType(document.getDocumentType());
        newDocument.setIsRecipientClient(document.getIsRecipientClient());
        if (newDocument.getIsRecipientClient() == null)
            newDocument.setIsRecipientClient(false);
        newDocument.setIsRecipientAffaire(document.getIsRecipientAffaire());
        if (newDocument.getIsRecipientAffaire() == null)
            newDocument.setIsRecipientAffaire(false);
        newDocument.setAffaireAddress(document.getAffaireAddress());
        newDocument.setAffaireRecipient(document.getAffaireRecipient());
        newDocument.setClientAddress(document.getClientAddress());
        newDocument.setClientRecipient(document.getClientRecipient());
        newDocument.setNumberMailingAffaire(document.getNumberMailingAffaire());
        newDocument.setNumberMailingClient(document.getNumberMailingClient());
        newDocument.setBillingLabelType(document.getBillingLabelType());

        newDocument.setMailsClient(new ArrayList<Mail>());
        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
            for (Mail mail : document.getMailsClient())
                newDocument.getMailsClient().add(mail);

        newDocument.setMailsAffaire(new ArrayList<Mail>());
        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
            for (Mail mail : document.getMailsAffaire())
                newDocument.getMailsAffaire().add(mail);

        newDocument.setIsResponsableOnBilling(document.getIsResponsableOnBilling());
        newDocument.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
        newDocument.setCommandNumber(document.getCommandNumber());
        newDocument.setPaymentDeadlineType(document.getPaymentDeadlineType());
        newDocument.setRefundType(document.getRefundType());
        newDocument.setRefundIBAN(document.getRefundIBAN());
        newDocument.setBillingClosureType(document.getBillingClosureType());
        newDocument.setBillingClosureRecipientType(document.getBillingClosureRecipientType());
        newDocument.setBillingLabel(document.getBillingLabel());
        newDocument.setBillingAddress(document.getBillingAddress());
        newDocument.setBillingPostalCode(document.getBillingPostalCode());
        newDocument.setExternalReference(document.getExternalReference());
        newDocument.setCedexComplement(document.getCedexComplement());
        newDocument.setBillingLabelCity(document.getBillingLabelCity());
        newDocument.setBillingLabelCountry(document.getBillingLabelCountry());
        newDocument.setBillingLabelIsIndividual(document.getBillingLabelIsIndividual());
        newDocument.setAddToAffaireMailList(document.getAddToAffaireMailList());
        newDocument.setAddToClientMailList(document.getAddToClientMailList());
        return newDocument;
    }
}