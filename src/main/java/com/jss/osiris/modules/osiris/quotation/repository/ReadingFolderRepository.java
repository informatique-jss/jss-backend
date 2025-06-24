package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface ReadingFolderRepository extends QueryCacheCrudRepository<ReadingFolder, Integer> {

    List<ReadingFolder> findByMail(Mail mail);

    @Query("SELECT rf.posts FROM ReadingFolder rf WHERE rf.mail = :mail")
    Page<Post> findBookmarkPostsByMail(@Param("mail") Mail mail, Pageable pageable);

}
