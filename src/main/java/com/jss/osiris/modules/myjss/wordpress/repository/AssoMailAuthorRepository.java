package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailAuthor;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailAuthorRepository extends QueryCacheCrudRepository<AssoMailAuthor, Integer> {
    AssoMailAuthor findByMailAndAuthor(Mail mail, Author author);

    List<AssoMailAuthor> findByMail(Mail mail);
}
