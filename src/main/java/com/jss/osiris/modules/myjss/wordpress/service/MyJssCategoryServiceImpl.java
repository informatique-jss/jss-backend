package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.repository.MyJssCategoryRepository;

@Service
public class MyJssCategoryServiceImpl implements MyJssCategoryService {

    @Autowired
    MediaService mediaService;

    @Autowired
    MyJssCategoryRepository myJssCategoryRepository;

    @Override
    public MyJssCategory getMyJssCategory(Integer id) {
        Optional<MyJssCategory> category = myJssCategoryRepository.findById(id);
        if (category.isPresent())
            return category.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MyJssCategory addOrUpdateMyJssCategory(MyJssCategory category) {
        if (category.getAcf() != null) {
            category.setColor(category.getAcf().getColor());
            if (category.getAcf().getPicture() != null)
                category.setPicture(mediaService.getMedia(category.getAcf().getPicture()));
            category.setCategoryOrder(category.getAcf().getOrdre());
        }
        return myJssCategoryRepository.save(category);
    }

    @Override
    public List<MyJssCategory> getAvailableMyJssCategories() {
        return myJssCategoryRepository.findAllByOrderByName();
    }
}
