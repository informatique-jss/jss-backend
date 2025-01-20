package com.jss.osiris.modules.osiris.tiers.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.TiersSearch;
import com.jss.osiris.modules.osiris.tiers.repository.ResponsableRepository;

@Service
public class ResponsableServiceImpl implements ResponsableService {

    @Autowired
    ResponsableRepository responsableRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    BatchService batchService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    DocumentService documentService;

    @Autowired
    QuotationService quotationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Responsable addOrUpdateResponsable(Responsable responsable) {
        return responsableRepository.save(responsable);
    }

    @Override
    public List<Responsable> getResponsables() {
        return IterableUtils.toList(responsableRepository.findAll());
    }

    @Override
    public Responsable getResponsable(Integer id) {
        Optional<Responsable> responsable = responsableRepository.findById(id);
        if (responsable.isPresent())
            return responsable.get();
        if (responsable.isPresent()) {
            return responsable.get();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexResponsable() throws OsirisException {
        List<Responsable> responsables = IterableUtils.toList(responsableRepository.findAll());
        if (responsables != null)
            for (Responsable responsable : responsables)
                batchService.declareNewBatch(Batch.REINDEX_RESPONSABLE, responsable.getId());
    }

    @Override
    public Responsable getResponsableByLoginWeb(String loginWeb) {
        return responsableRepository.findByLoginWeb(loginWeb);
    }

    @Override
    public Responsable getResponsableByMail(String mail) {
        return responsableRepository.findFirst1ByMail_MailIgnoreCase(mail);
    }

    @Override
    public Document applyParametersDocumentToQuotation(DocumentType documentType, Responsable responsable) {
        List<CustomerOrder> orders = customerOrderService.findCustomerOrderByResponsable(responsable);
        List<Quotation> quotations = quotationService.findQuotationByResponsable(responsable);
        Document document = documentService.findDocumentByDocumentTypeAndResponsable(documentType, responsable);

        if (orders != null) {
            for (CustomerOrder order : orders) {
                if (!order.getDocuments().isEmpty())
                    for (Document documentToSet : order.getDocuments())
                        if (documentToSet.getDocumentType().getId().equals(documentType.getId()))
                            mergeDocument(document, documentToSet);
            }
        }

        if (quotations != null) {
            for (Quotation quotation : quotations) {
                if (quotation.getDocuments().isEmpty())
                    for (Document documentToSet : quotation.getDocuments())
                        if (documentToSet.getDocumentType().getId().equals(documentType.getId()))
                            mergeDocument(document, documentToSet);
            }
        }
        return document;
    }

    private void mergeDocument(Document document, Document documentToSet) {
        documentToSet.setIsRecipientClient(document.getIsRecipientClient());
        documentToSet.setIsRecipientAffaire(document.getIsRecipientAffaire());
        documentToSet.setAffaireAddress(document.getAffaireAddress());
        documentToSet.setAffaireRecipient(document.getAffaireRecipient());
        documentToSet.setClientAddress(document.getClientAddress());
        documentToSet.setClientRecipient(document.getClientRecipient());
        documentToSet.setNumberMailingAffaire(document.getNumberMailingAffaire());
        documentToSet.setNumberMailingClient(document.getNumberMailingClient());
        documentToSet.setBillingLabelType(document.getBillingLabelType());
        documentToSet.setIsResponsableOnBilling(document.getIsResponsableOnBilling());
        documentToSet.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
        documentToSet.setCommandNumber(document.getCommandNumber());
        documentToSet.setPaymentDeadlineType(document.getPaymentDeadlineType());
        documentToSet.setRefundType(document.getRefundType());
        documentToSet.setRefundIBAN(document.getRefundIBAN());
        documentToSet.setBillingClosureType(document.getBillingClosureType());
        documentToSet.setBillingClosureRecipientType(document.getBillingClosureRecipientType());
        documentToSet.setBillingLabel(document.getBillingLabel());
        documentToSet.setBillingAddress(document.getBillingAddress());
        documentToSet.setBillingPostalCode(document.getBillingPostalCode());
        documentToSet.setExternalReference(document.getExternalReference());
        documentToSet.setCedexComplement(document.getCedexComplement());
        documentToSet.setBillingLabelCity(document.getBillingLabelCity());
        documentToSet.setBillingLabelCountry(document.getBillingLabelCountry());
        documentToSet.setBillingLabelIsIndividual(document.getBillingLabelIsIndividual());
        documentToSet.setAddToAffaireMailList(document.getAddToAffaireMailList());
        documentToSet.setAddToClientMailList(document.getAddToClientMailList());
        documentService.addOrUpdateDocument(documentToSet);
    }

    @Override
    public List<IResponsableSearchResult> searchResponsables(TiersSearch tiersSearch) throws OsirisException {
        Integer tiersId = 0;
        if (tiersSearch.getTiers() != null)
            tiersId = tiersSearch.getTiers().getEntityId();

        Integer responsableId = 0;
        if (tiersSearch.getResponsable() != null)
            responsableId = tiersSearch.getResponsable().getEntityId();

        Integer salesEmployeeId = 0;
        if (tiersSearch.getSalesEmployee() != null)
            salesEmployeeId = tiersSearch.getSalesEmployee().getId();

        if (tiersSearch.getMail() == null)
            tiersSearch.setMail("");

        if (tiersSearch.getStartDate() == null)
            tiersSearch.setStartDate(LocalDate.now().minusYears(10));

        if (tiersSearch.getEndDate() == null)
            tiersSearch.setEndDate(LocalDate.now().plusYears(10));

        if (tiersSearch.getLabel() == null)
            tiersSearch.setLabel("");

        if (tiersSearch.getWithNonNullTurnover() == null)
            tiersSearch.setWithNonNullTurnover(false);

        return responsableRepository.searchResponsable(tiersId, responsableId, salesEmployeeId, tiersSearch.getMail(),
                tiersSearch.getStartDate().atTime(0, 0),
                tiersSearch.getEndDate().atTime(23, 59, 59), tiersSearch.getLabel(),
                constantService.getConfrereJssSpel().getId(),
                Arrays.asList(constantService.getInvoiceStatusPayed().getId(),
                        constantService.getInvoiceStatusSend().getId()),
                this.constantService.getDocumentTypeBilling().getId(), tiersSearch.getWithNonNullTurnover());
    }
}
