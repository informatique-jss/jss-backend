package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;

@Service
public class MyJssCategoryServiceImpl implements MyJssCategoryService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Autowired
    MediaService mediaService;

    @Override
    @Cacheable(value = "wordpress-myjss-categories")
    public List<MyJssCategory> getAvailableMyJssCategories() {
        List<MyJssCategory> categories = wordpressDelegate.getAvailableMyJssCategories();
        if (categories != null && categories.size() > 0) {
            for (MyJssCategory category : categories)
                if (category.getAcf() != null) {
                    category.setColor(category.getAcf().getColor());
                    if (category.getAcf().getPicture() != null)
                        category.setPicture(mediaService.getMedia(category.getAcf().getPicture()));
                }

            categories.sort(new Comparator<MyJssCategory>() {
                @Override
                public int compare(MyJssCategory o1, MyJssCategory o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        return categories;
    }
}
