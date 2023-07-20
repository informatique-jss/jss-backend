package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.DebourSearch;
import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.DebourRepository;

@Service
public class DebourServiceImpl implements DebourService {

    @Autowired
    DebourRepository debourRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Override
    public Debour getDebour(Integer id) {
        Optional<Debour> debour = debourRepository.findById(id);
        if (debour.isPresent())
            return debour.get();
        return null;
    }

    @Override
    public List<Debour> getDeboursForProvision(Provision provision) {
        return debourRepository.findByProvision(provision);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Debour addOrUpdateDebour(
            Debour debour) {
        if (debour.getId() == null)
            debour.setIsAssociated(false);
        debour = debourRepository.save(debour);
        indexEntityService.indexEntity(debour, debour.getId());
        return debour;
    }

    public void deleteDebour(Debour debour) {
        debourRepository.delete(debour);
    }

    @Override
    public List<DebourSearchResult> searchDebours(DebourSearch debourSearch) throws OsirisException {
        return debourRepository.findDebours(
                debourSearch.getCompetentAuthority() == null ? 0 : debourSearch.getCompetentAuthority().getId(),
                debourSearch.getCustomerOrder() == null ? 0 : debourSearch.getCustomerOrder().getEntityId(),
                debourSearch.getMaxAmount(),
                debourSearch.getMinAmount(), debourSearch.getIsNonAssociated(),
                debourSearch.getIsCompetentAuthorityDirectCharge());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexDebours() {
        List<Debour> debours = IterableUtils.toList(debourRepository.findAll());
        if (debours != null)
            for (Debour debour : debours)
                indexEntityService.indexEntity(debour, debour.getId());
    }

    @Override
    public void setDebourAsAssociated(Debour debour) {
        debour.setIsAssociated(true);
        addOrUpdateDebour(debour);
    }

    @Override
    public List<Debour> findNonAssociatedDeboursForDateAndAmount(LocalDate date, Float amount) throws OsirisException {
        return debourRepository.findNonAssociatedDeboursForDateAndAmount(date, amount,
                constantService.getPaymentTypeCB().getId());
    }

    @Override
    public void unassociateDebourFromInvoice(Debour debour) throws OsirisException {
        if (debour.getAccountingRecords() != null && debour.getAccountingRecords().size() > 0) {
            for (AccountingRecord accountingRecord : debour.getAccountingRecords())
                accountingRecordService.generateCounterPart(accountingRecord, debour.getId(),
                        constantService.getAccountingJournalPurchases());
        }
        debour.setInvoiceItem(null);
    }
}