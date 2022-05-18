package com.jss.jssbackend.modules.profile.repository;

import java.util.ArrayList;

import com.jss.jssbackend.modules.profile.model.Employee;
import com.jss.jssbackend.modules.profile.model.Team;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    @Query("select a from Employee a where team =:team")
    ArrayList<Employee> findAllEmployeesByTeam(@Param("team") Team team);
}