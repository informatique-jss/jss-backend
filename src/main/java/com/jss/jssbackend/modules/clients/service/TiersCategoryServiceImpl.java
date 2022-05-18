package com.jss.jssbackend.modules.clients.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.clients.model.TiersCategory;
import com.jss.jssbackend.modules.clients.repository.TiersCategoryRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersCategoryServiceImpl implements TiersCategoryService {

    @Autowired
    TiersCategoryRepository tiersCategoryRepository;

    @Override
    public List<TiersCategory> getTiersCategories() {
        return IterableUtils.toList(tiersCategoryRepository.findAll());
    }

    @Override
    public TiersCategory getTiersCategory(Integer id) {
        Optional<TiersCategory> tiersCategory = tiersCategoryRepository.findById(id);
        if (!tiersCategory.isEmpty())
            return tiersCategory.get();
        return null;
    }
}
