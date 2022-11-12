package com.jss.osiris.modules.profile.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;

public interface EmployeeService {
    public Employee getEmployee(Integer id);

    public List<Employee> getEmployees();

    public void updateUserFromActiveDirectory();

    public Employee getCurrentEmployee();

    public Employee addOrUpdateEmployee(Employee employee);
}
