package com.jss.osiris.modules.osiris.profile.service;

import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.UserPreference;

public interface UserPreferenceService {

    public UserPreference getUserPreferenceByCode(String code, Employee employee);

    public List<UserPreference> getAllCustomUserPreferences(Employee employee);

    public UserPreference setUserPreference(String code, String jsonValue, Employee employee);

    public void deleteUserPreferenceByCode(Employee employee, String code);

    public void deleteUserPreferences(Employee employee);
}
