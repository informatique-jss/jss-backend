package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.tiers.model.TiersCategory;
import com.jss.osiris.modules.osiris.tiers.repository.TiersCategoryRepository;

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
        if (tiersCategory.isPresent())
            return tiersCategory.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TiersCategory addOrUpdateTiersCategory(
            TiersCategory tiersCategory) {
        return tiersCategoryRepository.save(tiersCategory);
    }
}
