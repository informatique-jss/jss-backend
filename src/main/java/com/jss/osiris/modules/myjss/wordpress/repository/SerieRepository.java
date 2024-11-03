package com.jss.osiris.modules.myjss.wordpress.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;

public interface SerieRepository extends QueryCacheCrudRepository<Serie, Integer> {

    Serie findBySlug(String slug);

}