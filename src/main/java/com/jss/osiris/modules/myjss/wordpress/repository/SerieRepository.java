package com.jss.osiris.modules.myjss.wordpress.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;

public interface SerieRepository extends QueryCacheCrudRepository<Serie, Integer> {

    Serie findBySlug(String slug);

    @Query("SELECT s FROM Post p " +
            "JOIN p.postSerie s " +
            "WHERE p.date = (SELECT MAX(p2.date) FROM Post p2 JOIN p2.postSerie s2 WHERE s2.id = s.id) " +
            "ORDER BY " +
            "  CASE WHEN s.isStayOnTop = true THEN 0 ELSE 1 END ASC, " +
            "  p.date DESC")
    Page<Serie> findSeries(Pageable pageableRequest);

}