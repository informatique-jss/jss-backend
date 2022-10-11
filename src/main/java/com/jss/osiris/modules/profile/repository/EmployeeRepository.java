package com.jss.osiris.modules.profile.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.profile.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    Employee findByUsername(String username);
}