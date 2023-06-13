package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.tiers.model.Responsable;
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
    IndexEntityService indexEntityService;

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
            Responsable responsableInstance = responsable.get();
            responsableInstance.setFirstBilling(invoiceService.getFirstBillingDateForResponsable(responsableInstance));
            return responsableInstance;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexResponsable() {
        List<Responsable> responsables = IterableUtils.toList(responsableRepository.findAll());
        if (responsables != null)
            for (Responsable responsable : responsables)
                indexEntityService.indexEntity(responsable, responsable.getId());
    }

    @Override
    public Responsable getResponsableByLoginWeb(String loginWeb) {
        return responsableRepository.findByLoginWeb(loginWeb);
    }
}
