package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.reporting.model.UserReporting;

public interface UserReportingRepository extends CrudRepository<UserReporting, Integer> {

    List<UserReporting> findByEmployee(Employee employee);
}