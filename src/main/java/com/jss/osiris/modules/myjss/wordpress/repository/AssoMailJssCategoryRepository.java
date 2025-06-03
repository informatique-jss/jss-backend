package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailJssCategoryRepository extends QueryCacheCrudRepository<AssoMailJssCategory, Integer> {
    AssoMailJssCategory findByMailAndJssCategory(Mail mail, JssCategory jssCategory);

    List<AssoMailJssCategory> findByMail(Mail mail);
}