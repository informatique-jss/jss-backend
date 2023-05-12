package com.jss.osiris.modules.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.User;
import com.jss.osiris.modules.profile.repository.EmployeeRepository;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.service.ResponsableService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    MailHelper mailHelper;

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

        List<Employee> holidaymakers = employeeRepository.getMyHolidaymaker(requestedEmployee.getId());
        if (holidaymakers == null)
            holidaymakers = new ArrayList<Employee>();
        holidaymakers.add(requestedEmployee);
        return holidaymakers;
    }

    @Override
    public Responsable loginWebsiteUser(User user, boolean isIntrospection) {
        Responsable responsable = responsableService.getResponsableByLoginWeb(user.getUsername());
        if (responsable != null) {
            String passwordExpected = responsable.getPassword();
            String passwordGiven = diggestPassword(user.getPassword(), responsable.getSalt());

            if (passwordExpected != null && passwordExpected.equals(passwordGiven) || isIntrospection)
                return responsable;
        }
        return null;
    }

    @Override
    public boolean renewResponsablePassword(Responsable responsable) throws OsirisException {
        String salt = SSLHelper.randomPassword(20);
        String password = SSLHelper.randomPassword(12);

        responsable.setSalt(salt);
        responsable.setPassword(diggestPassword(password, salt));
        responsableService.addOrUpdateResponsable(responsable);

        mailHelper.sendNewPasswordMail(responsable, password);
        return true;
    }

    private String diggestPassword(String clearPassword, String salt) {
        return DigestUtils.sha256Hex(salt + clearPassword + salt);
    }

}
