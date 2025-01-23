package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Category;

public interface CategoryService {
        public Category getCategory(Integer id);

        public List<Category> getAvailableCategories();

        public Category addOrUpdateCategory(Category author);
}
