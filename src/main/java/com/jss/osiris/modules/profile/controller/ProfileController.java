package com.jss.osiris.modules.profile.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.User;
import com.jss.osiris.modules.profile.service.EmployeeService;

@RestController
public class ProfileController {

	private static final String inputEntryPoint = "/profile";

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ActiveDirectoryHelper activeDirectoryHelper;

	@GetMapping(inputEntryPoint + "/login/check")
	public ResponseEntity<Boolean> checkLogin() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
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

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/login/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}

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

	@GetMapping(inputEntryPoint + "/employee/all")
	public ResponseEntity<List<Employee>> getEmployees() {
		List<Employee> salesEmployees = null;
		try {
			salesEmployees = employeeService.getEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(salesEmployees, HttpStatus.OK);
	}

}
