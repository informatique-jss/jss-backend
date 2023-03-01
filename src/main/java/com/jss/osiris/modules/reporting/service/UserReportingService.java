package com.jss.osiris.modules.reporting.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.reporting.model.UserReporting;

public interface UserReportingService {
    public List<UserReporting> getUserReportings(Employee employee);

    public UserReporting getUserReporting(Integer id);

    public UserReporting addOrUpdateUserReporting(UserReporting userReporting);

    public void copyUserReportingToUser(UserReporting userReporting, Employee employee);
}
