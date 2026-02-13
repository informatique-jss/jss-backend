package com.jss.osiris.modules.osiris.profile.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.User;
import com.jss.osiris.modules.osiris.profile.repository.EmployeeRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Value("${dev.mode}")
    private Boolean devMode;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    CustomerOrderOriginService customerOrderOriginService;

    @Autowired
    DailyConnexionService dailyConnexionService;

    private final SecureRandom random = new SecureRandom();

    private final long TOKEN_EXPIRATION_LENGTH_MINUTES = 15;

    @Override
    public Employee getEmployee(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent())
            return employee.get();
        return null;
    }

    @Override
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public Employee getEmployeeByName(String name) {
        String[] employeeNames = name.split(" ");
        return employeeRepository.findByFirstnameLikeIgnoreCaseAndLastnameLikeIgnoreCase(employeeNames[0],
                employeeNames[employeeNames.length - 1]);
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
                    Employee existingEmployee = employeeRepository.findByUsernameIgnoreCase(employee.getUsername());
                    if (existingEmployee != null) {
                        existingEmployee.setAdPath(employee.getAdPath());
                        existingEmployee.setFirstname(employee.getFirstname());
                        existingEmployee.setLastname(employee.getLastname());
                        existingEmployee.setTitle(employee.getTitle());
                        existingEmployee.setMail(employee.getMail());
                        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
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
                    Employee existingEmployeeDb = employeeRepository
                            .findByUsernameIgnoreCase(existingEmployee.getUsername());
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
            return employeeRepository.findByUsernameIgnoreCase(username);

        return null;
    }

    @Override
    public Responsable getCurrentMyJssUser() {
        String username = activeDirectoryHelper.getCurrentUsername();
        if (username != null && !username.equals("ANONYMOUSUSER") && !username.equals("OSIRIS")) {
            Responsable responsable = responsableService.getResponsable(Integer.parseInt(username));
            // TODO : refactor to avoid constaint exception that failed the login
            // dailyConnexionService.declareConnexionForToday(responsable);
            return responsable;
        }
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

    @Override // TODO delete
    public Responsable loginWebsiteUser(User user, boolean isIntrospection) {
        Responsable responsable = responsableService.getResponsableByLoginWeb(user.getUsername());
        if (responsable != null) {
            String passwordExpected = responsable.getPassword();
            String passwordGiven = diggestPassword(user.getPassword(), responsable.getSalt());

            // Check if user authorized
            List<CustomerOrderOrigin> origins = customerOrderOriginService
                    .getByUsername(activeDirectoryHelper.getCurrentUsername());
            if (origins != null && origins.size() == 1)
                if (passwordExpected != null && passwordExpected.equals(passwordGiven) || isIntrospection)
                    return responsable;
        }
        return null;
    }

    @Override // TODO delete
    @Transactional(rollbackFor = Exception.class)
    public boolean renewResponsablePassword(Responsable responsable) throws OsirisException {
        String salt = SSLHelper.randomPassword(20);
        String password = SSLHelper.randomPassword(12);

        responsable.setSalt(salt);
        responsable.setPassword(diggestPassword(password, salt));
        responsableService.addOrUpdateResponsable(responsable);

        mailHelper.sendNewPasswordMail(responsable, password);
        return true;
    }

    @Override // TODO delete
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyResponsablePassword(Responsable responsable, String newPassword) throws OsirisException {
        String salt = SSLHelper.randomPassword(20);

        responsable.setSalt(salt);
        responsable.setPassword(diggestPassword(newPassword, salt));

        // Check if user authorized
        List<CustomerOrderOrigin> origins = customerOrderOriginService
                .getByUsername(activeDirectoryHelper.getCurrentUsername());
        if (origins != null && origins.size() == 1)
            responsableService.addOrUpdateResponsable(responsable);

        return true;
    }

    private String diggestPassword(String clearPassword, String salt) { // TODO : delete
        return DigestUtils.sha256Hex(salt + clearPassword + salt);
    }

    @Override
    @Transactional
    public void sendTokenToResponsable(Responsable responsable, String overrideMail, Boolean isFromQuotation)
            throws OsirisException {
        responsable = responsableService.getResponsable(responsable.getId());

        byte bytes[] = new byte[512];
        random.nextBytes(bytes);
        String token = String.valueOf(Hex.encode(bytes));

        responsable.setIsComingFromQuotation(isFromQuotation);
        responsable.setLoginToken(token);
        responsable.setLoginTokenExpirationDateTime(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_LENGTH_MINUTES));
        responsableService.addOrUpdateResponsable(responsable);
        mailHelper.sendNewTokenMail(responsable, overrideMail);
    }

    @Override
    public List<Employee> findEmployeesInTheSameOU(Employee employee) {
        List<Employee> employees = new ArrayList<Employee>();
        if (employee != null) {
            int firstCommaIndex = employee.getAdPath().indexOf(',');
            String result = (firstCommaIndex != -1) ? employee.getAdPath().substring(firstCommaIndex + 1)
                    : employee.getAdPath();
            employees = employeeRepository.findByAdPathContainingAndIsActive(result, true);
        }
        return employees;
    }

    @Override
    public List<Integer> getPotentialUserScope(Integer idMail) {
        return employeeRepository.getPotentialUserScope(idMail);
    }

    @Override
    public boolean isCurrentUserHasAdGroup(ActiveDirectoryGroup adGroup) {
        if (devMode) {
            return true;
        }
        if (getCurrentEmployee() != null)
            if (getCurrentEmployee().getAdPath() != null
                    && getCurrentEmployee().getAdPath().endsWith(adGroup.getActiveDirectoryPath())) {
                return true;
            }
        return false;
    }

}
