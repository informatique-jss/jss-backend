package com.jss.osiris.modules.tiers.service;

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
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.TiersSearch;
import com.jss.osiris.modules.tiers.repository.ResponsableRepository;

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

        if (tiersSearch.getStartDate() == null)
            tiersSearch.setStartDate(LocalDate.now().minusYears(10));

        if (tiersSearch.getEndDate() == null)
            tiersSearch.setEndDate(LocalDate.now().plusYears(10));

        if (tiersSearch.getLabel() == null)
            tiersSearch.setLabel("");

        if (tiersSearch.getWithNonNullTurnover() == null)
            tiersSearch.setWithNonNullTurnover(false);

        return responsableRepository.searchResponsable(tiersId, responsableId, salesEmployeeId,
                tiersSearch.getStartDate(),
                tiersSearch.getEndDate(), tiersSearch.getLabel(), constantService.getConfrereJssSpel().getId(),
                Arrays.asList(constantService.getInvoiceStatusPayed().getId(),
                        constantService.getInvoiceStatusSend().getId()),
                this.constantService.getDocumentTypeBilling().getId(), tiersSearch.getWithNonNullTurnover());
    }
}
