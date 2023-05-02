package com.jss.osiris.modules.quotation.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;

public interface AnnouncementStatusRepository extends QueryCacheCrudRepository<AnnouncementStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AnnouncementStatus findByCode(String code);
}