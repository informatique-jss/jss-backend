package com.jss.osiris.modules.wordpress.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.wordpress.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Override
    @Cacheable(value = "wordpress-categories")
    public List<Category> getAvailableCategories() {
        return wordpressDelegate.getAvailableCategories();
    }
}
