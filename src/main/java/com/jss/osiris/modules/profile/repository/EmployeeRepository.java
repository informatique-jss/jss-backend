package com.jss.osiris.modules.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.profile.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    Employee findByUsername(String username);

    @Query(nativeQuery = true, value = "select e.* from asso_employee_backup a join employee e on a.id_employee = e.id where id_employee_backup = :employeeId")
    List<Employee> getMyHolidaymaker(@Param("employeeId") Integer employeeId);
}