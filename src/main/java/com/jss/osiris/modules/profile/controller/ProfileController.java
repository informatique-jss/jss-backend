package com.jss.osiris.modules.profile.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;

@RestController
public class ProfileController {

	private static final String inputEntryPoint = "/profile";

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	EmployeeService employeeService;

	@GetMapping(inputEntryPoint + "/employee")
	public ResponseEntity<Employee> getEmployeeById(@RequestParam Integer id) {
		Employee employee = null;
		try {
			employee = employeeService.getEmployee(id);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Employee>(employee, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/sales")
	public ResponseEntity<List<Employee>> getSalesEmployees() {
		List<Employee> salesEmployees = null;
		try {
			salesEmployees = employeeService.getSalesEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(salesEmployees, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/formalistes")
	public ResponseEntity<List<Employee>> getFormalisteEmployees() {
		List<Employee> formalisteEmployees = null;
		try {
			formalisteEmployees = employeeService.getFormalisteEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(formalisteEmployees, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/insertions")
	public ResponseEntity<List<Employee>> getInsertionEmployees() {
		List<Employee> insertionEmployees = null;
		try {
			insertionEmployees = employeeService.getInsertionEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(insertionEmployees, HttpStatus.OK);
	}

}
