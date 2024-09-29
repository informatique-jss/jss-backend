package com.jss.osiris.modules.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.wordpress.model.Category;

public interface CategoryService {
        public List<Category> getAvailableCategories();
}
