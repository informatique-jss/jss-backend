package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.Confrere;

public interface ConfrereRepository extends QueryCacheCrudRepository<Confrere, Integer> {

    @Query(nativeQuery = true, value = "select c.* from Confrere c where (:departmentId=0 or exists (select 1 from asso_confrere_department a  where   a.id_department =:departmentId and a.id_confrere = c.id ) ) and (upper(label) like '%' || upper(CAST(:label as text))  || '%' or exists (select 1 from asso_confrere_department a join department d on d.id = a.id_department where d.code=upper(CAST(:label as text)) and a.id_confrere = c.id )) order by coalesce(length(board_grade),0) desc, id_journal_type")
    List<Confrere> findConfrereFilteredByDepartmentAndName(@Param("departmentId") Integer departmentId,
            @Param("label") String label);
}