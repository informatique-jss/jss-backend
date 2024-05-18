package com.jss.osiris.modules.profile.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.User;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.tiers.service.ResponsableService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class ProfileController {

	private static final String inputEntryPoint = "/profile";

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ActiveDirectoryHelper activeDirectoryHelper;

	@Autowired
	ResponsableService responsableService;

	@GetMapping(inputEntryPoint + "/login/check")
	@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
	public ResponseEntity<Boolean> checkLogin() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user")
	@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
	public ResponseEntity<Employee> getMyUsername() {
		return new ResponseEntity<Employee>(employeeService.getCurrentEmployee(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/login")
	public ResponseEntity<Boolean> login(@RequestBody User user, HttpServletRequest request) {
		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					user.getUsername(), user.getPassword());
			token.setDetails(new WebAuthenticationDetails(request));

			Authentication auth = authenticationManager.authenticate(token);
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(auth);

			if (auth.isAuthenticated()) {
				SecurityContextHolder.getContext().getAuthentication();

				HttpSession session = request.getSession(true);
				session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
				return new ResponseEntity<Boolean>(true, HttpStatus.OK);
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);
			}
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
	}

	@GetMapping(inputEntryPoint + "/login/roles")
	@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/all")
	@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
	public ResponseEntity<List<Employee>> getEmployees() {
		return new ResponseEntity<List<Employee>>(employeeService.getEmployees(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/employee")
	@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
	public ResponseEntity<Employee> addOrUpdateEmployee(
			@RequestBody Employee employee) throws OsirisValidationException, OsirisException {
		List<Employee> backups = new ArrayList<Employee>();

		if (employee != null)
			backups = employee.getBackups();

		employee = (Employee) validationHelper.validateReferential(employee, true, "employee");

		if (backups != null)
			for (Employee backup : backups)
				validationHelper.validateReferential(backup, true, "backup");

		employee.setBackups(backups);

		return new ResponseEntity<Employee>(employeeService.addOrUpdateEmployee(employee), HttpStatus.OK);
	}

}
