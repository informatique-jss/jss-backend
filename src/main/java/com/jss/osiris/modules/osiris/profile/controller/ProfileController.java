package com.jss.osiris.modules.osiris.profile.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.User;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

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
	public ResponseEntity<Boolean> checkLogin() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user")
	public ResponseEntity<Employee> getMyUsername() throws OsirisClientMessageException {
		Employee employee = employeeService.getCurrentEmployee();
		if (employee == null)
			throw new OsirisClientMessageException("wrong id type");
		return new ResponseEntity<Employee>(employeeService.getEmployee(employee.getId()), HttpStatus.OK);
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

	@PostMapping(inputEntryPoint + "/login/website")
	public ResponseEntity<Responsable> loginWebsiteUser(@RequestBody User user, HttpServletRequest request)
			throws OsirisValidationException {
		validationHelper.validateString(user.getUsername(), true, 255, "Username");
		validationHelper.validateString(user.getPassword(), true, 255, "Password");
		return new ResponseEntity<Responsable>(employeeService.loginWebsiteUser(user, false), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/introspection/login/website")
	public ResponseEntity<Responsable> loginWebsiteUserWithIntrospection(@RequestBody User user,
			HttpServletRequest request)
			throws OsirisValidationException {
		validationHelper.validateString(user.getUsername(), true, 255, "Username");
		validationHelper.validateString(user.getPassword(), true, 255, "Password");
		return new ResponseEntity<Responsable>(employeeService.loginWebsiteUser(user, true), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/responsable/password")
	public ResponseEntity<Boolean> renewResponsablePassword(@RequestParam Integer idResponsable)
			throws OsirisValidationException, OsirisException {
		Responsable responsable = responsableService.getResponsable(idResponsable);

		if (responsable == null) {
			responsable = responsableService.getResponsableByLoginWeb(idResponsable + "");
			if (responsable == null)
				throw new OsirisValidationException("idResponsable");
		}
		return new ResponseEntity<Boolean>(employeeService.renewResponsablePassword(responsable),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/responsable/password/modify")
	public ResponseEntity<Boolean> modifyResponsablePassword(@RequestParam Integer idResponsable,
			@RequestBody String newPassword)
			throws OsirisValidationException, OsirisException {
		Responsable responsable = responsableService.getResponsable(idResponsable);

		if (responsable == null) {
			responsable = responsableService.getResponsableByLoginWeb(idResponsable + "");
			if (responsable == null)
				throw new OsirisValidationException("idResponsable");
		}

		if (!SSLHelper.checkPasswordStrength(newPassword))
			throw new OsirisValidationException("Weak password");
		return new ResponseEntity<Boolean>(employeeService.modifyResponsablePassword(responsable, newPassword),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/all")
	public ResponseEntity<List<Employee>> getEmployees() {
		return new ResponseEntity<List<Employee>>(employeeService.getEmployees(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/employee")
	public ResponseEntity<Employee> addOrUpdateEmployee(
			@RequestBody Employee employee) throws OsirisValidationException, OsirisException {
		List<Employee> backups = new ArrayList<Employee>();
		List<String> notificationTypesToHide = new ArrayList<String>();

		if (employee != null) {
			backups = employee.getBackups();
			notificationTypesToHide = employee.getNotificationTypeToHide();
		}

		employee = (Employee) validationHelper.validateReferential(employee, true, "employee");

		if (backups != null)
			for (Employee backup : backups)
				validationHelper.validateReferential(backup, true, "backup");

		employee.setBackups(backups);
		employee.setNotificationTypeToHide(notificationTypesToHide);

		return new ResponseEntity<Employee>(employeeService.addOrUpdateEmployee(employee), HttpStatus.OK);
	}

}
