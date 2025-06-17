package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface ReadingFolderRepository extends QueryCacheCrudRepository<ReadingFolder, Integer> {

    List<ReadingFolder> findByMail(Mail mail);
}
