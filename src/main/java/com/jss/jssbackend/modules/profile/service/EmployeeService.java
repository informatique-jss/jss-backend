package com.jss.jssbackend.modules.profile.service;

import java.util.List;

import com.jss.jssbackend.modules.profile.model.Employee;

public interface EmployeeService {
    public Employee getEmployeeById(Integer id);

    public List<Employee> getSalesEmployees();

    public List<Employee> getFormalisteEmployees();

    public List<Employee> getInsertionEmployees();
}
