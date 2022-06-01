package com.jss.jssbackend.libs;

import java.util.ArrayList;
import java.util.List;

import com.jss.jssbackend.libs.audit.repository.AuditRepository;
import com.jss.jssbackend.libs.search.repository.IndexEntityRepository;
import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;
import com.jss.jssbackend.modules.miscellaneous.model.BillingItem;
import com.jss.jssbackend.modules.miscellaneous.model.BillingType;
import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.model.DocumentType;
import com.jss.jssbackend.modules.miscellaneous.model.Gift;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.model.LegalForm;
import com.jss.jssbackend.modules.miscellaneous.model.PaymentType;
import com.jss.jssbackend.modules.miscellaneous.model.Region;
import com.jss.jssbackend.modules.miscellaneous.model.SpecialOffer;
import com.jss.jssbackend.modules.miscellaneous.model.Vat;
import com.jss.jssbackend.modules.miscellaneous.repository.AttachmentTypeRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.BillingItemRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.BillingTypeRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.CityRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.CivilityRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.CountryRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.DeliveryServiceRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.DepartmentRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.DocumentRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.DocumentTypeRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.GiftRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.LanguageRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.LegalFormRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.PaymentTypeRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.RegionRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.SpecialOfferRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.UploadedFileRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.VatRepository;
import com.jss.jssbackend.modules.profile.model.Employee;
import com.jss.jssbackend.modules.profile.model.Team;
import com.jss.jssbackend.modules.profile.repository.EmployeeRepository;
import com.jss.jssbackend.modules.profile.repository.TeamRepository;
import com.jss.jssbackend.modules.quotation.model.QuotationLabelType;
import com.jss.jssbackend.modules.quotation.model.QuotationStatus;
import com.jss.jssbackend.modules.quotation.model.RecordType;
import com.jss.jssbackend.modules.quotation.repository.QuotationLabelTypeRepository;
import com.jss.jssbackend.modules.quotation.repository.QuotationRepository;
import com.jss.jssbackend.modules.quotation.repository.QuotationStatusRepository;
import com.jss.jssbackend.modules.quotation.repository.RecordTypeRepository;
import com.jss.jssbackend.modules.tiers.model.BillingClosureRecipientType;
import com.jss.jssbackend.modules.tiers.model.BillingClosureType;
import com.jss.jssbackend.modules.tiers.model.BillingLabelType;
import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.PaymentDeadlineType;
import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.model.RefundType;
import com.jss.jssbackend.modules.tiers.model.SubscriptionPeriodType;
import com.jss.jssbackend.modules.tiers.model.TiersCategory;
import com.jss.jssbackend.modules.tiers.model.TiersFollowupType;
import com.jss.jssbackend.modules.tiers.model.TiersType;
import com.jss.jssbackend.modules.tiers.repository.BillingClosureRecipientTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.BillingClosureTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.BillingLabelTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.JssSubscriptionRepository;
import com.jss.jssbackend.modules.tiers.repository.MailRepository;
import com.jss.jssbackend.modules.tiers.repository.PaymentDeadlineTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.PhoneRepository;
import com.jss.jssbackend.modules.tiers.repository.RefundTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.ResponsableRepository;
import com.jss.jssbackend.modules.tiers.repository.SubscriptionPeriodTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersCategoryRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersFollowupTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO : delete !!!
// TODO : à ajouter dans toutes les bases :
// ALTER TABLE index_entity ADD COLUMN ts_text tsvector GENERATED ALWAYS AS (to_tsvector('french', text)) STORED;
// CREATE INDEX idx_ts_text ON index_entity USING GIST (ts_text);  
//CREATE INDEX idx_text ON index_entity USING GIST (text gist_trgm_ops );  
// CREATE EXTENSION pg_trgm;
@RestController
@CrossOrigin
public class InitReferentialsController {

	private static final String inputEntryPoint = "/referentials";

	@Autowired
	TiersTypeRepository tiersTypeRepository;

	@Autowired
	CivilityRepository civilityRepository;

	@Autowired
	TiersCategoryRepository tiersCategoryRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	LanguageRepository languageRepository;

	@Autowired
	VatRepository vatRepository;

	@Autowired
	BillingItemRepository billingItemRepository;

	@Autowired
	BillingTypeRepository billingTypeRepository;

	@Autowired
	SpecialOfferRepository specialOfferRepository;

	@Autowired
	MailRepository mailRepository;

	@Autowired
	PhoneRepository phoneRepository;

	@Autowired
	DocumentTypeRepository tiersDocumentTypeRepository;

	@Autowired
	DocumentRepository tiersDocumentRepository;

	@Autowired
	TiersRepository tiersRepository;

	@Autowired
	DeliveryServiceRepository deliveryServiceRepository;

	@Autowired
	PaymentTypeRepository paymentTypeRepository;

	@Autowired
	BillingLabelTypeRepository billingLabelTypeRepository;

	@Autowired
	IndexEntityRepository indexEntityRepository;

	@Autowired
	PaymentDeadlineTypeRepository paymentDeadlineTypeRepository;

	@Autowired
	RefundTypeRepository refundTypeRepository;

	@Autowired
	BillingClosureTypeRepository billingClosureTypeRepository;

	@Autowired
	BillingClosureRecipientTypeRepository billingClosureRecipientTypeRepository;

	@Autowired
	AttachmentTypeRepository attachmentTypeRepository;

	@Autowired
	AuditRepository auditRepository;

	@Autowired
	UploadedFileRepository uploadedFileRepository;

	@Autowired
	TiersFollowupTypeRepository tiersFollowupTypeRepository;

	@Autowired
	GiftRepository giftRepository;

	@Autowired
	ResponsableRepository responsableRepository;

	@Autowired
	CityRepository cityRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	RegionRepository regionRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	JssSubscriptionRepository jssSubscriptionRepository;

	@Autowired
	SubscriptionPeriodTypeRepository subscriptionPeriodTypeRepository;

	@Autowired
	QuotationStatusRepository quotationStatusRepository;

	@Autowired
	QuotationRepository quotationRepository;

	@Autowired
	QuotationLabelTypeRepository quotationLabelTypeRepository;

	@Autowired
	RecordTypeRepository recordTypeRepository;

	@Autowired
	LegalFormRepository legalFormRepository;

	@GetMapping(inputEntryPoint + "/create")
	public void create() {
		tiersRepository.deleteAll();
		indexEntityRepository.deleteAll();
		auditRepository.deleteAll();
		uploadedFileRepository.deleteAll();
		responsableRepository.deleteAll();

		cityRepository.deleteAll();
		departmentRepository.deleteAll();
		regionRepository.deleteAll();
		countryRepository.deleteAll();

		Country country = new Country();
		country.setCode("FR");
		country.setLabel("France");
		countryRepository.save(country);

		Region region = new Region();
		region.setCode("IDF");
		region.setLabel("Ile de france");
		regionRepository.save(region);

		Department departement = new Department();
		departement.setCode("75");
		departement.setLabel("Paris");
		departmentRepository.save(departement);

		City city = new City();
		city.setCountry(country);
		city.setDepartment(departement);
		city.setLabel("Paris 13");
		city.setLocality("Paris 13");
		city.setPostalCode("75013");
		city.setValidated(true);
		cityRepository.save(city);

		deliveryServiceRepository.deleteAll();
		DeliveryService deliveryService = new DeliveryService();
		deliveryService.setLabel("La Poste");
		deliveryServiceRepository.save(deliveryService);

		tiersTypeRepository.deleteAll();
		TiersType tiersType = new TiersType();
		tiersType.setCode("CLIENT");
		tiersType.setLabel("Client");
		tiersTypeRepository.save(tiersType);

		tiersType = new TiersType();
		tiersType.setCode("PROSPECT");
		tiersType.setLabel("Prospect");
		tiersTypeRepository.save(tiersType);

		civilityRepository.deleteAll();
		Civility civility = new Civility();
		civility.setLabel("Maître");
		civilityRepository.save(civility);
		civility = new Civility();
		civility.setLabel("Monsieur");
		civilityRepository.save(civility);
		civility = new Civility();
		civility.setLabel("Madame");
		civilityRepository.save(civility);

		tiersCategoryRepository.deleteAll();
		TiersCategory tiersCategory = new TiersCategory();
		tiersCategory.setCode("AV");
		tiersCategory.setLabel("Avocat");
		tiersCategoryRepository.save(tiersCategory);
		tiersCategory = new TiersCategory();
		tiersCategory.setCode("AW");
		tiersCategory.setLabel("Architecte");
		tiersCategoryRepository.save(tiersCategory);

		List<Team> teams = IterableUtils.toList(teamRepository.findAll());
		for (Team t : teams) {
			t.setManager(null);
			teamRepository.save(t);
		}

		employeeRepository.deleteAll();
		teamRepository.deleteAll();
		Team team1 = new Team();
		team1.setCode("COMMERCIAL");
		team1.setLabel("Commerciale");
		team1.setMail("commercial@jss.fr");
		teamRepository.save(team1);

		Employee employee1 = new Employee();
		employee1.setFirstname("John");
		employee1.setLastname("Doe");
		employee1.setTeam(team1);
		employeeRepository.save(employee1);

		team1.setManager(employee1);
		teamRepository.save(team1);

		Team team2 = new Team();
		team2.setCode("FORMALISTE");
		team2.setLabel("Formaliste");
		team2.setMail("formaliste@jss.fr");
		team2.setManager(employee1);
		teamRepository.save(team2);

		Employee employee2 = new Employee();
		employee2.setFirstname("Johanna");
		employee2.setLastname("Doe");
		employee2.setTeam(team2);
		employeeRepository.save(employee2);

		Team team3 = new Team();
		team3.setCode("INSERTIONS");
		team3.setLabel("Insertions");
		team3.setMail("insertion@jss.fr");
		team3.setManager(employee1);
		teamRepository.save(team3);

		Employee employee3 = new Employee();
		employee3.setFirstname("Emile");
		employee3.setLastname("Doe");
		employee3.setTeam(team3);
		employeeRepository.save(employee3);

		languageRepository.deleteAll();
		Language language = new Language();
		language.setLabel("Français");
		languageRepository.save(language);

		language = new Language();
		language.setLabel("Anglais");
		languageRepository.save(language);

		specialOfferRepository.deleteAll();
		billingItemRepository.deleteAll();
		billingTypeRepository.deleteAll();
		vatRepository.deleteAll();
		Vat vat = new Vat();
		vat.setLabel("Non taxable");
		vat.setRate(0f);
		vatRepository.save(vat);

		vat = new Vat();
		vat.setLabel("20.0");
		vat.setRate(20f);
		vatRepository.save(vat);

		BillingType billingType = new BillingType();
		billingType.setLabel("Poste 1");
		billingType.setPreTaxPrice(10f);
		billingType.setAccountingCode(10);
		billingTypeRepository.save(billingType);

		BillingItem billingItem = new BillingItem();
		billingItem.setBillingType(billingType);
		billingItem.setDiscountAmount(0f);
		billingItem.setDiscountRate(0f);
		billingItem.setVat(vat);
		billingItemRepository.save(billingItem);

		SpecialOffer specialOffer = new SpecialOffer();
		specialOffer.setCode("10");
		specialOffer.setLabel("Non taxable");
		ArrayList<BillingItem> billingItems = new ArrayList<BillingItem>();
		billingItems.add(billingItem);
		specialOffer.setBillingItems(billingItems);
		specialOfferRepository.save(specialOffer);

		mailRepository.deleteAll();
		Mail mail = new Mail();
		mail.setMail("aa@aa.com");
		mailRepository.save(mail);

		phoneRepository.deleteAll();
		Phone phone = new Phone();
		phone.setPhoneNumber("0123456789");
		phoneRepository.save(phone);

		phone = new Phone();
		phone.setPhoneNumber("+33123465789");
		phoneRepository.save(phone);

		tiersDocumentRepository.deleteAll();
		tiersDocumentTypeRepository.deleteAll();
		DocumentType tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("1");
		tiersDocumentType.setLabel("JUSTIFICATIF PARUTION");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("2");
		tiersDocumentType.setLabel("CFE");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("3");
		tiersDocumentType.setLabel("KBIS");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("4");
		tiersDocumentType.setLabel("FACTURE");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("5");
		tiersDocumentType.setLabel("RELANCE");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("6");
		tiersDocumentType.setLabel("REMBOURSEMENT");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("7");
		tiersDocumentType.setLabel("ARRETES COMPTABLES");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("8");
		tiersDocumentType.setLabel("RECU DE PROVISION");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("9");
		tiersDocumentType.setLabel("DIVERS");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		tiersDocumentType = new DocumentType();
		tiersDocumentType.setCode("10");
		tiersDocumentType.setLabel("DEVIS");
		tiersDocumentTypeRepository.save(tiersDocumentType);

		paymentTypeRepository.deleteAll();
		PaymentType paymentType = new PaymentType();
		paymentType.setCode("PRELEVEMENT");
		paymentType.setLabel("Prélèvement");
		paymentTypeRepository.save(paymentType);
		paymentType = new PaymentType();
		paymentType.setCode("CHEQUES");
		paymentType.setLabel("Chèques");
		paymentTypeRepository.save(paymentType);

		billingLabelTypeRepository.deleteAll();
		BillingLabelType billingLabelType = new BillingLabelType();
		billingLabelType.setCode("AFFAIRE");
		billingLabelType.setLabel("Affaire");
		billingLabelTypeRepository.save(billingLabelType);

		billingLabelType = new BillingLabelType();
		billingLabelType.setCode("CLIENT");
		billingLabelType.setLabel("Client");
		billingLabelTypeRepository.save(billingLabelType);

		billingLabelType = new BillingLabelType();
		billingLabelType.setCode("AUTRES");
		billingLabelType.setLabel("Autres");
		billingLabelTypeRepository.save(billingLabelType);

		paymentDeadlineTypeRepository.deleteAll();
		PaymentDeadlineType paymentDeadlineType = new PaymentDeadlineType();
		paymentDeadlineType.setCode("IMMEDIAT");
		paymentDeadlineType.setLabel("Immédiat");
		paymentDeadlineTypeRepository.save(paymentDeadlineType);

		paymentDeadlineType = new PaymentDeadlineType();
		paymentDeadlineType.setCode("30");
		paymentDeadlineType.setLabel("30");
		paymentDeadlineTypeRepository.save(paymentDeadlineType);

		paymentDeadlineType = new PaymentDeadlineType();
		paymentDeadlineType.setCode("45");
		paymentDeadlineType.setLabel("45");
		paymentDeadlineTypeRepository.save(paymentDeadlineType);

		paymentDeadlineType = new PaymentDeadlineType();
		paymentDeadlineType.setCode("60");
		paymentDeadlineType.setLabel("60");
		paymentDeadlineTypeRepository.save(paymentDeadlineType);

		refundTypeRepository.deleteAll();
		RefundType refundType = new RefundType();
		refundType.setCode("VIREMENT");
		refundType.setLabel("Virement");
		refundTypeRepository.save(refundType);

		refundType = new RefundType();
		refundType.setCode("CHEQUE");
		refundType.setLabel("Chèque");
		refundTypeRepository.save(refundType);

		billingClosureTypeRepository.deleteAll();
		BillingClosureType billingClosureType = new BillingClosureType();
		billingClosureType.setCode("AFFAIRE");
		billingClosureType.setLabel("Par affaire");
		billingClosureTypeRepository.save(billingClosureType);

		billingClosureType = new BillingClosureType();
		billingClosureType.setCode("COMPTABLE");
		billingClosureType.setLabel("Comptable");
		billingClosureTypeRepository.save(billingClosureType);

		billingClosureRecipientTypeRepository.deleteAll();
		BillingClosureRecipientType billingClosureRecipientType = new BillingClosureRecipientType();
		billingClosureRecipientType.setCode("REPONSABLE");
		billingClosureRecipientType.setLabel("Reponsable");
		billingClosureRecipientTypeRepository.save(billingClosureRecipientType);

		billingClosureRecipientType = new BillingClosureRecipientType();
		billingClosureRecipientType.setCode("TOUS");
		billingClosureRecipientType.setLabel("Tous");
		billingClosureRecipientTypeRepository.save(billingClosureRecipientType);

		attachmentTypeRepository.deleteAll();
		AttachmentType attachmentType = new AttachmentType();
		attachmentType.setCode("1");
		attachmentType.setLabel("Document client");
		attachmentTypeRepository.save(attachmentType);

		attachmentType = new AttachmentType();
		attachmentType.setCode("2");
		attachmentType.setLabel("Devis");
		attachmentTypeRepository.save(attachmentType);

		attachmentType = new AttachmentType();
		attachmentType.setCode("3");
		attachmentType.setLabel("Contrat");
		attachmentTypeRepository.save(attachmentType);

		attachmentType = new AttachmentType();
		attachmentType.setCode("4");
		attachmentType.setLabel("Commande");
		attachmentTypeRepository.save(attachmentType);

		attachmentType = new AttachmentType();
		attachmentType.setCode("5");
		attachmentType.setLabel("Facture");
		attachmentTypeRepository.save(attachmentType);

		tiersFollowupTypeRepository.deleteAll();
		TiersFollowupType tiersFollowupType = new TiersFollowupType();
		tiersFollowupType.setCode("VISITE");
		tiersFollowupType.setLabel("Visite");
		tiersFollowupTypeRepository.save(tiersFollowupType);

		tiersFollowupType = new TiersFollowupType();
		tiersFollowupType.setCode("ENVOI_CADEAU");
		tiersFollowupType.setLabel("Envoi d'un cadeau");
		tiersFollowupTypeRepository.save(tiersFollowupType);

		tiersFollowupType = new TiersFollowupType();
		tiersFollowupType.setCode("APPEL");
		tiersFollowupType.setLabel("Appel");
		tiersFollowupTypeRepository.save(tiersFollowupType);

		giftRepository.deleteAll();
		Gift gift = new Gift();
		gift.setCode("CHAMPAGNE");
		gift.setLabel("Champagne");
		giftRepository.save(gift);

		gift = new Gift();
		gift.setCode("FOIE GRAS");
		gift.setLabel("Foie gras");
		giftRepository.save(gift);

		jssSubscriptionRepository.deleteAll();

		subscriptionPeriodTypeRepository.deleteAll();
		SubscriptionPeriodType subscriptionPeriodType = new SubscriptionPeriodType();
		subscriptionPeriodType.setCode("3");
		subscriptionPeriodType.setLabel("3 mois");
		subscriptionPeriodTypeRepository.save(subscriptionPeriodType);

		subscriptionPeriodType = new SubscriptionPeriodType();
		subscriptionPeriodType.setCode("6");
		subscriptionPeriodType.setLabel("6 mois");
		subscriptionPeriodTypeRepository.save(subscriptionPeriodType);

		subscriptionPeriodType = new SubscriptionPeriodType();
		subscriptionPeriodType.setCode("12");
		subscriptionPeriodType.setLabel("12 mois");
		subscriptionPeriodTypeRepository.save(subscriptionPeriodType);

		quotationRepository.deleteAll();
		quotationStatusRepository.deleteAll();

		QuotationStatus quotationStatus = new QuotationStatus();
		quotationStatus.setCode("OUVERT");
		quotationStatus.setLabel("Ouvert");
		quotationStatusRepository.save(quotationStatus);

		quotationStatus = new QuotationStatus();
		quotationStatus.setCode("EN_COURS");
		quotationStatus.setLabel("En cours");
		quotationStatusRepository.save(quotationStatus);

		quotationLabelTypeRepository.deleteAll();
		QuotationLabelType quotationLabelType = new QuotationLabelType();
		quotationLabelType.setCode("AFFAIRE");
		quotationLabelType.setLabel("Affaire");
		quotationLabelTypeRepository.save(quotationLabelType);

		quotationLabelType = new QuotationLabelType();
		quotationLabelType.setCode("CLIENT");
		quotationLabelType.setLabel("Client");
		quotationLabelTypeRepository.save(quotationLabelType);

		quotationLabelType = new QuotationLabelType();
		quotationLabelType.setCode("AUTRES");
		quotationLabelType.setLabel("Autres");
		quotationLabelTypeRepository.save(quotationLabelType);

		recordTypeRepository.deleteAll();
		RecordType recordType = new RecordType();
		recordType.setCode("DEMATERIALISE");
		recordType.setLabel("Dématérialisé");
		recordTypeRepository.save(recordType);

		recordType = new RecordType();
		recordType.setCode("PAPIER");
		recordType.setLabel("PAPIER");
		recordTypeRepository.save(recordType);

		legalFormRepository.deleteAll();
		LegalForm legalForm = new LegalForm();
		legalForm.setCode("EARL");
		legalForm.setLabel("EARL");
		legalFormRepository.save(legalForm);

		legalForm = new LegalForm();
		legalForm.setCode("SARL");
		legalForm.setLabel("SARL");
		legalFormRepository.save(legalForm);

		legalForm = new LegalForm();
		legalForm.setCode("32");
		legalForm.setLabel("Société non immatriculée");
		legalFormRepository.save(legalForm);
	}
}
