package com.jss.jssbackend.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.libs.search.model.IndexEntity;
import com.jss.jssbackend.libs.search.service.SearchService;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.repository.ResponsableRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponsableServiceImpl implements ResponsableService {

    @Autowired
    ResponsableRepository responsableRepository;

    @Autowired
    SearchService searchService;

    @Override
    public List<Responsable> getResponsables() {
        return IterableUtils.toList(responsableRepository.findAll());
    }

    @Override
    public Responsable getResponsable(Integer id) {
        Optional<Responsable> responsable = responsableRepository.findById(id);
        if (!responsable.isEmpty())
            return responsable.get();
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
