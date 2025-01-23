package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Category getCategory(Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent())
            return category.get();
        return null;
    }

    @Override
    public List<Category> getAvailableCategories() {
        return IterableUtils.toList(categoryRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category addOrUpdateCategory(Category author) {
        return categoryRepository.save(author);
    }
}
