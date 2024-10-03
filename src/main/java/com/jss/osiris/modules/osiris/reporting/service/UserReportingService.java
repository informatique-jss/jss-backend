package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.UserReporting;

public interface UserReportingService {
    public List<UserReporting> getUserReportings(Employee employee);

    public UserReporting getUserReporting(Integer id);

    public UserReporting addOrUpdateUserReporting(UserReporting userReporting);

    public void copyUserReportingToUser(UserReporting userReporting, Employee employee);

    public void deleteReporting(UserReporting userReporting);
}
