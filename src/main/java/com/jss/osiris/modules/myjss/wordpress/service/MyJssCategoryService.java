package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;

public interface MyJssCategoryService {
    public MyJssCategory getMyJssCategory(Integer id);

    public List<MyJssCategory> getAvailableMyJssCategories();

    public MyJssCategory addOrUpdateMyJssCategory(MyJssCategory myJssCategory);
}
