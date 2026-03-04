package com.jss.osiris.libs.mail.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.mail.model.MailTracker;

public interface MailTrackerRepository extends QueryCacheCrudRepository<MailTracker, Long> {
    MailTracker findByUidAndExitDateIsNull(Long uid);

    List<MailTracker> findByExitDateIsNull();
}