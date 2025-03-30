package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;

public interface JssCategoryService {
        public JssCategory getJssCategory(Integer id);

        public List<JssCategory> getAvailableJssCategories();

        public JssCategory addOrUpdateJssCategory(JssCategory jssCategory);
}
