package com.jss.osiris.libs.search.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.search.model.IndexEntity;

public interface IndexEntityRepository extends QueryCacheCrudRepository<IndexEntity, Integer> {

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , ts_rank_cd(ts_text, websearch_to_tsquery(:searchQuery)) AS rank FROM index_entity WHERE websearch_to_tsquery(:searchQuery)  @@ ts_text) t ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity ) t where text ILIKE '%'||  :searchQuery ||'%' ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForContainsSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity ) t ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForDeepSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , ts_rank_cd(ts_text, websearch_to_tsquery(:searchQuery)) AS rank FROM index_entity WHERE entity_type=:entityType and websearch_to_tsquery(:searchQuery)  @@ ts_text) t ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForEntities(@Param("searchQuery") String searchQuery,
                        @Param("entityType") String entityType,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity ) t where  entity_type=:entityType and text ILIKE '%'||  :searchQuery ||'%' ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForContainsSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("entityType") String entityType,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query(nativeQuery = true, value = "select entity_id, entity_type, text, created_date,id_employee_createdy_by,udpated_date,id_employee_updated_by from (SELECT index_entity.* , similarity(text, :searchQuery)  AS rank FROM index_entity where entity_type=:entityType  ) t ORDER BY (case when entity_type in ('Tiers', 'Responsable') then 0.1 else 0 end) + rank desc LIMIT :numberOfResult")
        List<IndexEntity> searchForDeepSimilarEntities(@Param("searchQuery") String searchQuery,
                        @Param("entityType") String entityType,
                        @Param("numberOfResult") Integer numberOfResult);

        @Query("select e from IndexEntity e where e.entityType in (:entityTypeToSearch) and entityId = :id")
        List<IndexEntity> searchForEntitiesByIdAndEntityType(@Param("id") Integer id,
                        @Param("entityTypeToSearch") List<String> entityTypeToSearch);

        @Query("select e from IndexEntity e where  entityId = :id")
        List<IndexEntity> searchForEntitiesById(@Param("id") Integer id);
}