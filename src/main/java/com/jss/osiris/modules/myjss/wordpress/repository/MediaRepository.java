package com.jss.osiris.modules.myjss.wordpress.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Media;

public interface MediaRepository extends QueryCacheCrudRepository<Media, Integer> {

    Media findByUrlFull(String url);

}