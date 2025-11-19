package com.jss.osiris.modules.osiris.crm.repository;

import java.time.LocalDateTime;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Webinar;

public interface WebinarRepository extends QueryCacheCrudRepository<Webinar, Integer> {

    Webinar findFirstByWebinarDateBeforeOrderByWebinarDateDesc(LocalDateTime now);

    Webinar findFirstByWebinarDateAfterOrderByWebinarDateAsc(LocalDateTime date);
}