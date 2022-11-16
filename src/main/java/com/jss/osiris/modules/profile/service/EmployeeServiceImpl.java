package com.jss.osiris.modules.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Override
    public Employee getEmployee(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent())
            return employee.get();
        return null;
    }

    @Override
    public List<Employee> getEmployees() {
        return IterableUtils.toList(employeeRepository.findAll());
    }

    @Override
    public Employee addOrUpdateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void updateUserFromActiveDirectory() {
        List<Employee> adEmployees = activeDirectoryHelper.getActiveDirectoryEmployees();
        List<Employee> existingEmployees = getEmployees();
        if (adEmployees != null) {
            for (Employee employee : adEmployees)
                if (employee != null && !employee.getAdPath().contains("OU=Divers")
                        && !employee.getAdPath().contains("OU=Systeme")) {
                    Employee existingEmployee = employeeRepository.findByUsername(employee.getUsername());
                    if (existingEmployee != null) {
                        existingEmployee.setAdPath(employee.getAdPath());
                        existingEmployee.setFirstname(employee.getFirstname());
                        existingEmployee.setLastname(employee.getLastname());
                        existingEmployee.setTitle(employee.getTitle());
                        existingEmployee.setMail(employee.getMail());
                        addOrUpdateEmployee(existingEmployee);
                    } else {
                        addOrUpdateEmployee(employee);
                    }
                }

            if (existingEmployees != null) {
                for (Employee existingEmployee : existingEmployees) {
                    boolean found = false;
                    for (Employee adEmployee : adEmployees) {
                        if (adEmployee != null && !adEmployee.getAdPath().contains("OU=Divers")
                                && !adEmployee.getAdPath().contains("OU=Systeme")
                                && adEmployee.getUsername().equals(existingEmployee.getUsername())) {
                            found = true;
                            break;
                        }
                    }
                    Employee existingEmployeeDb = employeeRepository.findByUsername(existingEmployee.getUsername());
                    if (!found) {
                        existingEmployeeDb.setIsActive(false);
                    } else {
                        existingEmployeeDb.setIsActive(true);
                    }
                    addOrUpdateEmployee(existingEmployeeDb);
                }
            }
        }
    }

    @Override
    public Employee getCurrentEmployee() {
        String username = activeDirectoryHelper.getCurrentUsername();
        if (username != null)
            return employeeRepository.findByUsername(username);
        return null;
    }

    /**
     * Take a requested employee in param
     * - if request employee is null, return null
     * - if requested employee is myself, return myself and responsible
     * holidaymarkers assigned to me
     * - if requested employee is not myself, return request employee
     * 
     * @return list of Employee to take into account
     */
    @Override
    public List<Employee> getMyHolidaymaker(Employee requestedEmployee) {
        if (requestedEmployee == null)
            return null;

        Employee currentUser = getCurrentEmployee();
        List<Employee> holidaymakers = employeeRepository.getMyHolidaymaker(currentUser.getId());
        if (holidaymakers == null)
            holidaymakers = new ArrayList<Employee>();
        holidaymakers.add(currentUser);
        return holidaymakers;
    }
}
