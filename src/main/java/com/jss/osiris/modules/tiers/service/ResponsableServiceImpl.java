package com.jss.osiris.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.model.IndexEntity;
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
    public List<Responsable> getResponsableByKeyword(String searchedValue) {
        List<Responsable> foundResponsables = new ArrayList<Responsable>();
        List<IndexEntity> responsables = searchService.searchForEntities(searchedValue,
                Responsable.class.getSimpleName());
        if (responsables != null && responsables.size() > 0) {
            for (IndexEntity t : responsables) {
                foundResponsables.add(this.getResponsable(t.getEntityId()));
            }
        }
        return foundResponsables;
    }
}
