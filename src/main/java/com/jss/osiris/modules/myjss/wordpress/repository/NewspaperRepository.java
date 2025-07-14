package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;

public interface NewspaperRepository extends QueryCacheCrudRepository<Newspaper, Integer> {

    @Query("SELECT n FROM Newspaper n WHERE n.date >= :start AND n.date < :end")
    List<Newspaper> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
