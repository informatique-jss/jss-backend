package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Department;

public interface DepartmentService {
    public List<Department> getDepartments();

    public Department getDepartment(Integer id);

    public Department addOrUpdateDepartment(Department department);
}
