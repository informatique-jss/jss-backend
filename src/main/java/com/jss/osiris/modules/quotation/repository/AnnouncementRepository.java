package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Confrere;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {

    @Query("select a from Announcement a where confrere=:journalJssPaper and journal is null")
    List<Announcement> findAnnouncementWaitingForPublicationProof(@Param("journalJssPaper") Confrere journalJssPaper);
}
