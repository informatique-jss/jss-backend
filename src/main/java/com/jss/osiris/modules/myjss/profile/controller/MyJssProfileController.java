package com.jss.osiris.modules.myjss.profile.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.profile.model.SiteMapEntry;
import com.jss.osiris.modules.myjss.profile.model.SiteMapUrl;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.myjss.quotation.controller.MyJssQuotationValidationHelper;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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

	@Autowired
	UserScopeService userScopeService;

	@Autowired
	ConstantService constantService;

	@Autowired
	SearchService searchService;

	@Autowired
	MyJssQuotationValidationHelper myJssQuotationValidationHelper;

	@Autowired
	TiersService tiersService;

	private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
	private final long rateLimit = 10;
	private LocalDateTime lastFloodFlush = LocalDateTime.now();
	private int floodFlushDelayMinute = 1;

	@GetMapping(inputEntryPoint + "/user")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Responsable> getMyUsername() throws OsirisClientMessageException {
		return new ResponseEntity<Responsable>(employeeService.getCurrentMyJssUser(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login/token/send")
	public ResponseEntity<String> sendTokenToResponsable(@RequestParam String mail, HttpServletRequest request)
			throws OsirisException {
		ResponseEntity<String> isFlood = detectFlood(request);
		if (isFlood != null)
			return isFlood;

		Responsable responsable;
		String overrideMail = null;

		if (mail.contains("#") && mail.endsWith("@jss.fr")) {
			String[] mailToken = mail.split("#");
			responsable = responsableService.getResponsable(Integer.parseInt(mailToken[0]));
			overrideMail = mailToken[1];
		} else {
			responsable = responsableService.getResponsableByMail(mail);
		}

		if (responsable == null)
			return new ResponseEntity<String>("", HttpStatus.OK);

		employeeService.sendTokenToResponsable(responsable, overrideMail);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login/signout")
	public ResponseEntity<String> signOut(HttpServletRequest request)
			throws OsirisException {
		SecurityContextHolder.getContext().setAuthentication(null);
		HttpSession session = request.getSession();
		session.invalidate();
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/login")
	public ResponseEntity<String> login(@RequestParam Integer userId, @RequestParam String aToken,
			HttpServletRequest request) throws OsirisClientMessageException {

		ResponseEntity<String> isFlood = detectFlood(request);
		if (isFlood != null)
			return isFlood;

		if (employeeService.getCurrentEmployee() != null)
			return new ResponseEntity<String>("", HttpStatus.OK);

		if (employeeService.getCurrentMyJssUser() != null)
			return new ResponseEntity<String>("", HttpStatus.OK);

		Responsable responsable = responsableService.getResponsable(userId);
		if (responsable == null)
			throw new OsirisClientMessageException("Identifiant incorrect");

		if (responsable.getLoginTokenExpirationDateTime().isBefore(LocalDateTime.now()))
			throw new OsirisClientMessageException(
					"Identification expirée, veuillez renouveler votre demande de connexion");

		String token = responsable.getLoginToken();
		if (aToken.equals(token)) {
			authenticateUser(responsable, request);

			return new ResponseEntity<String>("", HttpStatus.OK);
		}
		SecurityContextHolder.getContext().setAuthentication(null);
		throw new OsirisClientMessageException(
				"Identification expirée, veuillez renouveler votre demande de connexion");
	}

	@GetMapping(inputEntryPoint + "/switch")
	public ResponseEntity<String> switchUser(@RequestParam Integer newUserId, HttpServletRequest request)
			throws OsirisClientMessageException {

		ResponseEntity<String> isFlood = detectFlood(request);
		if (isFlood != null)
			return isFlood;

		if (employeeService.getCurrentEmployee() != null)
			return new ResponseEntity<String>("", HttpStatus.OK);

		if (employeeService.getCurrentMyJssUser() == null)
			return new ResponseEntity<String>("", HttpStatus.OK);

		Responsable responsable = responsableService.getResponsable(newUserId);
		if (responsable == null)
			throw new OsirisClientMessageException("Identifiant incorrect");

		List<Responsable> userScope = userScopeService.getPotentialUserScope();
		if (userScope != null)
			for (Responsable scope : userScope)
				if (scope.getId().equals(newUserId)) {
					authenticateUser(responsable, request);
					return new ResponseEntity<String>("", HttpStatus.OK);
				}

		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	public void authenticateUser(Responsable responsable, HttpServletRequest request) {
		userScopeService.authenticateUser(responsable, request);
	}

	@GetMapping(inputEntryPoint + "/login/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getUserRoles() {
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(activeDirectoryHelper.getUserRoles(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user/scope/possible")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Responsable>> getPotentialUserScope() {
		return new ResponseEntity<List<Responsable>>(userScopeService.getPotentialUserScope(), HttpStatus.OK);
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

	@GetMapping(inputEntryPoint + "/search/global")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<IndexEntity>> globalSearchForEntity(@RequestParam String searchText)
			throws OsirisException {
		if (searchText != null && searchText.length() > 2)
			return new ResponseEntity<List<IndexEntity>>(searchService.searchEntitiesForCustomer(searchText),
					HttpStatus.OK);
		return new ResponseEntity<List<IndexEntity>>(new ArrayList<IndexEntity>(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/responsable")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Responsable> getResponsable(@RequestParam Integer idResponsable)
			throws OsirisClientMessageException {
		Responsable responsable = responsableService.getResponsable(idResponsable);

		if (responsable == null || !myJssQuotationValidationHelper.canSeeResponsable(responsable))
			return new ResponseEntity<Responsable>(new Responsable(), HttpStatus.OK);

		return new ResponseEntity<Responsable>(responsable, HttpStatus.OK);
	}

	@GetMapping(value = "/sitemap/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getSitemap(HttpServletRequest request) throws OsirisException {
		String baseUrl = request.getScheme() + "://" + request.getServerName();

		LocalDate today = LocalDate.now();
		List<SiteMapUrl> entries = List.of(
				new SiteMapUrl(baseUrl + "/home", today.toString()),
				new SiteMapUrl(baseUrl + "/contact", today.minusDays(3).toString()));

		SiteMapEntry sitemap = new SiteMapEntry(entries);

		XmlMapper xmlMapper = new XmlMapper();
		String xml;
		try {
			xml = xmlMapper.writeValueAsString(sitemap);
		} catch (JsonProcessingException e) {
			throw new OsirisException(e, "Error when generating sitemap");
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_XML)
				.body(xml);
	}

	@GetMapping(inputEntryPoint + "/responsable/accept-terms")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> updateAcceptTermsForCurrentUser()
			throws OsirisValidationException {
		responsableService.updateConsentDateForCurrentUser();
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tiers/responsables/current")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Responsable>> getResponsablesForCurrentUser()
			throws OsirisException {
		Responsable currentUser = employeeService.getCurrentMyJssUser();

		if (currentUser != null && Boolean.TRUE.equals(currentUser.getCanViewAllTiersInWeb())) {
			return new ResponseEntity<List<Responsable>>(
					tiersService.getTiers(currentUser.getTiers().getId()).getResponsables(), HttpStatus.OK);
		}
		return new ResponseEntity<List<Responsable>>(new ArrayList<Responsable>(), HttpStatus.OK);
	}
}
