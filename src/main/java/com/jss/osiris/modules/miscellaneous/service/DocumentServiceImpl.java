package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.repository.DocumentRepository;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ConstantService constantService;

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
    public Document cloneDocument(Document document) {
        Document newDocument = new Document();
        newDocument.setTiers(document.getTiers());
        newDocument.setConfrere(document.getConfrere());
        newDocument.setResponsable(document.getResponsable());
        newDocument.setQuotation(document.getQuotation());
        newDocument.setCustomerOrder(document.getCustomerOrder());
        newDocument.setDocumentType(document.getDocumentType());
        newDocument.setIsRecipientClient(document.getIsRecipientClient());
        newDocument.setIsRecipientAffaire(document.getIsRecipientAffaire());
        newDocument.setAffaireAddress(document.getAffaireAddress());
        newDocument.setAffaireRecipient(document.getAffaireRecipient());
        newDocument.setClientAddress(document.getClientAddress());
        newDocument.setClientRecipient(document.getClientRecipient());
        newDocument.setIsMailingPaper(document.getIsMailingPaper());
        newDocument.setIsMailingPdf(document.getIsMailingPdf());
        newDocument.setNumberMailingAffaire(document.getNumberMailingAffaire());
        newDocument.setNumberMailingClient(document.getNumberMailingClient());
        newDocument.setBillingLabelType(document.getBillingLabelType());
        newDocument.setMailsClient(document.getMailsClient());
        newDocument.setMailsCCResponsableClient(document.getMailsCCResponsableClient());
        newDocument.setMailsAffaire(document.getMailsAffaire());
        newDocument.setMailsCCResponsableAffaire(document.getMailsCCResponsableAffaire());
        newDocument.setIsResponsableOnBilling(document.getIsResponsableOnBilling());
        newDocument.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
        newDocument.setCommandNumber(document.getCommandNumber());
        newDocument.setPaymentDeadlineType(document.getPaymentDeadlineType());
        newDocument.setRefundType(document.getRefundType());
        newDocument.setRefundIBAN(document.getRefundIBAN());
        newDocument.setIsRefundable(document.getIsRefundable());
        newDocument.setBillingClosureType(document.getBillingClosureType());
        newDocument.setBillingClosureRecipientType(document.getBillingClosureRecipientType());
        newDocument.setBillingLabel(document.getBillingLabel());
        newDocument.setBillingAddress(document.getBillingAddress());
        newDocument.setBillingPostalCode(document.getBillingPostalCode());
        newDocument.setBillingLabelCity(document.getBillingLabelCity());
        newDocument.setBillingLabelCountry(document.getBillingLabelCountry());
        newDocument.setBillingLabelIsIndividual(document.getBillingLabelIsIndividual());
        newDocument.setRegie(document.getRegie());
        return newDocument;
    }
}
