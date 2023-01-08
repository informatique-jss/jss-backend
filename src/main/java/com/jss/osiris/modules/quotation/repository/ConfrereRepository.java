package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Confrere;

public interface ConfrereRepository extends CrudRepository<Confrere, Integer> {

    @Query(nativeQuery = true, value = "select c.* from Confrere c where (:departmentId=0 or exists (select 1 from asso_confrere_department a where a.id_department =:departmentId and a.id_confrere = c.id ) ) and upper(label) like '%' || upper(CAST(:label as text))  || '%'   ")
    List<Confrere> findConfrereFilteredByDepartmentAndName(@Param("departmentId") Integer departmentId,
            @Param("label") String label);
}