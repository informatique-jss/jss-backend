package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailPost;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailPostRepository extends QueryCacheCrudRepository<AssoMailPost, Integer> {
    AssoMailPost findByMailAndPost(Mail mail, Post post);

    List<AssoMailPost> findByMail(Mail mail);
}
