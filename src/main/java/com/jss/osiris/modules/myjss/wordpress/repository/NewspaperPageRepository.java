package com.jss.osiris.modules.myjss.wordpress.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.NewspaperPage;

public interface NewspaperPageRepository extends QueryCacheCrudRepository<NewspaperPage, Integer> {

    @Query("""
            SELECT DISTINCT np.newspaper FROM NewspaperPage np
            WHERE :searchText IS NULL OR :searchText = ''
            OR LOWER(np.content) LIKE LOWER(CONCAT('%', :searchText, '%'))
                 """)
    Page<Newspaper> findByContent(@Param("searchText") String searchText, Pageable pageable);
}
