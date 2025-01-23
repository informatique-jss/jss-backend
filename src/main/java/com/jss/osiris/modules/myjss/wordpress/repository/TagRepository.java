package com.jss.osiris.modules.myjss.wordpress.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface TagRepository extends QueryCacheCrudRepository<Tag, Integer> {

    Tag findBySlug(String slug);

}