package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Formalite;

public interface FormaliteRepository extends CrudRepository<Formalite, Integer> {

    @Query("select f from Formalite f join f.provision p join f.formaliteStatus s where p.assignedTo=:assignedTo and f.formaliteGuichetUnique is not null and s.isCloseState = false")
    List<Formalite> getFormaliteForGURefresh(@Param("assignedTo") Employee assignedTo);
}