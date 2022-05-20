package com.jss.jssbackend.modules.tiers.controller;

import java.util.List;

import com.jss.jssbackend.libs.ValidationHelper;
import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.model.Region;
import com.jss.jssbackend.modules.miscellaneous.model.VatRate;
import com.jss.jssbackend.modules.miscellaneous.service.CityService;
import com.jss.jssbackend.modules.miscellaneous.service.CivilityService;
import com.jss.jssbackend.modules.miscellaneous.service.CountryService;
import com.jss.jssbackend.modules.miscellaneous.service.DeliveryServiceService;
import com.jss.jssbackend.modules.miscellaneous.service.DepartmentService;
import com.jss.jssbackend.modules.miscellaneous.service.LanguageService;
import com.jss.jssbackend.modules.miscellaneous.service.RegionService;
import com.jss.jssbackend.modules.miscellaneous.service.VatRateService;
import com.jss.jssbackend.modules.profile.service.EmployeeService;
import com.jss.jssbackend.modules.tiers.model.BillingItem;
import com.jss.jssbackend.modules.tiers.model.BillingType;
import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.model.SpecialOffer;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersCategory;
import com.jss.jssbackend.modules.tiers.model.TiersType;
import com.jss.jssbackend.modules.tiers.service.BillingItemService;
import com.jss.jssbackend.modules.tiers.service.BillingTypeService;
import com.jss.jssbackend.modules.tiers.service.MailService;
import com.jss.jssbackend.modules.tiers.service.PhoneService;
import com.jss.jssbackend.modules.tiers.service.SpecialOfferService;
import com.jss.jssbackend.modules.tiers.service.TiersCategoryService;
import com.jss.jssbackend.modules.tiers.service.TiersService;
import com.jss.jssbackend.modules.tiers.service.TiersTypesService;

import org.apache.commons.validator.routines.EmailValidator;
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

	@Autowired
	CountryService countryService;

	@Autowired
	CityService cityService;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	RegionService regionService;

	@Autowired
	SpecialOfferService specialOfferService;

	@Autowired
	BillingItemService billingItemService;

	@Autowired
	BillingTypeService billingTypeService;

	@Autowired
	VatRateService vatRateService;

	@Autowired
	MailService mailService;

	@Autowired
	PhoneService phoneService;

	@GetMapping(inputEntryPoint + "/phones/search")
	public ResponseEntity<List<Phone>> findPhones(@RequestParam String phone) {
		List<Phone> phones = null;
		try {
			phones = phoneService.findPhones(phone);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching phone", e);
			return new ResponseEntity<List<Phone>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching phone", e);
			return new ResponseEntity<List<Phone>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Phone>>(phones, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/mails/search")
	public ResponseEntity<List<Mail>> findMails(@RequestParam String mail) {
		List<Mail> mails = null;
		try {
			mails = mailService.findMails(mail);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching mail", e);
			return new ResponseEntity<List<Mail>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching mail", e);
			return new ResponseEntity<List<Mail>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Mail>>(mails, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/vat-rates")
	public ResponseEntity<List<VatRate>> getVatRates() {
		List<VatRate> vatRates = null;
		try {
			vatRates = vatRateService.getVatRates();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching vatRate", e);
			return new ResponseEntity<List<VatRate>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching vatRate", e);
			return new ResponseEntity<List<VatRate>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<VatRate>>(vatRates, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/billingTypes")
	public ResponseEntity<List<BillingType>> getBillingTypes() {
		List<BillingType> billingTypes = null;
		try {
			billingTypes = billingTypeService.getBillingTypes();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching billingType", e);
			return new ResponseEntity<List<BillingType>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching billingType", e);
			return new ResponseEntity<List<BillingType>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<BillingType>>(billingTypes, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/billing-items")
	public ResponseEntity<List<BillingItem>> getBillingItems() {
		List<BillingItem> billingItems = null;
		try {
			billingItems = billingItemService.getBillingItems();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching billingItem", e);
			return new ResponseEntity<List<BillingItem>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching billingItem", e);
			return new ResponseEntity<List<BillingItem>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<BillingItem>>(billingItems, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/special-offers")
	public ResponseEntity<List<SpecialOffer>> getSpecialOffers() {
		List<SpecialOffer> specialOffers = null;
		try {
			specialOffers = specialOfferService.getSpecialOffers();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching specialOffer", e);
			return new ResponseEntity<List<SpecialOffer>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching specialOffer", e);
			return new ResponseEntity<List<SpecialOffer>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<SpecialOffer>>(specialOffers, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/regions")
	public ResponseEntity<List<Region>> getRegions() {
		List<Region> regions = null;
		try {
			regions = regionService.getRegions();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching region", e);
			return new ResponseEntity<List<Region>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching region", e);
			return new ResponseEntity<List<Region>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Region>>(regions, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/departments")
	public ResponseEntity<List<Department>> getDepartments() {
		List<Department> departments = null;
		try {
			departments = departmentService.getDepartments();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching department", e);
			return new ResponseEntity<List<Department>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching department", e);
			return new ResponseEntity<List<Department>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Department>>(departments, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities")
	public ResponseEntity<List<City>> getCities() {
		List<City> cities = null;
		try {
			cities = cityService.getCities();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities/search/postal-code")
	public ResponseEntity<List<City>> getCitiesByPostalCode(@RequestParam String postalCode) {
		List<City> cities = null;
		try {
			cities = cityService.getCitiesByPostalCode(postalCode);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities/search/country")
	public ResponseEntity<List<City>> getCitiesByCountry(@RequestParam(required = false) Integer countryId,
			@RequestParam String city) {
		List<City> cities = null;
		try {
			cities = cityService.getCitiesByCountry(countryId, city);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching city", e);
			return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/countries")
	public ResponseEntity<List<Country>> getCountries() {
		List<Country> countries = null;
		try {
			countries = countryService.getCountries();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching country", e);
			return new ResponseEntity<List<Country>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching country", e);
			return new ResponseEntity<List<Country>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Country>>(countries, HttpStatus.OK);
	}

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
		if (tiers.getTiersType() == null || tiers.getTiersType().getId() == null
				|| clientTypesService.getTiersType(tiers.getTiersType().getId()) == null)
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

		if (tiers.getAddress() == null || tiers.getAddress().equals("") || tiers.getAddress().length() > 60)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getCountry() != null && tiers.getCountry().getCode().equals("FR")
				&& (tiers.getPostalCode() == null || tiers.getPostalCode().equals("")
						|| cityService.getCitiesByPostalCode(tiers.getPostalCode()).size() == 0))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getCity() == null || tiers.getCity().getLabel() == null || tiers.getCity().getLabel().equals(""))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getCity().getId() != null && cityService.getCity(tiers.getCity().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getCity().getId() == null && tiers.getCity().getLabel().length() > 30)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		// TODO : crééer la ville en base si pas d'ID renseigné

		if (tiers.getCountry() == null || tiers.getCountry().getId() == null
				|| countryService.getCountry(tiers.getCountry().getId()) == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getIntercom() != null && tiers.getIntercom().length() > 12)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getIsIndividual() == false
				&& (tiers.getIntercommunityVat() == null || tiers.getIntercommunityVat().equals("")
						|| tiers.getIntercommunityVat().length() > 20))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getSpecialOffer() != null && (tiers.getSpecialOffer().getId() == null
				|| specialOfferService.getSpecialOffer(tiers.getSpecialOffer().getId()) == null))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		if (tiers.getMails() != null && tiers.getMails().size() > 0) {
			EmailValidator emailvalidator = EmailValidator.getInstance();
			for (Mail mail : tiers.getMails()) {
				if (!emailvalidator.isValid(mail.getMail()))
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

		if (tiers.getPhones() != null && tiers.getPhones().size() > 0) {
			for (Phone phone : tiers.getPhones()) {
				if (!ValidationHelper.validateFrenchPhone(phone.getPhoneNumber())
						|| !ValidationHelper.validateInternationalPhone(phone.getPhoneNumber()))
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

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
