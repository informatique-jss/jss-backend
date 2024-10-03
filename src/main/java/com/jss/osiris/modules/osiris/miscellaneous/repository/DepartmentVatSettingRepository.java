package com.jss.osiris.modules.osiris.miscellaneous.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.DepartmentVatSetting;

public interface DepartmentVatSettingRepository extends QueryCacheCrudRepository<DepartmentVatSetting, Integer> {

    DepartmentVatSetting findByDepartment(Department department);
}