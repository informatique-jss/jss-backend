package com.jss.osiris.modules.profile.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;

public interface EmployeeService {
    public Employee getEmployee(Integer id);

    public List<Employee> getSalesEmployees();

    public List<Employee> getFormalisteEmployees();

    public List<Employee> getInsertionEmployees();
}
