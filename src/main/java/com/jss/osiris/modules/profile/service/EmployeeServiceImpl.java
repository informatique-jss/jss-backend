package com.jss.osiris.modules.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.Team;
import com.jss.osiris.modules.profile.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TeamService teamService;

    private String SALES_TEAM_CODE = "COMMERCIAL";
    private String FORMALISTES_TEAM_CODE = "FORMALISTE";
    private String INSERTIONS_TEAM_CODE = "INSERTIONS";

    @Override
    public Employee getEmployee(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isEmpty())
            return employee.get();
        return null;
    }

    @Override
    public List<Employee> getSalesEmployees() {
        Team team = teamService.getTeamIdByCode(SALES_TEAM_CODE);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getFormalisteEmployees() {
        Team team = teamService.getTeamIdByCode(FORMALISTES_TEAM_CODE);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getInsertionEmployees() {
        Team team = teamService.getTeamIdByCode(INSERTIONS_TEAM_CODE);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getEmployees() {
        return IterableUtils.toList(employeeRepository.findAll());
    }
}
