package com.jss.osiris.libs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.audit.repository.AuditRepository;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.accounting.repository.AccountingAccountClassRepository;
import com.jss.osiris.modules.invoicing.repository.InvoiceStatusRepository;
import com.jss.osiris.modules.miscellaneous.repository.AttachmentTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.BillingItemRepository;
import com.jss.osiris.modules.miscellaneous.repository.BillingTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.CityRepository;
import com.jss.osiris.modules.miscellaneous.repository.CivilityRepository;
import com.jss.osiris.modules.miscellaneous.repository.CompetentAuthorityRepository;
import com.jss.osiris.modules.miscellaneous.repository.CompetentAuthorityTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.CountryRepository;
import com.jss.osiris.modules.miscellaneous.repository.DeliveryServiceRepository;
import com.jss.osiris.modules.miscellaneous.repository.DepartmentRepository;
import com.jss.osiris.modules.miscellaneous.repository.DocumentRepository;
import com.jss.osiris.modules.miscellaneous.repository.DocumentTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.GiftRepository;
import com.jss.osiris.modules.miscellaneous.repository.LanguageRepository;
import com.jss.osiris.modules.miscellaneous.repository.LegalFormRepository;
import com.jss.osiris.modules.miscellaneous.repository.MailRepository;
import com.jss.osiris.modules.miscellaneous.repository.PaymentTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.PhoneRepository;
import com.jss.osiris.modules.miscellaneous.repository.RegionRepository;
import com.jss.osiris.modules.miscellaneous.repository.SpecialOfferRepository;
import com.jss.osiris.modules.miscellaneous.repository.UploadedFileRepository;
import com.jss.osiris.modules.miscellaneous.repository.VatCollectionTypeRepository;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;
import com.jss.osiris.modules.miscellaneous.repository.WeekDayRepository;
import com.jss.osiris.modules.profile.repository.EmployeeRepository;
import com.jss.osiris.modules.quotation.repository.ActTypeRepository;
import com.jss.osiris.modules.quotation.repository.AnnouncementNoticeTemplateRepository;
import com.jss.osiris.modules.quotation.repository.BodaccPublicationTypeRepository;
import com.jss.osiris.modules.quotation.repository.BuildingDomiciliationRepository;
import com.jss.osiris.modules.quotation.repository.CharacterPriceRepository;
import com.jss.osiris.modules.quotation.repository.ConfrereRepository;
import com.jss.osiris.modules.quotation.repository.DomiciliationContractTypeRepository;
import com.jss.osiris.modules.quotation.repository.FundTypeRepository;
import com.jss.osiris.modules.quotation.repository.JournalTypeRepository;
import com.jss.osiris.modules.quotation.repository.MailRedirectionTypeRepository;
import com.jss.osiris.modules.quotation.repository.NoticeTypeFamilyRepository;
import com.jss.osiris.modules.quotation.repository.NoticeTypeRepository;
import com.jss.osiris.modules.quotation.repository.ProvisionFamilyTypeRepository;
import com.jss.osiris.modules.quotation.repository.ProvisionTypeRepository;
import com.jss.osiris.modules.quotation.repository.QuotationRepository;
import com.jss.osiris.modules.quotation.repository.QuotationStatusRepository;
import com.jss.osiris.modules.quotation.repository.RecordTypeRepository;
import com.jss.osiris.modules.quotation.repository.TransfertFundsTypeRepository;
import com.jss.osiris.modules.tiers.repository.BillingClosureRecipientTypeRepository;
import com.jss.osiris.modules.tiers.repository.BillingClosureTypeRepository;
import com.jss.osiris.modules.tiers.repository.BillingLabelTypeRepository;
import com.jss.osiris.modules.tiers.repository.JssSubscriptionRepository;
import com.jss.osiris.modules.tiers.repository.PaymentDeadlineTypeRepository;
import com.jss.osiris.modules.tiers.repository.RefundTypeRepository;
import com.jss.osiris.modules.tiers.repository.ResponsableRepository;
import com.jss.osiris.modules.tiers.repository.SubscriptionPeriodTypeRepository;
import com.jss.osiris.modules.tiers.repository.TiersCategoryRepository;
import com.jss.osiris.modules.tiers.repository.TiersFollowupTypeRepository;
import com.jss.osiris.modules.tiers.repository.TiersRepository;
import com.jss.osiris.modules.tiers.repository.TiersTypeRepository;

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
	RecordTypeRepository recordTypeRepository;

	@Autowired
	LegalFormRepository legalFormRepository;

	@Autowired
	ProvisionFamilyTypeRepository provisionFamilyTypeRepository;

	@Autowired
	ProvisionTypeRepository provisionTypeRepository;

	@Autowired
	DomiciliationContractTypeRepository domiciliationContractTypeRepository;

	@Autowired
	BuildingDomiciliationRepository buildingDomiciliationRepository;

	@Autowired
	MailRedirectionTypeRepository mailRedirectionTypeRepository;

	@Autowired
	WeekDayRepository weekDayRepository;

	@Autowired
	ConfrereRepository confrereRepository;

	@Autowired
	JournalTypeRepository journalTypeRepository;

	@Autowired
	CharacterPriceRepository characterPriceRepository;

	@Autowired
	NoticeTypeRepository noticeTypeRepository;

	@Autowired
	NoticeTypeFamilyRepository noticeTypeFamilyRepository;

	@Autowired
	BodaccPublicationTypeRepository bodaccPublicationTypeRepository;

	@Autowired
	TransfertFundsTypeRepository transfertFundsTypeRepository;

	@Autowired
	CompetentAuthorityRepository competentAuthorityRepository;

	@Autowired
	CompetentAuthorityTypeRepository competentAuthorityTypeRepository;

	@Autowired
	FundTypeRepository fundTypeRepository;

	@Autowired
	ActTypeRepository actTypeRepository;

	@Autowired
	AnnouncementNoticeTemplateRepository AnnouncementNoticeTemplateRepository;

	@Autowired
	AccountingAccountClassRepository accountingAccountClassRepository;

	@Autowired
	VatCollectionTypeRepository vatCollectionTypeRepository;

	@Autowired
	InvoiceStatusRepository invoiceStatusRepository;

	@GetMapping(inputEntryPoint + "/create")
	public void create() {
		/*
		 * tiersRepository.deleteAll();
		 * indexEntityRepository.deleteAll();
		 * auditRepository.deleteAll();
		 * uploadedFileRepository.deleteAll();
		 * responsableRepository.deleteAll();
		 * 
		 * cityRepository.deleteAll();
		 * departmentRepository.deleteAll();
		 * regionRepository.deleteAll();
		 * countryRepository.deleteAll();
		 * 
		 * Country country = new Country();
		 * country.setCode("FR");
		 * country.setLabel("France");
		 * countryRepository.save(country);
		 * 
		 * Region region = new Region();
		 * region.setCode("IDF");
		 * region.setLabel("Ile de france");
		 * regionRepository.save(region);
		 * 
		 * Department departement = new Department();
		 * departement.setCode("75");
		 * departement.setLabel("Paris");
		 * departmentRepository.save(departement);
		 * 
		 * City city = new City();
		 * city.setCountry(country);
		 * city.setDepartment(departement);
		 * city.setLabel("Paris 13");
		 * city.setLocality("Paris 13");
		 * city.setPostalCode("75013");
		 * city.setCode("75013");
		 * cityRepository.save(city);
		 * 
		 * deliveryServiceRepository.deleteAll();
		 * DeliveryService deliveryService = new DeliveryService();
		 * deliveryService.setLabel("La Poste");
		 * deliveryService.setCode("La Poste");
		 * deliveryServiceRepository.save(deliveryService);
		 * 
		 * tiersTypeRepository.deleteAll();
		 * TiersType tiersType = new TiersType();
		 * tiersType.setCode("CLIENT");
		 * tiersType.setLabel("Client");
		 * tiersTypeRepository.save(tiersType);
		 * 
		 * tiersType = new TiersType();
		 * tiersType.setCode("PROSPECT");
		 * tiersType.setLabel("Prospect");
		 * tiersTypeRepository.save(tiersType);
		 * 
		 * civilityRepository.deleteAll();
		 * Civility civility = new Civility();
		 * civility.setLabel("Maître");
		 * civility.setCode("Maître");
		 * civilityRepository.save(civility);
		 * civility = new Civility();
		 * civility.setLabel("Monsieur");
		 * civility.setCode("Monsieur");
		 * civilityRepository.save(civility);
		 * civility = new Civility();
		 * civility.setLabel("Madame");
		 * civility.setCode("Madame");
		 * civilityRepository.save(civility);
		 * 
		 * tiersCategoryRepository.deleteAll();
		 * TiersCategory tiersCategory = new TiersCategory();
		 * tiersCategory.setCode("AV");
		 * tiersCategory.setLabel("Avocat");
		 * tiersCategoryRepository.save(tiersCategory);
		 * tiersCategory = new TiersCategory();
		 * tiersCategory.setCode("AW");
		 * tiersCategory.setLabel("Architecte");
		 * tiersCategoryRepository.save(tiersCategory);
		 * 
		 * List<Team> teams = IterableUtils.toList(teamRepository.findAll());
		 * for (Team t : teams) {
		 * t.setManager(null);
		 * teamRepository.save(t);
		 * }
		 * 
		 * employeeRepository.deleteAll();
		 * teamRepository.deleteAll();
		 * Team team1 = new Team();
		 * team1.setCode("COMMERCIAL");
		 * team1.setLabel("Commerciale");
		 * teamRepository.save(team1);
		 * 
		 * Employee employee1 = new Employee();
		 * employee1.setFirstname("John");
		 * employee1.setLastname("Doe");
		 * employee1.setTeam(team1);
		 * employeeRepository.save(employee1);
		 * 
		 * team1.setManager(employee1);
		 * teamRepository.save(team1);
		 * 
		 * Team team2 = new Team();
		 * team2.setCode("FORMALISTE");
		 * team2.setLabel("Formaliste");
		 * team2.setManager(employee1);
		 * teamRepository.save(team2);
		 * 
		 * Employee employee2 = new Employee();
		 * employee2.setFirstname("Johanna");
		 * employee2.setLastname("Doe");
		 * employee2.setTeam(team2);
		 * employeeRepository.save(employee2);
		 * 
		 * Team team3 = new Team();
		 * team3.setCode("INSERTIONS");
		 * team3.setLabel("Insertions");
		 * team3.setManager(employee1);
		 * teamRepository.save(team3);
		 * 
		 * Employee employee3 = new Employee();
		 * employee3.setFirstname("Emile");
		 * employee3.setLastname("Doe");
		 * employee3.setTeam(team3);
		 * employeeRepository.save(employee3);
		 * 
		 * languageRepository.deleteAll();
		 * Language language = new Language();
		 * language.setLabel("Français");
		 * language.setCode("FR");
		 * languageRepository.save(language);
		 * 
		 * language = new Language();
		 * language.setLabel("Anglais");
		 * language.setCode("ENG");
		 * languageRepository.save(language);
		 * 
		 * specialOfferRepository.deleteAll();
		 * billingItemRepository.deleteAll();
		 * billingTypeRepository.deleteAll();
		 * vatRepository.deleteAll();
		 * Vat vat = new Vat();
		 * vat.setLabel("Non taxable");
		 * vat.setRate(0f);
		 * vatRepository.save(vat);
		 * 
		 * vat = new Vat();
		 * vat.setLabel("20.0");
		 * vat.setRate(20f);
		 * vatRepository.save(vat);
		 * 
		 * BillingType billingType = new BillingType();
		 * billingType.setLabel("Poste 1");
		 * billingType.setCode("Poste 1");
		 * billingType.setCanOverridePrice(false);
		 * billingType.setIsPriceBasedOnCharacterNumber(false);
		 * billingTypeRepository.save(billingType);
		 * 
		 * BillingItem billingItem = new BillingItem();
		 * billingItem.setBillingType(billingType);
		 * billingItem.setPreTaxPrice(10f);
		 * billingItem.setStartDate(LocalDate.now());
		 * billingItemRepository.save(billingItem);
		 * 
		 * SpecialOffer specialOffer = new SpecialOffer();
		 * specialOffer.setCode("10");
		 * specialOffer.setLabel("Non taxable");
		 * specialOfferRepository.save(specialOffer);
		 * 
		 * mailRepository.deleteAll();
		 * Mail mail = new Mail();
		 * mail.setMail("aa@aa.com");
		 * mailRepository.save(mail);
		 * 
		 * phoneRepository.deleteAll();
		 * Phone phone = new Phone();
		 * phone.setPhoneNumber("0123456789");
		 * phoneRepository.save(phone);
		 * 
		 * phone = new Phone();
		 * phone.setPhoneNumber("+33123465789");
		 * phoneRepository.save(phone);
		 * 
		 * tiersDocumentRepository.deleteAll();
		 * tiersDocumentTypeRepository.deleteAll();
		 * DocumentType tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("1");
		 * tiersDocumentType.setLabel("JUSTIFICATIF PARUTION");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("2");
		 * tiersDocumentType.setLabel("CFE");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("3");
		 * tiersDocumentType.setLabel("KBIS");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("4");
		 * tiersDocumentType.setLabel("FACTURE");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("5");
		 * tiersDocumentType.setLabel("RELANCE");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("6");
		 * tiersDocumentType.setLabel("REMBOURSEMENT");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("7");
		 * tiersDocumentType.setLabel("ARRETES COMPTABLES");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("8");
		 * tiersDocumentType.setLabel("RECU DE PROVISION");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("9");
		 * tiersDocumentType.setLabel("DIVERS");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("10");
		 * tiersDocumentType.setLabel("DEVIS");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("12");
		 * tiersDocumentType.setLabel("EPREUVE DE RELECTURE");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * tiersDocumentType = new DocumentType();
		 * tiersDocumentType.setCode("13");
		 * tiersDocumentType.setLabel("ATTESTATION DE PURATION");
		 * tiersDocumentTypeRepository.save(tiersDocumentType);
		 * 
		 * paymentTypeRepository.deleteAll();
		 * PaymentType paymentType = new PaymentType();
		 * paymentType.setCode("PRELEVEMENT");
		 * paymentType.setLabel("Prélèvement");
		 * paymentTypeRepository.save(paymentType);
		 * paymentType = new PaymentType();
		 * paymentType.setCode("CHEQUES");
		 * paymentType.setLabel("Chèques");
		 * paymentTypeRepository.save(paymentType);
		 * 
		 * paymentType = new PaymentType();
		 * paymentType.setCode("COMPTE_INPI");
		 * paymentType.setLabel("Compte INPI");
		 * paymentTypeRepository.save(paymentType);
		 * paymentType = new PaymentType();
		 * paymentType.setCode("MISE_EN_COMPTE");
		 * paymentType.setLabel("Mise en compte");
		 * paymentTypeRepository.save(paymentType);
		 * paymentType = new PaymentType();
		 * paymentType.setCode("VIREMENT");
		 * paymentType.setLabel("Virement");
		 * paymentTypeRepository.save(paymentType);
		 * 
		 * paymentType = new PaymentType();
		 * paymentType.setCode("AUTRES");
		 * paymentType.setLabel("Autres");
		 * paymentTypeRepository.save(paymentType);
		 * 
		 * billingLabelTypeRepository.deleteAll();
		 * BillingLabelType billingLabelType = new BillingLabelType();
		 * billingLabelType.setCode("AFFAIRE");
		 * billingLabelType.setLabel("Affaire");
		 * billingLabelTypeRepository.save(billingLabelType);
		 * 
		 * billingLabelType = new BillingLabelType();
		 * billingLabelType.setCode("CLIENT");
		 * billingLabelType.setLabel("Client");
		 * billingLabelTypeRepository.save(billingLabelType);
		 * 
		 * billingLabelType = new BillingLabelType();
		 * billingLabelType.setCode("AUTRES");
		 * billingLabelType.setLabel("Autres");
		 * billingLabelTypeRepository.save(billingLabelType);
		 * 
		 * paymentDeadlineTypeRepository.deleteAll();
		 * PaymentDeadlineType paymentDeadlineType = new PaymentDeadlineType();
		 * paymentDeadlineType.setCode("IMMEDIAT");
		 * paymentDeadlineType.setLabel("Immédiat");
		 * paymentDeadlineTypeRepository.save(paymentDeadlineType);
		 * 
		 * paymentDeadlineType = new PaymentDeadlineType();
		 * paymentDeadlineType.setCode("30");
		 * paymentDeadlineType.setLabel("30");
		 * paymentDeadlineTypeRepository.save(paymentDeadlineType);
		 * 
		 * paymentDeadlineType = new PaymentDeadlineType();
		 * paymentDeadlineType.setCode("45");
		 * paymentDeadlineType.setLabel("45");
		 * paymentDeadlineTypeRepository.save(paymentDeadlineType);
		 * 
		 * paymentDeadlineType = new PaymentDeadlineType();
		 * paymentDeadlineType.setCode("60");
		 * paymentDeadlineType.setLabel("60");
		 * paymentDeadlineTypeRepository.save(paymentDeadlineType);
		 * 
		 * refundTypeRepository.deleteAll();
		 * RefundType refundType = new RefundType();
		 * refundType.setCode("VIREMENT");
		 * refundType.setLabel("Virement");
		 * refundTypeRepository.save(refundType);
		 * 
		 * refundType = new RefundType();
		 * refundType.setCode("CHEQUE");
		 * refundType.setLabel("Chèque");
		 * refundTypeRepository.save(refundType);
		 * 
		 * billingClosureTypeRepository.deleteAll();
		 * BillingClosureType billingClosureType = new BillingClosureType();
		 * billingClosureType.setCode("AFFAIRE");
		 * billingClosureType.setLabel("Par affaire");
		 * billingClosureTypeRepository.save(billingClosureType);
		 * 
		 * billingClosureType = new BillingClosureType();
		 * billingClosureType.setCode("COMPTABLE");
		 * billingClosureType.setLabel("Comptable");
		 * billingClosureTypeRepository.save(billingClosureType);
		 * 
		 * billingClosureType = new BillingClosureType();
		 * billingClosureType.setCode("MENSUEL");
		 * billingClosureType.setLabel("Mensuel");
		 * billingClosureTypeRepository.save(billingClosureType);
		 * 
		 * billingClosureRecipientTypeRepository.deleteAll();
		 * BillingClosureRecipientType billingClosureRecipientType = new
		 * BillingClosureRecipientType();
		 * billingClosureRecipientType.setCode("REPONSABLE");
		 * billingClosureRecipientType.setLabel("Reponsable");
		 * billingClosureRecipientTypeRepository.save(billingClosureRecipientType);
		 * 
		 * billingClosureRecipientType = new BillingClosureRecipientType();
		 * billingClosureRecipientType.setCode("TOUS");
		 * billingClosureRecipientType.setLabel("Tous");
		 * billingClosureRecipientTypeRepository.save(billingClosureRecipientType);
		 * 
		 * billingClosureRecipientType = new BillingClosureRecipientType();
		 * billingClosureRecipientType.setCode("OTHERS");
		 * billingClosureRecipientType.setLabel("Autres");
		 * billingClosureRecipientTypeRepository.save(billingClosureRecipientType);
		 * 
		 * attachmentTypeRepository.deleteAll();
		 * AttachmentType attachmentType = new AttachmentType();
		 * attachmentType.setCode("1");
		 * attachmentType.setLabel("Document client");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("2");
		 * attachmentType.setLabel("Devis");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("3");
		 * attachmentType.setLabel("Contrat");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("4");
		 * attachmentType.setLabel("Commande");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("5");
		 * attachmentType.setLabel("Facture");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("6");
		 * attachmentType.setLabel("KBIS");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("7");
		 * attachmentType.setLabel("CNI");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("8");
		 * attachmentType.setLabel("Justificatif de domicile");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * attachmentType = new AttachmentType();
		 * attachmentType.setCode("9");
		 * attachmentType.setLabel("Logo");
		 * attachmentTypeRepository.save(attachmentType);
		 * 
		 * tiersFollowupTypeRepository.deleteAll();
		 * TiersFollowupType tiersFollowupType = new TiersFollowupType();
		 * tiersFollowupType.setCode("VISITE");
		 * tiersFollowupType.setLabel("Visite");
		 * tiersFollowupTypeRepository.save(tiersFollowupType);
		 * 
		 * tiersFollowupType = new TiersFollowupType();
		 * tiersFollowupType.setCode("ENVOI_CADEAU");
		 * tiersFollowupType.setLabel("Envoi d'un cadeau");
		 * tiersFollowupTypeRepository.save(tiersFollowupType);
		 * 
		 * tiersFollowupType = new TiersFollowupType();
		 * tiersFollowupType.setCode("APPEL");
		 * tiersFollowupType.setLabel("Appel");
		 * tiersFollowupTypeRepository.save(tiersFollowupType);
		 * 
		 * giftRepository.deleteAll();
		 * Gift gift = new Gift();
		 * gift.setCode("CHAMPAGNE");
		 * gift.setLabel("Champagne");
		 * giftRepository.save(gift);
		 * 
		 * gift = new Gift();
		 * gift.setCode("FOIE GRAS");
		 * gift.setLabel("Foie gras");
		 * giftRepository.save(gift);
		 * 
		 * jssSubscriptionRepository.deleteAll();
		 * 
		 * subscriptionPeriodTypeRepository.deleteAll();
		 * SubscriptionPeriodType subscriptionPeriodType = new SubscriptionPeriodType();
		 * subscriptionPeriodType.setCode("3");
		 * subscriptionPeriodType.setLabel("3 mois");
		 * subscriptionPeriodTypeRepository.save(subscriptionPeriodType);
		 * 
		 * subscriptionPeriodType = new SubscriptionPeriodType();
		 * subscriptionPeriodType.setCode("6");
		 * subscriptionPeriodType.setLabel("6 mois");
		 * subscriptionPeriodTypeRepository.save(subscriptionPeriodType);
		 * 
		 * subscriptionPeriodType = new SubscriptionPeriodType();
		 * subscriptionPeriodType.setCode("12");
		 * subscriptionPeriodType.setLabel("12 mois");
		 * subscriptionPeriodTypeRepository.save(subscriptionPeriodType);
		 * 
		 * quotationRepository.deleteAll();
		 * quotationStatusRepository.deleteAll();
		 * 
		 * QuotationStatus quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("OPEN");
		 * quotationStatus.setLabel("Ouvert");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("TO_VERIFY");
		 * quotationStatus.setLabel("A vérifier");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("BILLED");
		 * quotationStatus.setLabel("Facturée");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("VALIDATED_BY_JSS");
		 * quotationStatus.setLabel("Validé par JSS");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("SENT_TO_CUSTOMER");
		 * quotationStatus.setLabel("Envoyé au client");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("VALIDATED_BY_CUSTOMER");
		 * quotationStatus.setLabel("Validé par le client");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("REFUSED_BY_CUSTOMER");
		 * quotationStatus.setLabel("Refusé par le client");
		 * 
		 * quotationStatusRepository.save(quotationStatus);
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("ABANDONED");
		 * quotationStatus.setLabel("Abandonné");
		 * 
		 * quotationStatusRepository.save(quotationStatus);
		 * quotationStatus = new QuotationStatus();
		 * quotationStatus.setCode("CANCELLED");
		 * quotationStatus.setLabel("Annulé");
		 * quotationStatusRepository.save(quotationStatus);
		 * 
		 * quotationLabelTypeRepository.deleteAll();
		 * QuotationLabelType quotationLabelType = new QuotationLabelType();
		 * quotationLabelType.setCode("AFFAIRE");
		 * quotationLabelType.setLabel("Affaire");
		 * quotationLabelTypeRepository.save(quotationLabelType);
		 * 
		 * quotationLabelType = new QuotationLabelType();
		 * quotationLabelType.setCode("CLIENT");
		 * quotationLabelType.setLabel("Client");
		 * quotationLabelTypeRepository.save(quotationLabelType);
		 * 
		 * quotationLabelType = new QuotationLabelType();
		 * quotationLabelType.setCode("AUTRES");
		 * quotationLabelType.setLabel("Autres");
		 * quotationLabelTypeRepository.save(quotationLabelType);
		 * 
		 * recordTypeRepository.deleteAll();
		 * RecordType recordType = new RecordType();
		 * recordType.setCode("DEMATERIALISE");
		 * recordType.setLabel("Dématérialisé");
		 * recordTypeRepository.save(recordType);
		 * 
		 * recordType = new RecordType();
		 * recordType.setCode("PAPIER");
		 * recordType.setLabel("PAPIER");
		 * recordTypeRepository.save(recordType);
		 * 
		 * legalFormRepository.deleteAll();
		 * LegalForm legalForm = new LegalForm();
		 * legalForm.setCode("EARL");
		 * legalForm.setLabel("EARL");
		 * legalFormRepository.save(legalForm);
		 * 
		 * legalForm = new LegalForm();
		 * legalForm.setCode("SARL");
		 * legalForm.setLabel("SARL");
		 * legalFormRepository.save(legalForm);
		 * 
		 * legalForm = new LegalForm();
		 * legalForm.setCode("32");
		 * legalForm.setLabel("Société non immatriculée");
		 * legalFormRepository.save(legalForm);
		 * 
		 * provisionTypeRepository.deleteAll();
		 * provisionFamilyTypeRepository.deleteAll();
		 * ProvisionFamilyType provisionFamilyType1 = new ProvisionFamilyType();
		 * provisionFamilyType1.setCode("FORMALITE");
		 * provisionFamilyType1.setLabel("Formalité");
		 * provisionFamilyTypeRepository.save(provisionFamilyType1);
		 * 
		 * ProvisionFamilyType provisionFamilyType2 = new ProvisionFamilyType();
		 * provisionFamilyType2.setCode("ANNONCE_LEGALE");
		 * provisionFamilyType2.setLabel("Annonce légale");
		 * provisionFamilyTypeRepository.save(provisionFamilyType2);
		 * 
		 * ProvisionFamilyType provisionFamilyType3 = new ProvisionFamilyType();
		 * provisionFamilyType3.setCode("DOMICILIATION");
		 * provisionFamilyType3.setLabel("Domiciliation");
		 * provisionFamilyTypeRepository.save(provisionFamilyType3);
		 * 
		 * ProvisionFamilyType provisionFamilyType4 = new ProvisionFamilyType();
		 * provisionFamilyType4.setCode("FORMATION");
		 * provisionFamilyType4.setLabel("Formation");
		 * provisionFamilyTypeRepository.save(provisionFamilyType4);
		 * 
		 * ProvisionFamilyType provisionFamilyType5 = new ProvisionFamilyType();
		 * provisionFamilyType5.setCode("BODACC");
		 * provisionFamilyType5.setLabel("BODACC");
		 * provisionFamilyTypeRepository.save(provisionFamilyType5);
		 * 
		 * provisionTypeRepository.deleteAll();
		 * ProvisionType provisionType = new ProvisionType();
		 * provisionType.setCode("FORMALITE");
		 * provisionType.setLabel("Formalité");
		 * provisionType.setProvisionFamilyType(provisionFamilyType1);
		 * provisionTypeRepository.save(provisionType);
		 * 
		 * provisionType = new ProvisionType();
		 * provisionType.setCode("ANNONCE_LEGALE");
		 * provisionType.setLabel("Annonce légale");
		 * provisionType.setProvisionFamilyType(provisionFamilyType2);
		 * provisionTypeRepository.save(provisionType);
		 * 
		 * provisionType = new ProvisionType();
		 * provisionType.setCode("DOMICILIATION");
		 * provisionType.setLabel("Domiciliation");
		 * provisionType.setProvisionFamilyType(provisionFamilyType3);
		 * provisionTypeRepository.save(provisionType);
		 * 
		 * provisionType = new ProvisionType();
		 * provisionType.setCode("FORMATION");
		 * provisionType.setLabel("Formation");
		 * provisionType.setProvisionFamilyType(provisionFamilyType4);
		 * provisionTypeRepository.save(provisionType);
		 * 
		 * provisionType = new ProvisionType();
		 * provisionType.setCode("BODACC");
		 * provisionType.setLabel("BODACC");
		 * provisionType.setProvisionFamilyType(provisionFamilyType5);
		 * provisionTypeRepository.save(provisionType);
		 * 
		 * domiciliationContractTypeRepository.deleteAll();
		 * DomiciliationContractType domiciliationDomiciliationContractType = new
		 * DomiciliationContractType();
		 * domiciliationDomiciliationContractType.setCode("1");
		 * domiciliationDomiciliationContractType.
		 * setLabel("Adresse Commerciale, réception et conservation du courrier");
		 * domiciliationContractTypeRepository.save(
		 * domiciliationDomiciliationContractType);
		 * 
		 * domiciliationDomiciliationContractType = new DomiciliationContractType();
		 * domiciliationDomiciliationContractType.setCode("2");
		 * domiciliationDomiciliationContractType
		 * .setLabel("Adresse Commerciale, réception et renvoi du courrier par la poste"
		 * );
		 * domiciliationContractTypeRepository.save(
		 * domiciliationDomiciliationContractType);
		 * 
		 * domiciliationDomiciliationContractType = new DomiciliationContractType();
		 * domiciliationDomiciliationContractType.setCode("3");
		 * domiciliationDomiciliationContractType
		 * .setLabel("Adresse Commerciale, réception et renvoi du courrier par mail");
		 * domiciliationContractTypeRepository.save(
		 * domiciliationDomiciliationContractType);
		 * 
		 * buildingDomiciliationRepository.deleteAll();
		 * BuildingDomiciliation buildingDomiciliation = new BuildingDomiciliation();
		 * buildingDomiciliation.setCode("JSS_RDC");
		 * buildingDomiciliation.setLabel("8 rue St Augustin");
		 * buildingDomiciliationRepository.save(buildingDomiciliation);
		 * 
		 * mailRedirectionTypeRepository.deleteAll();
		 * MailRedirectionType mailRedirectionType = new MailRedirectionType();
		 * mailRedirectionType.setCode("EXERCICE_ACTIVITE");
		 * mailRedirectionType.setLabel("Exercice de l'activité");
		 * mailRedirectionTypeRepository.save(mailRedirectionType);
		 * 
		 * mailRedirectionType = new MailRedirectionType();
		 * mailRedirectionType.setCode("REPRESENTANT_LEGAL");
		 * mailRedirectionType.setLabel("Représentant légal");
		 * mailRedirectionTypeRepository.save(mailRedirectionType);
		 * 
		 * mailRedirectionType = new MailRedirectionType();
		 * mailRedirectionType.setCode("AUTRES");
		 * mailRedirectionType.setLabel("Autres");
		 * mailRedirectionTypeRepository.save(mailRedirectionType);
		 * 
		 * weekDayRepository.deleteAll();
		 * WeekDay day = new WeekDay();
		 * day.setCode("LU");
		 * day.setLabel("Lundi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("MA");
		 * day.setLabel("Mardi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("ME");
		 * day.setLabel("Mercredi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("JE");
		 * day.setLabel("Jeudi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("VE");
		 * day.setLabel("Vendredi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("SA");
		 * day.setLabel("Samedi");
		 * weekDayRepository.save(day);
		 * 
		 * day = new WeekDay();
		 * day.setCode("DI");
		 * day.setLabel("Dimanche");
		 * weekDayRepository.save(day);
		 * 
		 * journalTypeRepository.deleteAll();
		 * JournalType journalType = new JournalType();
		 * journalType.setCode("SPEL");
		 * journalType.setLabel("SPEL");
		 * journalTypeRepository.save(journalType);
		 * 
		 * journalType = new JournalType();
		 * journalType.setCode("PAPIER");
		 * journalType.setLabel("Papier");
		 * journalTypeRepository.save(journalType);
		 * 
		 * List<Department> departments =
		 * IterableUtils.toList(departmentRepository.findAll());
		 * confrereRepository.deleteAll();
		 * Confrere confrere = new Confrere();
		 * confrere.setAdministrativeFees(10.0f);
		 * confrere.setDepartments(departments);
		 * confrere.setJournalType(journalType);
		 * confrere.setLastShipmentForPublication("lastShipmentForPublication");
		 * confrere.setNumberOfPrint(20000);
		 * confrere.setReinvoicing(10);
		 * confrere.setShippingCosts(10.0f);
		 * List<WeekDay> weekDays = new ArrayList<WeekDay>();
		 * weekDays.add(day);
		 * confrere.setWeekDays(weekDays);
		 * confrereRepository.save(confrere);
		 * 
		 * departments = IterableUtils.toList(departmentRepository.findAll());
		 * weekDays = IterableUtils.toList(weekDayRepository.findAll());
		 * confrere = new Confrere();
		 * confrere.setAdministrativeFees(10.0f);
		 * confrere.setDepartments(departments);
		 * confrere.setLastShipmentForPublication("lastShipmentForPublication");
		 * confrere.setNumberOfPrint(20000);
		 * confrere.setReinvoicing(10);
		 * confrere.setShippingCosts(10.0f);
		 * confrere.setWeekDays(weekDays);
		 * confrereRepository.save(confrere);
		 * 
		 * departments = IterableUtils.toList(departmentRepository.findAll());
		 * weekDays = IterableUtils.toList(weekDayRepository.findAll());
		 * confrere = new Confrere();
		 * confrere.setAdministrativeFees(10.0f);
		 * confrere.setDepartments(departments);
		 * confrere.setLastShipmentForPublication("lastShipmentForPublication2");
		 * confrere.setNumberOfPrint(30000);
		 * confrere.setReinvoicing(10);
		 * confrere.setShippingCosts(10.0f);
		 * confrere.setWeekDays(weekDays);
		 * confrereRepository.save(confrere);
		 * 
		 * departments = IterableUtils.toList(departmentRepository.findAll());
		 * characterPriceRepository.deleteAll();
		 * CharacterPrice characterPrice = new CharacterPrice();
		 * characterPrice.setDepartments(departments);
		 * characterPrice.setPrice(0.38f);
		 * characterPrice.setStartDate(LocalDate.now());
		 * characterPriceRepository.save(characterPrice);
		 * 
		 * noticeTypeFamilyRepository.deleteAll();
		 * noticeTypeRepository.deleteAll();
		 * 
		 * NoticeTypeFamily noticeTypeFamily = new NoticeTypeFamily();
		 * noticeTypeFamily.setCode("MOD");
		 * noticeTypeFamily.setLabel("Modifications");
		 * noticeTypeFamilyRepository.save(noticeTypeFamily);
		 * 
		 * NoticeType noticeType = new NoticeType();
		 * noticeType.setCode("DENOM");
		 * noticeType.setLabel("Dénomincation");
		 * noticeType.setNoticeTypeFamily(noticeTypeFamily);
		 * noticeTypeRepository.save(noticeType);
		 * 
		 * noticeType = new NoticeType();
		 * noticeType.setCode("MOUV");
		 * noticeType.setLabel("Mouvement sur l'activité");
		 * noticeType.setNoticeTypeFamily(noticeTypeFamily);
		 * noticeTypeRepository.save(noticeType);
		 * 
		 * bodaccPublicationTypeRepository.deleteAll();
		 * BodaccPublicationType bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("1");
		 * bodaccPublicationType.setLabel("Cession de fonds de commerce");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("2");
		 * bodaccPublicationType.setLabel("Fusion");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("3");
		 * bodaccPublicationType.setLabel("Scission");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("6");
		 * bodaccPublicationType.setLabel("Apport partiel d'actifs");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("4");
		 * bodaccPublicationType.setLabel("Envoi en possession");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * bodaccPublicationType = new BodaccPublicationType();
		 * bodaccPublicationType.setCode("5");
		 * bodaccPublicationType.setLabel("Désignation du mandataire successoral");
		 * bodaccPublicationTypeRepository.save(bodaccPublicationType);
		 * 
		 * transfertFundsTypeRepository.deleteAll();
		 * TransfertFundsType transfertFundsType = new TransfertFundsType();
		 * transfertFundsType.setCode("1");
		 * transfertFundsType.setLabel("Acquéreur physique");
		 * transfertFundsTypeRepository.save(transfertFundsType);
		 * 
		 * transfertFundsType = new TransfertFundsType();
		 * transfertFundsType.setCode("2");
		 * transfertFundsType.setLabel("Acquéreur moral");
		 * transfertFundsTypeRepository.save(transfertFundsType);
		 * 
		 * transfertFundsType = new TransfertFundsType();
		 * transfertFundsType.setCode("3");
		 * transfertFundsType.setLabel("Résilisation du bail commercial");
		 * transfertFundsTypeRepository.save(transfertFundsType);
		 * 
		 * competentAuthorityTypeRepository.deleteAll();
		 * CompetentAuthorityType competentAuthorityType = new CompetentAuthorityType();
		 * competentAuthorityType.setCode("1");
		 * competentAuthorityType.setLabel("RCS");
		 * competentAuthorityTypeRepository.save(competentAuthorityType);
		 * 
		 * competentAuthorityType = new CompetentAuthorityType();
		 * competentAuthorityType.setCode("2");
		 * competentAuthorityType.setLabel("CFP - Centre des finances publiques");
		 * competentAuthorityTypeRepository.save(competentAuthorityType);
		 * 
		 * competentAuthorityType = new CompetentAuthorityType();
		 * competentAuthorityType.setCode("3");
		 * competentAuthorityType.setLabel("RCS - Registre du Commerce et des Sociétés"
		 * );
		 * competentAuthorityTypeRepository.save(competentAuthorityType);
		 * 
		 * competentAuthorityRepository.deleteAll();
		 * CompetentAuthority competentAuthority = new CompetentAuthority();
		 * competentAuthority.setCode("1");
		 * competentAuthority.setLabel("Paris");
		 * competentAuthority.setCompetentAuthorityType(competentAuthorityType);
		 * competentAuthorityRepository.save(competentAuthority);
		 * 
		 * fundTypeRepository.deleteAll();
		 * FundType fundType = new FundType();
		 * fundType.setCode("1");
		 * fundType.setLabel("Principal");
		 * fundTypeRepository.save(fundType);
		 * fundType = new FundType();
		 * fundType.setCode("2");
		 * fundType.setLabel("Secondaire");
		 * fundTypeRepository.save(fundType);
		 * fundType = new FundType();
		 * fundType.setCode("3");
		 * fundType.setLabel("Complémentaire");
		 * fundTypeRepository.save(fundType);
		 * 
		 * actTypeRepository.deleteAll();
		 * ActType actType = new ActType();
		 * actType.setCode("1");
		 * actType.setLabel("Sous seing privé");
		 * actTypeRepository.save(actType);
		 * actType = new ActType();
		 * actType.setCode("2");
		 * actType.setLabel("Forme authentique");
		 * actTypeRepository.save(actType);
		 * 
		 * AnnouncementNoticeTemplateRepository.deleteAll();
		 * AnnouncementNoticeTemplate AnnouncementNoticeTemplate = new
		 * AnnouncementNoticeTemplate();
		 * AnnouncementNoticeTemplate.setCode("1");
		 * AnnouncementNoticeTemplate.setLabel("Changement de nom");
		 * AnnouncementNoticeTemplate.setText(
		 * "Mr/Mme/Melle Prénom Nom, né le 00/00/0000 à Ville (Département) de nationalité, activité, demeurant adresse, code postal ville, dépose une requête auprès du Garde des Sceaux à l’effet de substituer à son nom patronymique celui de « Nom souhaité »."
		 * );
		 * AnnouncementNoticeTemplateRepository.save(AnnouncementNoticeTemplate);
		 * 
		 * AnnouncementNoticeTemplate = new AnnouncementNoticeTemplate();
		 * AnnouncementNoticeTemplate.setCode("2");
		 * AnnouncementNoticeTemplate.setLabel("Envoi en possession");
		 * AnnouncementNoticeTemplate.setText(
		 * "Par testament du 00/00/0000 déposé au rang des minutes de Maitre (NOM PRENOM) Notaire à … (ADRESSE) suivant procès-verbal dont la copie authentique a été reçue par le Tribunal de Grande Instance de X le 00/00/0000. (Tribunal du domicile du défunt)				M. ou MME NOM- PRENOMS (le défunt) demeurant de son vivant à (adresse), né le 00/00/0000 à (Ville du lieu de naissance) et décédé le 00/00/0000 (Ville du lieu de décès), veuf ou veuve de M. ou MME NOM PRENOMS a institué un ou plusieurs légataires universels (NOMS PRENOMS et ADRESSES).<br>Les oppositions seront reçues dans le délai d’un mois à compter de 00/00/0000 (date de réception par le greffe du TGI) entre les mains du Notaire chargé du règlement de la succession (si le notaire dépositaire du testament n’est pas celui chargé de la succession, (le préciser ) <br>Pour avis<br>Maitre XXXXXX"
		 * );
		 * AnnouncementNoticeTemplateRepository.save(AnnouncementNoticeTemplate);
		 * 
		 * accountingAccountClassRepository.deleteAll();
		 * AccountingAccountClass accountingAccountClass = new AccountingAccountClass();
		 * accountingAccountClass.setCode("6");
		 * accountingAccountClass.setLabel("Charge");
		 * accountingAccountClassRepository.save(accountingAccountClass);
		 * 
		 * accountingAccountClass = new AccountingAccountClass();
		 * accountingAccountClass.setCode("7");
		 * accountingAccountClass.setLabel("Produits");
		 * accountingAccountClassRepository.save(accountingAccountClass);
		 * 
		 * accountingAccountClass = new AccountingAccountClass();
		 * accountingAccountClass.setCode("4");
		 * accountingAccountClass.setLabel("Tiers");
		 * accountingAccountClassRepository.save(accountingAccountClass);
		 * 
		 * vatCollectionTypeRepository.deleteAll();
		 * VatCollectionType vatCollectionType = new VatCollectionType();
		 * vatCollectionType.setCode("DEBIT");
		 * vatCollectionType.setLabel("Au débit");
		 * vatCollectionTypeRepository.save(vatCollectionType);
		 * vatCollectionType = new VatCollectionType();
		 * vatCollectionType.setCode("ENCAISSEMENT");
		 * vatCollectionType.setLabel("A l'encaissement");
		 * vatCollectionTypeRepository.save(vatCollectionType);
		 * 
		 * invoiceStatusRepository.deleteAll();
		 * InvoiceStatus status = new InvoiceStatus();
		 * status.setCode("SENT");
		 * status.setLabel("Envoyée");
		 * invoiceStatusRepository.save(status);
		 * 
		 * status = new InvoiceStatus();
		 * status.setCode("PAYED");
		 * status.setLabel("Réglée/lettrée");
		 * invoiceStatusRepository.save(status);
		 * 
		 * status = new InvoiceStatus();
		 * status.setCode("CANCELLED");
		 * status.setLabel("Annulée");
		 * invoiceStatusRepository.save(status);
		 */
	}

}
