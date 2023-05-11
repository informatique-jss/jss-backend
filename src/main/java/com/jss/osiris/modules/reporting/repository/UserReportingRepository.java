package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.reporting.model.UserReporting;

public interface UserReportingRepository extends QueryCacheCrudRepository<UserReporting, Integer> {

    List<UserReporting> findByEmployee(Employee employee);
}