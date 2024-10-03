package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.UserReporting;
import com.jss.osiris.modules.osiris.reporting.repository.UserReportingRepository;

@Service
public class UserReportingServiceImpl implements UserReportingService {

    @Autowired
    UserReportingRepository userReportingRepository;

    @Override
    public List<UserReporting> getUserReportings(Employee employee) {
        return userReportingRepository.findByEmployee(employee);
    }

    @Override
    public UserReporting getUserReporting(Integer id) {
        Optional<UserReporting> userReporting = userReportingRepository.findById(id);
        if (userReporting.isPresent())
            return userReporting.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserReporting addOrUpdateUserReporting(
            UserReporting userReporting) {
        return userReportingRepository.save(userReporting);
    }

    @Override
    public void copyUserReportingToUser(UserReporting userReporting, Employee employee) {
        UserReporting userReportingOut = new UserReporting();
        userReportingOut.setDataset(userReporting.getDataset());
        userReportingOut.setEmployee(employee);
        userReportingOut.setName(userReporting.getName());
        userReportingOut.setSettings(userReporting.getSettings());
        addOrUpdateUserReporting(userReportingOut);
    }

    @Override
    public void deleteReporting(UserReporting userReporting) {
        userReportingRepository.delete(userReporting);
    }
}
