package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.AffaireSearch;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.AssoAffaireOrderRepository;

@Service
public class AssoAffaireOrderServiceImpl implements AssoAffaireOrderService {

    @Autowired
    AssoAffaireOrderRepository assoAffaireOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    public List<AssoAffaireOrder> getAssoAffaireOrders() {
        return IterableUtils.toList(assoAffaireOrderRepository.findAll());
    }

    @Override
    public AssoAffaireOrder getAssoAffaireOrder(Integer id) {
        Optional<AssoAffaireOrder> assoAffaireOrder = assoAffaireOrderRepository.findById(id);
        if (assoAffaireOrder.isPresent())
            return assoAffaireOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoAffaireOrder addOrUpdateAssoAffaireOrder(
            AssoAffaireOrder assoAffaireOrder) {
        for (Provision provision : assoAffaireOrder.getProvisions()) {
            provision.setAssoAffaireOrder(assoAffaireOrder);
        }
        AssoAffaireOrder affaireSaved = assoAffaireOrderRepository.save(assoAffaireOrder);
        indexEntityService.indexEntity(affaireSaved, affaireSaved.getId());
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForAsso(AssoAffaireOrder asso, Employee employee) {
        asso.setAssignedTo(employee);
        assoAffaireOrderRepository.save(asso);
    }

    @Override
    public List<AssoAffaireOrder> searchForAsso(AffaireSearch affaireSearch) {
        return assoAffaireOrderRepository.findAsso(affaireSearch.getResponsible(), affaireSearch.getAssignedTo(),
                affaireSearch.getAffaireStatus(), affaireSearch.getLabel());
    }

    @Override
    public void reindexAffaires() {
        List<AssoAffaireOrder> affaires = getAssoAffaireOrders();
        if (affaires != null)
            for (AssoAffaireOrder affaire : affaires)
                indexEntityService.indexEntity(affaire, affaire.getId());
    }
}
