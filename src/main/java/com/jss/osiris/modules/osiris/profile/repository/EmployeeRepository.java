package com.jss.osiris.modules.osiris.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;

import jakarta.persistence.QueryHint;

public interface EmployeeRepository extends QueryCacheCrudRepository<Employee, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Employee findByUsernameIgnoreCase(String username);

    @Query(nativeQuery = true, value = "select e.* from asso_employee_backup a join employee e on a.id_employee = e.id where id_employee_backup = :employeeId")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Employee> getMyHolidaymaker(@Param("employeeId") Integer employeeId);

    List<Employee> findByAdPathContainingAndIsActive(String result, Boolean isActive);

    @Query(nativeQuery = true, value = "" +
            " select r.id " +
            " from responsable r " +
            " where r.id_mail = :idMail " +
            " union  " +
            " select r3.id " +
            " from responsable r2  " +
            " join tiers t on t.id = r2.id_tiers and r2.can_view_all_tiers_in_web " +
            " join responsable r3 on r3.id_tiers = t.id " +
            " where r2.id_mail = :idMail ")
    List<Integer> getPotentialUserScope(@Param("idMail") Integer idMail);
}