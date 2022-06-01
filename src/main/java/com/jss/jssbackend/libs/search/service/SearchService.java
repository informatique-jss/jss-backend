package com.jss.jssbackend.libs.search.service;

import java.util.List;

import com.jss.jssbackend.libs.search.model.IndexEntity;

public interface SearchService {
    public List<IndexEntity> searchForEntities(String search);

    public List<IndexEntity> searchForEntities(String search, String entityType);
}
