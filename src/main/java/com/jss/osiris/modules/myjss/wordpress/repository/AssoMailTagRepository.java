package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailTagRepository extends QueryCacheCrudRepository<AssoMailTag, Integer> {
    AssoMailTag findByMailAndTag(Mail mail, Tag tag);

    List<AssoMailTag> findByMail(Mail mail);
}
