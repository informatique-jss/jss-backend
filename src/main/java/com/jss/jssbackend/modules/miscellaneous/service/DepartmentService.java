package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Department;

public interface DepartmentService {
    public List<Department> getDepartments();

    public Department getDepartment(Integer id);
}
