package com.jss.jssbackend.libs.search.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.jss.jssbackend.libs.search.model.IndexEntity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IndexEntityRepository extends CrudRepository<IndexEntity, Integer> {

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , ts_rank_cd(ts_text, websearch_to_tsquery(:searchQuery)) AS rank FROM index_entity WHERE websearch_to_tsquery(:searchQuery)  @@ ts_text) t ORDER BY rank DESC LIMIT :numberOfResult")
        List<IndexEntity> searchForEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity ) t ORDER BY rank DESC LIMIT :numberOfResult")
        List<IndexEntity> searchForSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Transactional
        @Modifying
        @Query(value = "UPDATE index_entity u set text =:text where entity_id = :entity_id and entity_type = :entity_type", nativeQuery = true)
        void updateText(@Param("entity_type") String entity_type,
                        @Param("entity_id") Integer entity_id, @Param("text") String text);
}