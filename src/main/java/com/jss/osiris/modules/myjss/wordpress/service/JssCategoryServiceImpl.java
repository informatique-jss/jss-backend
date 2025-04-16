package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.repository.JssCategoryRepository;

@Service
public class JssCategoryServiceImpl implements JssCategoryService {

    @Autowired
    MediaService mediaService;

    @Autowired
    JssCategoryRepository jssCategoryRepository;

    @Override
    public JssCategory getJssCategory(Integer id) {
        Optional<JssCategory> category = jssCategoryRepository.findById(id);
        if (category.isPresent())
            return category.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JssCategory addOrUpdateJssCategory(JssCategory category) {
        if (category.getAcf() != null) {
            category.setColor(category.getAcf().getColor());
            if (category.getAcf().getPicture() != null)
                category.setPicture(mediaService.getMedia(category.getAcf().getPicture()));
            category.setCategoryOrder(category.getAcf().getOrdre());
        }
        return jssCategoryRepository.save(category);
    }

    @Override
    public List<JssCategory> getAvailableJssCategories() {
        return jssCategoryRepository.findAllByOrderByName();
    }

    @Override
    public JssCategory getJssCategoryBySlug(String slug) {
        return jssCategoryRepository.findBySlug(slug);
    }
}
