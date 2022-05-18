package com.jss.jssbackend.modules.clients.controller;

import java.util.List;

import com.jss.jssbackend.modules.clients.model.Tiers;
import com.jss.jssbackend.modules.clients.model.TiersCategory;
import com.jss.jssbackend.modules.clients.model.TiersType;
import com.jss.jssbackend.modules.clients.service.TiersCategoryService;
import com.jss.jssbackend.modules.clients.service.TiersService;
import com.jss.jssbackend.modules.clients.service.TiersTypesService;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.service.CivilityService;
import com.jss.jssbackend.modules.miscellaneous.service.DeliveryServiceService;
import com.jss.jssbackend.modules.miscellaneous.service.LanguageService;
import com.jss.jssbackend.modules.profile.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
public class TiersController {

	private static final String inputEntryPoint = "/tiers";

	private static final Logger logger = LoggerFactory.getLogger(TiersController.class);

	@Autowired
	TiersTypesService clientTypesService;

	@Autowired
	TiersService tiersService;

	@Autowired
	CivilityService civilityService;

	@Autowired
	DeliveryServiceService deliveryServiceService;

	@Autowired
	TiersCategoryService tiersCategoryService;

	@Autowired
	LanguageService languageService;

	@Autowired
	EmployeeService employeeService;

	@GetMapping(inputEntryPoint + "/types")
	public ResponseEntity<List<TiersType>> getClientTypes() {
		List<TiersType> clientTypes = null;
		try {
			clientTypes = clientTypesService.getTiersTypes();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<TiersType>>(clientTypes, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/languages")
	public ResponseEntity<List<Language>> getLanguages() {
		List<Language> languages = null;
		try {
			languages = languageService.getLanguages();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Language>>(languages, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/civilities")
	public ResponseEntity<List<Civility>> getCivilities() {
		List<Civility> civilities = null;
		try {
			civilities = civilityService.getCivilities();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Civility>>(civilities, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/delivery-services")
	public ResponseEntity<List<DeliveryService>> getDeliveryServices() {
		List<DeliveryService> deliveryServices = null;
		try {
			deliveryServices = deliveryServiceService.getDeliveryServices();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<DeliveryService>>(deliveryServices, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/categories")
	public ResponseEntity<List<TiersCategory>> getCategories() {
		List<TiersCategory> civilities = null;
		try {
			civilities = tiersCategoryService.getTiersCategories();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<TiersCategory>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<TiersCategory>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<TiersCategory>>(civilities, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tiers")
	public ResponseEntity<Tiers> getTiersById(@RequestParam Integer id) {
		Tiers tiers = null;
		try {
			tiers = tiersService.getTiersById(id);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/tiers")
	public ResponseEntity<Tiers> addOrUpdateTiers(@RequestBody Tiers tiers) {
		if (tiers.getClientType() == null || tiers.getClientType().getId() == null
				|| clientTypesService.getTiersType(tiers.getClientType().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getIsIndividual() == true && tiers.getDenomination() != null && !tiers.getDenomination().equals("")
				|| tiers.getIsIndividual() == false
						&& (tiers.getDenomination() == null || tiers.getDenomination().length() > 60))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getIsIndividual() == true
				&& (tiers.getCivility() == null || tiers.getCivility().getId() == null
						|| civilityService.getCivility(tiers.getCivility().getId()) == null)
				|| tiers.getIsIndividual() == false && tiers.getCivility() != null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getTiersCategory() != null && tiers.getTiersCategory().getId() != null
				&& tiersCategoryService.getTiersCategory(tiers.getTiersCategory().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getSalesEmployee() == null || tiers.getSalesEmployee().getId() == null
				|| employeeService.getEmployeeById(tiers.getSalesEmployee().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getFormalisteEmployee() != null && (tiers.getSalesEmployee().getId() == null
				|| employeeService.getEmployeeById(tiers.getFormalisteEmployee().getId()) == null))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getInsertionEmployee() != null && (tiers.getInsertionEmployee().getId() == null
				|| employeeService.getEmployeeById(tiers.getInsertionEmployee().getId()) == null))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getLanguage() == null || tiers.getLanguage().getId() == null
				|| languageService.getLanguageById(tiers.getLanguage().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getDeliveryService() == null || tiers.getDeliveryService().getId() == null
				|| deliveryServiceService.getDeliveryServiceById(tiers.getDeliveryService().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		try {
			tiers = tiersService.addOrUpdateTiers(tiers);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
	}

}
