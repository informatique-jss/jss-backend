package com.jss.osiris.libs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class ActiveDirectoryHelper {
    @Autowired
    EmployeeService employeeService;

    public String getCurrentUsername() {
        // TODO : faire le branchement à l'AD
        return "toto";
    }

}
