package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PostView;

public interface PostViewRepository extends QueryCacheCrudRepository<PostView, Integer> {

    PostView findByPostAndDay(Post post, LocalDate now);

}