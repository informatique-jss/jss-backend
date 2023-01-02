package com.jss.osiris.libs.search.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${search.results.max}")
    private Integer maxNumberOfResults;
    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Override
    public List<IndexEntity> searchForEntities(String search) {
        List<IndexEntity> entities = indexEntityRepository.searchForEntities(search, maxNumberOfResults);
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForContainsSimilarEntities(search, maxNumberOfResults);
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForDeepSimilarEntities(search, maxNumberOfResults);
        return entities;
    }

    @Override
    public List<IndexEntity> searchForEntities(String search, String entityType) {
        List<IndexEntity> entities = indexEntityRepository.searchForEntities(search, entityType, maxNumberOfResults);
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForSimilarEntities(search, entityType, maxNumberOfResults);
        return entities;
    }

    @Override
    public List<IndexEntity> searchForEntitiesById(Integer id, List<String> entityTypeToSearch) {
        return indexEntityRepository.searchForEntitiesByIdAndEntityType(id, entityTypeToSearch);
    }

    @Override
    public List<IndexEntity> getResponsableByKeyword(String searchedValue) {
        return searchForEntities(searchedValue, Responsable.class.getSimpleName());
    }

    @Override
    public List<IndexEntity> getIndividualTiersByKeyword(String searchedValue) {
        return searchForEntities(searchedValue + "\"isIndividual\"=\"true\"", Tiers.class.getSimpleName());

    }

}
