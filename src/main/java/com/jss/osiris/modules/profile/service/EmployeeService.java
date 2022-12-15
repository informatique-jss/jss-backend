package com.jss.osiris.modules.profile.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.User;
import com.jss.osiris.modules.tiers.model.Responsable;

public interface EmployeeService {
    public Employee getEmployee(Integer id);

    public List<Employee> getEmployees();

    public void updateUserFromActiveDirectory();

    public Employee getCurrentEmployee();

    public Employee addOrUpdateEmployee(Employee employee);

    public List<Employee> getMyHolidaymaker(Employee requestedEmployee);

    public Responsable loginWebsiteUser(User user);

    public boolean renewResponsablePassword(Responsable responsable) throws OsirisException;
}
