package com.jss.jssbackend.libs.search.service;

import java.util.List;

import com.jss.jssbackend.libs.search.model.IndexEntity;
import com.jss.jssbackend.libs.search.repository.IndexEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            entities = indexEntityRepository.searchForSimilarEntities(search, maxNumberOfResults);
        return entities;
    }

}
