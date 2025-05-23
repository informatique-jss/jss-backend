package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;

public interface JssCategoryRepository extends QueryCacheCrudRepository<JssCategory, Integer> {

    List<JssCategory> findAllByOrderByName();

    JssCategory findBySlug(String slug);
}