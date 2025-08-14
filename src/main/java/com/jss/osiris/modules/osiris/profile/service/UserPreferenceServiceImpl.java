package com.jss.osiris.modules.osiris.profile.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.UserPreference;
import com.jss.osiris.modules.osiris.profile.repository.UserPreferenceRepository;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    UserPreferenceRepository userPreferenceRepository;

    @Override
    public UserPreference getUserPreferenceByCode(String code, Employee employee) {
        return userPreferenceRepository.findByEmployeeAndCodeIgnoreCase(employee, code);
    }

    @Override
    public List<UserPreference> getAllCustomUserPreferences(Employee employee) {

        List<UserPreference> allUserPrefs = userPreferenceRepository.findAllByEmployee(employee);

        return allUserPrefs.stream().filter(pref -> !pref.getCode().contains(UserPreference.DEFAULT_USER_PREFERENCE))
                .toList();
    }

    @Override
    public UserPreference setUserPreference(String code, String jsonValue, Employee employee) {

        UserPreference userPreference = getUserPreferenceByCode(code, employee);
        if (userPreference != null) {
            userPreference.setJsonValue(jsonValue);
        } else {
            userPreference = new UserPreference();
            userPreference.setCode(code);
            userPreference.setEmployee(employee);
            userPreference.setJsonValue(jsonValue);
        }

        return userPreferenceRepository.save(userPreference);
    }

    @Override
    public void deleteUserPreferenceByCode(Employee employee, String code) {
        userPreferenceRepository.deleteByEmployeeAndCodeIgnoreCase(employee, code);
    }

    @Override
    public void deleteUserPreferences(Employee employee) {
        userPreferenceRepository.deleteAllByEmployee(employee);
    }
}
