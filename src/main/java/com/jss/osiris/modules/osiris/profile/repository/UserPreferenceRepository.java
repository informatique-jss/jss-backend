package com.jss.osiris.modules.osiris.profile.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.UserPreference;

public interface UserPreferenceRepository extends QueryCacheCrudRepository<UserPreference, Integer> {

    UserPreference findByEmployeeAndCodeIgnoreCase(Employee employee, String code);

    List<UserPreference> findAllByEmployee(Employee employee);

    void deleteAllByEmployee(Employee employee);

    void deleteByEmployeeAndCodeIgnoreCase(Employee employee, String code);
}