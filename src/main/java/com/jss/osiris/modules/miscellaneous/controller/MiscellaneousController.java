package com.jss.osiris.modules.miscellaneous.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.model.Gift;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.model.Regie;
import com.jss.osiris.modules.miscellaneous.model.Region;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.BillingTypeService;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityTypeService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DeliveryServiceService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.miscellaneous.service.GiftService;
import com.jss.osiris.modules.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.miscellaneous.service.ProviderService;
import com.jss.osiris.modules.miscellaneous.service.RegieService;
import com.jss.osiris.modules.miscellaneous.service.RegionService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatCollectionTypeService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.miscellaneous.service.WeekDayService;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@RestController
public class MiscellaneousController {

    private static final String inputEntryPoint = "/miscellaneous";

    private static final Logger logger = LoggerFactory.getLogger(MiscellaneousController.class);

    @Autowired
    AttachmentTypeService attachmentTypeService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    DocumentTypeService documentTypeService;

    @Autowired
    LegalFormService legalFormService;

    @Autowired
    WeekDayService weekDayService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    CompetentAuthorityTypeService competentAuthorityTypeService;

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    CivilityService civilityService;

    @Autowired
    CountryService countryService;

    @Autowired
    DeliveryServiceService deliveryServiceService;

    @Autowired
    LanguageService languageService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    RegionService regionService;

    @Autowired
    VatService vatService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    BillingTypeService billingTypeService;

    @Autowired
    CityService cityService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    SpecialOfferService specialOfferService;

    @Autowired
    GiftService giftService;

    @Autowired
    VatCollectionTypeService vatCollectionTypeService;

    @Autowired
    ProviderService providerService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    RegieService regieService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    ConstantService constantService;

    @GetMapping(inputEntryPoint + "/constants")
    public ResponseEntity<Constant> getConstants() {
        Constant constants = null;
        try {
            constants = constantService.getConstants();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching constant", e);
            return new ResponseEntity<Constant>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching constant", e);
            return new ResponseEntity<Constant>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Constant>(constants, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/constant")
    public ResponseEntity<Constant> addOrUpdateConstant(
            @RequestBody Constant constant) {
        Constant outConstant;
        try {
            if (constant.getId() != null)
                validationHelper.validateReferential(constant, true);
            // TODO complete
            validationHelper.validateReferential(constant.getBillingLabelTypeCodeAffaire(), true);
            outConstant = constantService
                    .addOrUpdateConstant(constant);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching constant", e);
            return new ResponseEntity<Constant>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching constant", e);
            return new ResponseEntity<Constant>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Constant>(outConstant, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/regies")
    public ResponseEntity<List<Regie>> getRegies() {
        List<Regie> regies = null;
        try {
            regies = regieService.getRegies();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regie", e);
            return new ResponseEntity<List<Regie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regie", e);
            return new ResponseEntity<List<Regie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Regie>>(regies, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/regie")
    public ResponseEntity<Regie> addOrUpdateRegie(
            @RequestBody Regie regie) {
        Regie outRegie;
        try {
            if (regie.getId() != null)
                validationHelper.validateReferential(regie, true);
            validationHelper.validateString(regie.getCode(), true);
            validationHelper.validateString(regie.getLabel(), true);
            validationHelper.validateReferential(regie.getCountry(), true);
            validationHelper.validateReferential(regie.getCity(), true);
            validationHelper.validateString(regie.getPostalCode(), false, 6);
            validationHelper.validateString(regie.getAddress(), true, 60);
            validationHelper.validateString(regie.getIban(), true, 40);

            outRegie = regieService
                    .addOrUpdateRegie(regie);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regie", e);
            return new ResponseEntity<Regie>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regie", e);
            return new ResponseEntity<Regie>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Regie>(outRegie, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/providers")
    public ResponseEntity<List<Provider>> getProviders() {
        List<Provider> providers = null;
        try {
            providers = providerService.getProviders();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching provider", e);
            return new ResponseEntity<List<Provider>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching provider", e);
            return new ResponseEntity<List<Provider>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Provider>>(providers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/provider")
    public ResponseEntity<Provider> addOrUpdateProvider(
            @RequestBody Provider provider) {
        Provider outProvider;
        try {
            if (provider.getId() != null)
                validationHelper.validateReferential(provider, true);
            validationHelper.validateString(provider.getLabel(), true);
            validationHelper.validateString(provider.getIban(), false, 40);
            validationHelper.validateString(provider.getJssReference(), false, 20);
            validationHelper.validateReferential(provider.getVatCollectionType(), true);
            validationHelper.validateReferential(provider.getPaymentType(), false);

            outProvider = providerService
                    .addOrUpdateProvider(provider);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching provider", e);
            return new ResponseEntity<Provider>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching provider", e);
            return new ResponseEntity<Provider>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Provider>(outProvider, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/vat-collection-types")
    public ResponseEntity<List<VatCollectionType>> getVatCollectionTypes() {
        List<VatCollectionType> vatCollectionTypes = null;
        try {
            vatCollectionTypes = vatCollectionTypeService.getVatCollectionTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching vatCollectionType", e);
            return new ResponseEntity<List<VatCollectionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching vatCollectionType", e);
            return new ResponseEntity<List<VatCollectionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<VatCollectionType>>(vatCollectionTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/vat-collection-type")
    public ResponseEntity<VatCollectionType> addOrUpdateVatCollectionType(
            @RequestBody VatCollectionType vatCollectionTypes) {
        VatCollectionType outVatCollectionType;
        try {
            if (vatCollectionTypes.getId() != null)
                validationHelper.validateReferential(vatCollectionTypes, true);
            validationHelper.validateString(vatCollectionTypes.getCode(), true);
            validationHelper.validateString(vatCollectionTypes.getLabel(), true);

            outVatCollectionType = vatCollectionTypeService
                    .addOrUpdateVatCollectionType(vatCollectionTypes);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching vatCollectionType", e);
            return new ResponseEntity<VatCollectionType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching vatCollectionType", e);
            return new ResponseEntity<VatCollectionType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<VatCollectionType>(outVatCollectionType, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/gifts")
    public ResponseEntity<List<Gift>> getGifts() {
        List<Gift> gifts = null;
        try {
            gifts = giftService.getGifts();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching gift", e);
            return new ResponseEntity<List<Gift>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching gift", e);
            return new ResponseEntity<List<Gift>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Gift>>(gifts, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/gift")
    public ResponseEntity<Gift> addOrUpdateGift(
            @RequestBody Gift gifts) {
        Gift outGift;
        try {
            if (gifts.getId() != null)
                validationHelper.validateReferential(gifts, true);
            validationHelper.validateString(gifts.getCode(), true, 20);
            validationHelper.validateString(gifts.getLabel(), true, 100);
            validationHelper.validateReferential(gifts.getAccountingAccount(), false);

            outGift = giftService
                    .addOrUpdateGift(gifts);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching gift", e);
            return new ResponseEntity<Gift>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching gift", e);
            return new ResponseEntity<Gift>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Gift>(outGift, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/special-offer")
    public ResponseEntity<SpecialOffer> addOrUpdateSpecialOffer(
            @RequestBody SpecialOffer specialOffers) {
        SpecialOffer outSpecialOffer;
        try {
            if (specialOffers.getId() != null)
                validationHelper.validateReferential(specialOffers, true);
            validationHelper.validateString(specialOffers.getCode(), true, 20);
            validationHelper.validateString(specialOffers.getLabel(), true, 100);

            if (specialOffers.getAssoSpecialOfferBillingTypes() == null
                    || specialOffers.getAssoSpecialOfferBillingTypes().size() == 0)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for (AssoSpecialOfferBillingType asso : specialOffers.getAssoSpecialOfferBillingTypes())
                validationHelper.validateReferential(asso.getBillingType(), true);

            outSpecialOffer = specialOfferService
                    .addOrUpdateSpecialOffer(specialOffers);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching specialOffer", e);
            return new ResponseEntity<SpecialOffer>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching specialOffer", e);
            return new ResponseEntity<SpecialOffer>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<SpecialOffer>(outSpecialOffer, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/department")
    public ResponseEntity<Department> addOrUpdateDepartment(
            @RequestBody Department departments) {
        Department outDepartment;
        try {
            if (departments.getId() != null)
                validationHelper.validateReferential(departments, true);
            validationHelper.validateString(departments.getCode(), true, 20);
            validationHelper.validateString(departments.getLabel(), true, 100);

            outDepartment = departmentService
                    .addOrUpdateDepartment(departments);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching department", e);
            return new ResponseEntity<Department>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching department", e);
            return new ResponseEntity<Department>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Department>(outDepartment, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/city")
    public ResponseEntity<City> addOrUpdateCity(
            @RequestBody City cities) {
        City outCity;
        try {
            if (cities.getId() != null)
                validationHelper.validateReferential(cities, true);
            validationHelper.validateString(cities.getCode(), true, 20);
            validationHelper.validateString(cities.getLabel(), true, 100);
            validationHelper.validateString(cities.getPostalCode(), false, 6);
            validationHelper.validateReferential(cities.getDepartment(), false);
            validationHelper.validateReferential(cities.getCountry(), true);

            outCity = cityService
                    .addOrUpdateCity(cities);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<City>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<City>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<City>(outCity, HttpStatus.OK);
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

    @GetMapping(inputEntryPoint + "/billing-types")
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

    @PostMapping(inputEntryPoint + "/billing-type")
    public ResponseEntity<BillingType> addOrUpdateBillingType(
            @RequestBody BillingType billingType) {
        BillingType outBillingType;
        try {
            if (billingType.getId() != null)
                validationHelper.validateReferential(billingType, true);
            validationHelper.validateString(billingType.getCode(), true, 20);
            validationHelper.validateString(billingType.getLabel(), true, 100);
            validationHelper.validateReferential(billingType.getVat(), billingType.getIsOverrideVat());
            if (!billingType.getIsOverrideVat())
                billingType.setVat(null);

            outBillingType = billingTypeService
                    .addOrUpdateBillingType(billingType);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching billingType", e);
            return new ResponseEntity<BillingType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching billingType", e);
            return new ResponseEntity<BillingType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<BillingType>(outBillingType, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/billing-item")
    public ResponseEntity<BillingItem> addOrUpdateBillingItem(
            @RequestBody BillingItem billingItems) {
        BillingItem outBillingItem = null;
        try {
            if (billingItems.getId() != null)
                validationHelper.validateReferential(billingItems, true);
            validationHelper.validateReferential(billingItems.getBillingType(), true);

            validationHelper.validateDate(billingItems.getStartDate(), true);

            outBillingItem = billingItemService.addOrUpdateBillingItem(billingItems);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching billingItem", e);
            return new ResponseEntity<BillingItem>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching billingItem", e);
            return new ResponseEntity<BillingItem>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<BillingItem>(outBillingItem, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/vats")
    public ResponseEntity<List<Vat>> getVats() {
        List<Vat> vats = null;
        try {
            vats = vatService.getVats();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching vat", e);
            return new ResponseEntity<List<Vat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching vat", e);
            return new ResponseEntity<List<Vat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Vat>>(vats, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/vat")
    public ResponseEntity<Vat> addOrUpdateVat(
            @RequestBody Vat vat) {
        Vat outVat;
        try {
            if (vat.getId() != null)
                validationHelper.validateReferential(vat, true);
            validationHelper.validateString(vat.getCode(), true, 20);
            validationHelper.validateString(vat.getLabel(), true, 100);
            validationHelper.validateReferential(vat.getAccountingAccount(), true);
            validationHelper.validateFloat(vat.getRate(), true);

            outVat = vatService.addOrUpdateVat(vat);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching vat", e);
            return new ResponseEntity<Vat>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching vat", e);
            return new ResponseEntity<Vat>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Vat>(outVat, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/region")
    public ResponseEntity<Region> addOrUpdateRegion(
            @RequestBody Region regions) {
        Region outRegion;
        try {
            if (regions.getId() != null)
                validationHelper.validateReferential(regions, true);
            validationHelper.validateString(regions.getCode(), true, 20);
            validationHelper.validateString(regions.getLabel(), true, 100);

            outRegion = regionService
                    .addOrUpdateRegion(regions);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching region", e);
            return new ResponseEntity<Region>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching region", e);
            return new ResponseEntity<Region>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Region>(outRegion, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment-types")
    public ResponseEntity<List<PaymentType>> getPaymentTypes() {
        List<PaymentType> paymentTypes = null;
        try {
            paymentTypes = paymentTypeService.getPaymentTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching paymentType", e);
            return new ResponseEntity<List<PaymentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching paymentType", e);
            return new ResponseEntity<List<PaymentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<PaymentType>>(paymentTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payment-type")
    public ResponseEntity<PaymentType> addOrUpdatePaymentType(
            @RequestBody PaymentType paymentTypes) {
        PaymentType outPaymentType;
        try {
            if (paymentTypes.getId() != null)
                validationHelper.validateReferential(paymentTypes, true);
            validationHelper.validateString(paymentTypes.getCode(), true, 20);
            validationHelper.validateString(paymentTypes.getLabel(), true, 100);

            outPaymentType = paymentTypeService
                    .addOrUpdatePaymentType(paymentTypes);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching paymentType", e);
            return new ResponseEntity<PaymentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching paymentType", e);
            return new ResponseEntity<PaymentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<PaymentType>(outPaymentType, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/languages")
    public ResponseEntity<List<Language>> getLanguages() {
        List<Language> languages = null;
        try {
            languages = languageService.getLanguages();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching language", e);
            return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching language", e);
            return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Language>>(languages, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/language")
    public ResponseEntity<Language> addOrUpdateLanguage(
            @RequestBody Language languages) {
        Language outLanguage;
        try {
            if (languages.getId() != null)
                validationHelper.validateReferential(languages, true);
            validationHelper.validateString(languages.getCode(), true, 20);
            validationHelper.validateString(languages.getLabel(), true, 100);

            outLanguage = languageService
                    .addOrUpdateLanguage(languages);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching language", e);
            return new ResponseEntity<Language>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching language", e);
            return new ResponseEntity<Language>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Language>(outLanguage, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/delivery-services")
    public ResponseEntity<List<DeliveryService>> getDeliveryServices() {
        List<DeliveryService> deliveryServices = null;
        try {
            deliveryServices = deliveryServiceService.getDeliveryServices();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching deliveryService", e);
            return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching deliveryService", e);
            return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DeliveryService>>(deliveryServices, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/delivery-service")
    public ResponseEntity<DeliveryService> addOrUpdateDeliveryService(
            @RequestBody DeliveryService deliveryServices) {
        DeliveryService outDeliveryService;
        try {
            if (deliveryServices.getId() != null)
                validationHelper.validateReferential(deliveryServices, true);
            validationHelper.validateString(deliveryServices.getCode(), true, 20);
            validationHelper.validateString(deliveryServices.getLabel(), true, 100);

            outDeliveryService = deliveryServiceService
                    .addOrUpdateDeliveryService(deliveryServices);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching deliveryService", e);
            return new ResponseEntity<DeliveryService>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching deliveryService", e);
            return new ResponseEntity<DeliveryService>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<DeliveryService>(outDeliveryService, HttpStatus.OK);
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

    @PostMapping(inputEntryPoint + "/country")
    public ResponseEntity<Country> addOrUpdateCountry(
            @RequestBody Country countries) {
        Country outCountry;
        try {
            if (countries.getId() != null)
                validationHelper.validateReferential(countries, true);
            validationHelper.validateString(countries.getCode(), true, 20);
            validationHelper.validateString(countries.getLabel(), true, 100);

            outCountry = countryService
                    .addOrUpdateCountry(countries);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching country", e);
            return new ResponseEntity<Country>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching country", e);
            return new ResponseEntity<Country>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Country>(outCountry, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/civilities")
    public ResponseEntity<List<Civility>> getCivilities() {
        List<Civility> civilities = null;
        try {
            civilities = civilityService.getCivilities();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching civility", e);
            return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching civility", e);
            return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Civility>>(civilities, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/civility")
    public ResponseEntity<Civility> addOrUpdateCivility(
            @RequestBody Civility civilities) {
        Civility outCivility;
        try {
            if (civilities.getId() != null)
                validationHelper.validateReferential(civilities, true);
            validationHelper.validateString(civilities.getCode(), true, 20);
            validationHelper.validateString(civilities.getLabel(), true, 100);

            outCivility = civilityService
                    .addOrUpdateCivility(civilities);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching civility", e);
            return new ResponseEntity<Civility>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching civility", e);
            return new ResponseEntity<Civility>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Civility>(outCivility, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities/search/department")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorityByDepartmentAndName(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam String authority) {
        List<CompetentAuthority> competentAuthorities = null;
        try {
            competentAuthorities = competentAuthorityService.getCompetentAuthorityByDepartment(departmentId, authority);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CompetentAuthority>>(competentAuthorities, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities/search/city")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorityByCity(Integer cityId) {
        if (cityId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<CompetentAuthority> competentAuthorities = null;
        try {
            competentAuthorities = competentAuthorityService.getCompetentAuthorityByCity(cityId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CompetentAuthority>>(competentAuthorities, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authority-types")
    public ResponseEntity<List<CompetentAuthorityType>> getCompetentAuthorityTypes() {
        List<CompetentAuthorityType> competentAuthorityTypes = null;
        try {
            competentAuthorityTypes = competentAuthorityTypeService.getCompetentAuthorityTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching competentAuthorityType", e);
            return new ResponseEntity<List<CompetentAuthorityType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching competentAuthorityType", e);
            return new ResponseEntity<List<CompetentAuthorityType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CompetentAuthorityType>>(competentAuthorityTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/competent-authority-type")
    public ResponseEntity<CompetentAuthorityType> addOrUpdateCompetentAuthorityType(
            @RequestBody CompetentAuthorityType competentAuthorityTypes) {
        CompetentAuthorityType outCompetentAuthorityType;
        try {
            if (competentAuthorityTypes.getId() != null)
                validationHelper.validateReferential(competentAuthorityTypes, true);
            validationHelper.validateString(competentAuthorityTypes.getCode(), true, 20);
            validationHelper.validateString(competentAuthorityTypes.getLabel(), true, 100);

            outCompetentAuthorityType = competentAuthorityTypeService
                    .addOrUpdateCompetentAuthorityType(competentAuthorityTypes);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching competentAuthorityType", e);
            return new ResponseEntity<CompetentAuthorityType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching competentAuthorityType", e);
            return new ResponseEntity<CompetentAuthorityType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CompetentAuthorityType>(outCompetentAuthorityType, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorities() {
        List<CompetentAuthority> competentAuthorities = null;
        try {
            competentAuthorities = competentAuthorityService.getCompetentAuthorities();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching competentAuthority", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching competentAuthority", e);
            return new ResponseEntity<List<CompetentAuthority>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CompetentAuthority>>(competentAuthorities, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/competent-authority")
    public ResponseEntity<CompetentAuthority> addOrUpdateCompetentAuthority(
            @RequestBody CompetentAuthority competentAuthorities) {
        CompetentAuthority outCompetentAuthority;
        try {
            if (competentAuthorities.getId() != null)
                validationHelper.validateReferential(competentAuthorities, true);
            validationHelper.validateString(competentAuthorities.getCode(), true, 20);
            validationHelper.validateString(competentAuthorities.getLabel(), true, 100);
            validationHelper.validateString(competentAuthorities.getSchedulle(), false, 150);
            validationHelper.validateReferential(competentAuthorities.getCompetentAuthorityType(), true);
            if (competentAuthorities.getCities() == null && competentAuthorities.getDepartments() == null
                    && competentAuthorities.getRegions() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (competentAuthorities.getCities() != null)
                for (City city : competentAuthorities.getCities())
                    validationHelper.validateReferential(city, false);

            if (competentAuthorities.getRegions() != null)
                for (Region region : competentAuthorities.getRegions())
                    validationHelper.validateReferential(region, false);

            if (competentAuthorities.getDepartments() != null)
                for (Department department : competentAuthorities.getDepartments())
                    validationHelper.validateReferential(department, false);

            validationHelper.validateString(competentAuthorities.getIban(), competentAuthorities.getHasAccount(), 40);
            validationHelper.validateString(competentAuthorities.getJssAccount(), false, 40);
            validationHelper.validateString(competentAuthorities.getContact(), false, 40);
            validationHelper.validateString(competentAuthorities.getMailRecipient(), false, 60);
            validationHelper.validateString(competentAuthorities.getAddress(), false, 60);
            validationHelper.validateString(competentAuthorities.getPostalCode(), false, 10);
            validationHelper.validateReferential(competentAuthorities.getCity(), false);
            validationHelper.validateReferential(competentAuthorities.getCountry(), false);

            outCompetentAuthority = competentAuthorityService
                    .addOrUpdateCompetentAuthority(competentAuthorities);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching competentAuthority", e);
            return new ResponseEntity<CompetentAuthority>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching competentAuthority", e);
            return new ResponseEntity<CompetentAuthority>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CompetentAuthority>(outCompetentAuthority, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/weekdays")
    public ResponseEntity<List<WeekDay>> getWeekDays() {
        List<WeekDay> weekDays = null;
        try {
            weekDays = weekDayService.getWeekDays();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching weekDay", e);
            return new ResponseEntity<List<WeekDay>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching weekDay", e);
            return new ResponseEntity<List<WeekDay>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<WeekDay>>(weekDays, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/weekday")
    public ResponseEntity<WeekDay> addOrUpdateWeekDay(
            @RequestBody WeekDay weekDays) {
        WeekDay outWeekDay;
        try {
            if (weekDays.getId() != null)
                validationHelper.validateReferential(weekDays, true);
            validationHelper.validateString(weekDays.getCode(), true, 20);
            validationHelper.validateString(weekDays.getLabel(), true, 100);

            outWeekDay = weekDayService
                    .addOrUpdateWeekDay(weekDays);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching weekDay", e);
            return new ResponseEntity<WeekDay>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching weekDay", e);
            return new ResponseEntity<WeekDay>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<WeekDay>(outWeekDay, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/legal-forms")
    public ResponseEntity<List<LegalForm>> getLegalForms() {
        List<LegalForm> legalForms = null;
        try {
            legalForms = legalFormService.getLegalForms();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching legalForm", e);
            return new ResponseEntity<List<LegalForm>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching legalForm", e);
            return new ResponseEntity<List<LegalForm>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<LegalForm>>(legalForms, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/legal-form")
    public ResponseEntity<LegalForm> addOrUpdateLegalForm(
            @RequestBody LegalForm legalForms) {
        LegalForm outLegalForm;
        try {
            if (legalForms.getId() != null)
                validationHelper.validateReferential(legalForms, true);
            validationHelper.validateString(legalForms.getCode(), true, 20);
            validationHelper.validateString(legalForms.getLabel(), true, 100);
            validationHelper.validateString(legalForms.getDescription(), false, 400);

            outLegalForm = legalFormService
                    .addOrUpdateLegalForm(legalForms);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching legalForm", e);
            return new ResponseEntity<LegalForm>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching legalForm", e);
            return new ResponseEntity<LegalForm>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<LegalForm>(outLegalForm, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/document-types")
    public ResponseEntity<List<DocumentType>> getDocumentTypes() {
        List<DocumentType> documentTypes = null;
        try {
            documentTypes = documentTypeService.getDocumentTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching documentTypes", e);
            return new ResponseEntity<List<DocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching documentTypes", e);
            return new ResponseEntity<List<DocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DocumentType>>(documentTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/document-type")
    public ResponseEntity<DocumentType> addOrUpdateDocumentType(
            @RequestBody DocumentType documentTypes) {
        DocumentType outDocumentType;
        try {
            if (documentTypes.getId() != null)
                validationHelper.validateReferential(documentTypes, true);
            validationHelper.validateString(documentTypes.getCode(), true, 20);
            validationHelper.validateString(documentTypes.getLabel(), true, 100);

            outDocumentType = documentTypeService
                    .addOrUpdateDocumentType(documentTypes);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching documentType", e);
            return new ResponseEntity<DocumentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching documentType", e);
            return new ResponseEntity<DocumentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<DocumentType>(outDocumentType, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment-types")
    public ResponseEntity<List<AttachmentType>> getAttachmentTypes() {
        List<AttachmentType> attachmentTypes = null;
        try {
            attachmentTypes = attachmentTypeService.getAttachmentTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching attachmentType", e);
            return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching attachmentType", e);
            return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AttachmentType>>(attachmentTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment-type")
    public ResponseEntity<AttachmentType> addOrUpdateAttachmentType(
            @RequestBody AttachmentType attachmentTypes) {
        AttachmentType outAttachmentType;
        try {
            if (attachmentTypes.getId() != null)
                validationHelper.validateReferential(attachmentTypes, true);
            validationHelper.validateString(attachmentTypes.getCode(), true, 20);
            validationHelper.validateString(attachmentTypes.getLabel(), true, 100);
            validationHelper.validateString(attachmentTypes.getDescription(), false, 400);

            outAttachmentType = attachmentTypeService
                    .addOrUpdateAttachmentType(attachmentTypes);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching attachmentType", e);
            return new ResponseEntity<AttachmentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching attachmentType", e);
            return new ResponseEntity<AttachmentType>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<AttachmentType>(outAttachmentType, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment/disabled")
    public ResponseEntity<Boolean> disableDocument(@RequestParam Integer idAttachment) {
        if (idAttachment == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Attachment attachment = attachmentService.getAttachment(idAttachment);
        if (attachment == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            attachmentService.disableDocument(attachment);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching documentTypes", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching documentTypes", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment/upload")
    public ResponseEntity<List<Attachment>> uploadAttachment(@RequestParam MultipartFile file,
            @RequestParam Integer idEntity, @RequestParam String entityType,
            @RequestParam Integer idAttachmentType,
            @RequestParam String filename, @RequestParam Boolean replaceExistingAttachementType) {
        try {
            if (idAttachmentType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

            if (attachmentType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (filename == null || filename.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (idEntity == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (entityType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (!entityType.equals(Tiers.class.getSimpleName())
                    && !entityType.equals(Responsable.class.getSimpleName())
                    && !entityType.equals(Quotation.class.getSimpleName())
                    && !entityType.equals(Announcement.class.getSimpleName())
                    && !entityType.equals(Domiciliation.class.getSimpleName())
                    && !entityType.equals(Bodacc.class.getSimpleName()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            return new ResponseEntity<List<Attachment>>(
                    attachmentService.addAttachment(file, idEntity, entityType, attachmentType, filename,
                            replaceExistingAttachementType),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Could not upload the file: " + file.getOriginalFilename() + "!", e);
            return new ResponseEntity<List<Attachment>>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(inputEntryPoint + "/attachment/download")
    public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment) {
        byte[] data = null;
        HttpHeaders headers = null;
        try {
            Attachment tiersAttachment = attachmentService.getAttachment(idAttachment);

            if (tiersAttachment == null || tiersAttachment.getUploadedFile() == null
                    || tiersAttachment.getUploadedFile().getPath() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            File file = new File(tiersAttachment.getUploadedFile().getPath());

            if (file != null) {
                data = Files.readAllBytes(file.toPath());

                headers = new HttpHeaders();
                headers.add("filename", tiersAttachment.getUploadedFile().getFilename());
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);

                // Compute content type
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null)
                    mimeType = "application/octet-stream";
                headers.set("content-type", mimeType);

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/all")
    @Transactional
    public ResponseEntity<Boolean> reindexAll() {
        try {
            invoiceService.reindexInvoices();
            tiersService.reindexTiers();
            responsableService.reindexResponsable();
            quotationService.reindexQuotation();
            customerOrderService.reindexCustomerOrder();
            assoAffaireOrderService.reindexAffaires();
            affaireService.reindexAffaire();

        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching attachmentType", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching attachmentType", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}