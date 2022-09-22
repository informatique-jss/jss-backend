package com.jss.osiris.libs.search.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.search.model.IndexEntity;

public interface IndexEntityRepository extends CrudRepository<IndexEntity, Integer> {

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , ts_rank_cd(ts_text, websearch_to_tsquery(:searchQuery)) AS rank FROM index_entity WHERE websearch_to_tsquery(:searchQuery)  @@ ts_text) t ORDER BY rank DESC LIMIT :numberOfResult")
        List<IndexEntity> searchForEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity ) t ORDER BY rank DESC LIMIT :numberOfResult")
        List<IndexEntity> searchForSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , ts_rank_cd(ts_text, websearch_to_tsquery(:searchQuery)) AS rank FROM index_entity WHERE entity_type=:entityType and websearch_to_tsquery(:searchQuery)  @@ ts_text) t ORDER BY rank DESC LIMIT :numberOfResult")
        List<IndexEntity> searchForEntities(@Param("searchQuery") String searchQuery,
                        @Param("entityType") String entityType,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity where entity_type=:entityType  ) t ORDER BY rank DESC LIMIT :numberOfResult")

        List<IndexEntity> searchForSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("entityType") String entityType,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query("select e from IndexEntity e where e.entityType in (:entityTypeToSearch) and entityId = :id")
        List<IndexEntity> searchForEntitiesByIdAndEntityType(@Param("id") Integer id,
                        @Param("entityTypeToSearch") List<String> entityTypeToSearch);
}