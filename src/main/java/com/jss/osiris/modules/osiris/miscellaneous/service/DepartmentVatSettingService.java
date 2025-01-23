package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.DepartmentVatSetting;

public interface DepartmentVatSettingService {
    public List<DepartmentVatSetting> getDepartmentVatSettings();

    public DepartmentVatSetting getDepartmentVatSetting(Integer id);

    public DepartmentVatSetting addOrUpdateDepartmentVatSetting(DepartmentVatSetting departmentVatSetting);

    public DepartmentVatSetting getDepartmentVatSettingByDepartment(Department department);
}
