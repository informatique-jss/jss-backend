package com.jss.osiris.modules.myjss.profile.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.jackson.JacksonViews;
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

	private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
	private final long rateLimit = 10;
	private LocalDateTime lastFloodFlush = LocalDateTime.now();
	private int floodFlushDelayMinute = 1;

	@GetMapping(inputEntryPoint + "/login/check")
	public ResponseEntity<Boolean> checkLogin() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<IOsirisUser> getMyUsername() throws OsirisClientMessageException {
		return new ResponseEntity<IOsirisUser>(employeeService.getCurrentEmployee(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login/token/send")
	public ResponseEntity<String> sendTokenToResponsable(@RequestParam Integer userId, HttpServletRequest request)
			throws OsirisException {
		ResponseEntity<String> isFlood = detectFlood(request);
		if (isFlood != null)
			return isFlood;

		Responsable responsable = responsableService.getResponsable(userId);
		if (responsable == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", "/login/token/send/error");
			return new ResponseEntity<String>(headers, HttpStatus.FOUND);
		}
		employeeService.sendTokenToResponsable(responsable);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/login/token/send/success");
		return new ResponseEntity<String>(headers, HttpStatus.FOUND);
	}

	@GetMapping(inputEntryPoint + "/login")
	public ResponseEntity<String> login(@RequestParam Integer userId, @RequestParam String aToken,
			HttpServletRequest request) {

		ResponseEntity<String> isFlood = detectFlood(request);
		if (isFlood != null)
			return isFlood;

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

				HttpHeaders headers = new HttpHeaders();
				headers.add("Location", "/login/success");
				return new ResponseEntity<String>(headers, HttpStatus.FOUND);
			}
			SecurityContextHolder.getContext().setAuthentication(null);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", "/login/error");
			return new ResponseEntity<String>(headers, HttpStatus.FOUND);
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(inputEntryPoint + "/login/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}

	private ResponseEntity<String> detectFlood(HttpServletRequest request) {
		if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
			requestCount.clear();
		else
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

		String ipAddress = request.getRemoteAddr();
		AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

		if (count.incrementAndGet() > rateLimit) {
			return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
		}
		return null;
	}
}