package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.repository.MyJssCategoryRepository;

@Service
public class MyJssCategoryServiceImpl implements MyJssCategoryService {

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
    public List<MyJssCategory> getAvailableMyJssCategories() {
        return myJssCategoryRepository.findAllByOrderByCategoryOrderAscNameAsc();
    }

    @Override
    public MyJssCategory addOrUpdateMyJssCategory(MyJssCategory myJssCategory) {
        if (myJssCategory.getAcf() != null && myJssCategory.getAcf().getOrdre() != null) {
            myJssCategory.setCategoryOrder(myJssCategory.getAcf().getOrdre());
        }
        return myJssCategoryRepository.save(myJssCategory);
    }

}
