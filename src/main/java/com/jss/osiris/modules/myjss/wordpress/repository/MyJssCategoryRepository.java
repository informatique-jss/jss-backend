package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;

public interface MyJssCategoryRepository extends QueryCacheCrudRepository<MyJssCategory, Integer> {

    List<MyJssCategory> findAllByOrderByName();

}
