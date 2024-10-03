package com.jss.osiris.modules.myjss.profile.controller;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.model.IOsirisUser;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class MyJssProfileController {

	private static final String inputEntryPoint = "/myjss/profile";

	@Autowired
	ActiveDirectoryHelper activeDirectoryHelper;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ResponsableService responsableService;

	@GetMapping(inputEntryPoint + "/login/check")
	public ResponseEntity<Boolean> checkLogin() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user")
	public ResponseEntity<IOsirisUser> getMyUsername() throws OsirisClientMessageException {
		return new ResponseEntity<IOsirisUser>(employeeService.getCurrentEmployee(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login/token/send")
	public ResponseEntity<Boolean> sendTokenToResponsable(@RequestParam Integer userId)
			throws OsirisException {
		Responsable responsable = responsableService.getResponsable(userId);
		if (responsable == null)
			throw new OsirisClientMessageException("Identifiant incorrect");
		employeeService.sendTokenToResponsable(responsable);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login")
	public ResponseEntity<Boolean> login(@RequestParam Integer userId, @RequestParam String aToken,
			HttpServletRequest request) {
		try {
			Responsable responsable = responsableService.getResponsable(userId);
			if (responsable == null)
				throw new OsirisClientMessageException("Identifiant incorrect");

			if (responsable.getLoginTokenExpirationDateTime().isBefore(LocalDateTime.now()))
				throw new OsirisClientMessageException(
						"Identification expirée, veuillez renouveler votre demande de connexion");

			String token = responsable.getLoginToken();
			if (aToken.equals(token)) {
				Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,
						AuthorityUtils.createAuthorityList(ActiveDirectoryHelper.MYJSS_USER_GROUP));

				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(authentication);

				HttpSession session = request.getSession(true);
				session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

				responsable.setLoginTokenExpirationDateTime(LocalDateTime.now().minusSeconds(1));
				responsableService.addOrUpdateResponsable(responsable);

				return new ResponseEntity<Boolean>(true, HttpStatus.OK);
			}
			SecurityContextHolder.getContext().setAuthentication(null);
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
	}

	@GetMapping(inputEntryPoint + "/login/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}
}
