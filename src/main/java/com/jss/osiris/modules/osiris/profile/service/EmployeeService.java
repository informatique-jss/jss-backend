package com.jss.osiris.modules.osiris.profile.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.User;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface EmployeeService {
    public Employee getEmployee(Integer id);

    public Employee getEmployeeByUsername(String username);

    public List<Employee> getEmployees();

    public void updateUserFromActiveDirectory();

    public Employee getCurrentEmployee();

    public Employee addOrUpdateEmployee(Employee employee);

    public List<Employee> getMyHolidaymaker(Employee requestedEmployee);

    public Responsable loginWebsiteUser(User user, boolean isIntrospection);

    public boolean renewResponsablePassword(Responsable responsable) throws OsirisException;

    public boolean modifyResponsablePassword(Responsable responsable, String newPassword) throws OsirisException;
}
