package com.jss.osiris.libs.exception;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;

public interface OsirisLogRepository extends QueryCacheCrudRepository<OsirisLog, Integer> {

    @Query(value = "select * from OsirisLog n where n.created_date_time<(now() - make_interval(months => :monthNbr))  ", nativeQuery = true)
    List<OsirisLog> findLogsOlderThanMonths(@Param("monthNbr") Integer monthNbr);

    @Query("select n from OsirisLog n where :hideRead = false or isRead = false ")
    List<OsirisLog> findLogs(@Param("hideRead") boolean hideRead);
}