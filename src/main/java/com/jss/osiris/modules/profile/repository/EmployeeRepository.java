package com.jss.osiris.modules.profile.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.Team;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    @Query("select a from Employee a where team =:team")
    ArrayList<Employee> findAllEmployeesByTeam(@Param("team") Team team);
}