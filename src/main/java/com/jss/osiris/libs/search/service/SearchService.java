package com.jss.osiris.libs.search.service;

import java.util.List;

import com.jss.osiris.libs.search.model.IndexEntity;

public interface SearchService {
    public List<IndexEntity> searchForEntities(String search);

    public List<IndexEntity> searchForEntities(String search, String entityType);

    public List<IndexEntity> searchForEntitiesById(Integer id, List<String> entityTypeToSearch);

    public List<IndexEntity> getResponsableByKeyword(String searchedValue);

    public List<IndexEntity> getIndividualTiersByKeyword(String searchedValue);
}
