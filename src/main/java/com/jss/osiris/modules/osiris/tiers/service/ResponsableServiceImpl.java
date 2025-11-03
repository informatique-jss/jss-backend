package com.jss.osiris.modules.osiris.tiers.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.TiersSearch;
import com.jss.osiris.modules.osiris.tiers.repository.ResponsableRepository;

@Service
public class ResponsableServiceImpl implements ResponsableService {

    @Autowired
    ResponsableRepository responsableRepository;

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

    @Autowired
    EmployeeService employeeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Responsable addOrUpdateResponsable(Responsable responsable) {
        return responsableRepository.save(responsable);
    }

    @Override
    public List<Responsable> getAllActiveResponsables() {
        return responsableRepository.findByIsActiveTrue();
    }

    @Override
    public List<Responsable> getResponsables(String searchedValue) {
        if (searchedValue == null || searchedValue.trim().length() <= 2)
            return null;

        return responsableRepository.findByLastnameContainingIgnoreCaseOrFirstnameContainingIgnoreCase(searchedValue,
                searchedValue);
    }

    @Override
    public List<Responsable> getResponsablesByTiers(Tiers tiers) {
        return responsableRepository.findByTiers(tiers);
    }

    @Override
    public Responsable getResponsable(Integer id) {
        Optional<Responsable> responsable = responsableRepository.findById(id);
        if (responsable.isPresent())
            return responsable.get();
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
        return responsableRepository.findFirst1ByIsActiveAndMail_MailIgnoreCase(true, mail);
    }

    @Override
    public List<Responsable> getResponsableByMail(Mail mail) {
        return responsableRepository.findByMailAndIsActive(mail, true);
    }

    @Override
    public Document applyParametersDocumentToQuotation(DocumentType documentType, Responsable responsable) {
        List<CustomerOrder> orders = customerOrderService.findCustomerOrderByResponsable(responsable);
        List<Quotation> quotations = quotationService.findQuotationByResponsable(responsable);
        Document document = documentService.findDocumentByDocumentTypeAndResponsable(documentType, responsable);

        if (orders != null) {
            for (CustomerOrder order : orders) {
                if (!order.getDocuments().isEmpty()
                        && !order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)
                        && !order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
                    for (Document documentToSet : order.getDocuments())
                        if (documentToSet.getDocumentType().getId().equals(documentType.getId())) {
                            documentService.cloneOrMergeDocument(document, documentToSet);
                            documentToSet.setTiers(null);
                            documentToSet.setResponsable(null);
                            documentToSet.setCustomerOrder(order);
                            documentService.addOrUpdateDocument(documentToSet);
                        }
            }
        }

        if (quotations != null) {
            for (Quotation quotation : quotations) {
                if (quotation.getDocuments().isEmpty()
                        && !quotation.getQuotationStatus().getCode().equals(QuotationStatus.ABANDONED)
                        && !quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
                    for (Document documentToSet : quotation.getDocuments())
                        if (documentToSet.getDocumentType().getId().equals(documentType.getId())) {
                            documentService.cloneOrMergeDocument(document, documentToSet);
                            documentToSet.setTiers(null);
                            documentToSet.setResponsable(null);
                            documentToSet.setQuotation(quotation);
                            documentService.addOrUpdateDocument(documentToSet);
                        }
            }
        }
        return document;
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

    @Override
    public void updateConsentDateForCurrentUser() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable.getMail() != null) {
            List<Responsable> responsables = getResponsableByMail(responsable.getMail());
            if (responsables != null)
                for (Responsable responsableToUpdate : responsables) {
                    if (responsableToUpdate.getConsentTermsDate() == null) {
                        responsableToUpdate.setConsentTermsDate(LocalDateTime.now());
                        addOrUpdateResponsable(responsableToUpdate);
                    }
                }
        }
    }
}
