package com.jss.jssbackend.modules.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.profile.model.Employee;
import com.jss.jssbackend.modules.profile.model.Team;
import com.jss.jssbackend.modules.profile.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TeamService teamService;

    private Integer SALES_TEAM_ID = 1;
    private Integer FORMALISTES_TEAM_ID = 2;
    private Integer INSERTIONS_TEAM_ID = 3;

    @Override
    public Employee getEmployeeById(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isEmpty())
            return employee.get();
        return null;
    }

    @Override
    public List<Employee> getSalesEmployees() {
        Team team = teamService.getTeamById(SALES_TEAM_ID);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getFormalisteEmployees() {
        Team team = teamService.getTeamById(FORMALISTES_TEAM_ID);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getInsertionEmployees() {
        Team team = teamService.getTeamById(INSERTIONS_TEAM_ID);
        if (team != null)
            return employeeRepository.findAllEmployeesByTeam(team);
        return new ArrayList<Employee>();
    }
}
