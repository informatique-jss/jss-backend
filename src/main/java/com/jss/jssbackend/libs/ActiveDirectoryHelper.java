package com.jss.jssbackend.libs;

import com.jss.jssbackend.modules.profile.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActiveDirectoryHelper {
    @Autowired
    EmployeeService employeeService;

    public String getCurrentUsername() {
        // TODO : faire le branchement à l'AD
        return "toto";
    }

}
