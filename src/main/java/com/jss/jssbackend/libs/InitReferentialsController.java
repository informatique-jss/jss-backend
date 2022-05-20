package com.jss.jssbackend.libs;

import java.util.ArrayList;
import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.model.VatRate;
import com.jss.jssbackend.modules.miscellaneous.repository.CivilityRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.LanguageRepository;
import com.jss.jssbackend.modules.miscellaneous.repository.VatRateRepository;
import com.jss.jssbackend.modules.profile.model.Employee;
import com.jss.jssbackend.modules.profile.model.Team;
import com.jss.jssbackend.modules.profile.repository.EmployeeRepository;
import com.jss.jssbackend.modules.profile.repository.TeamRepository;
import com.jss.jssbackend.modules.tiers.model.BillingItem;
import com.jss.jssbackend.modules.tiers.model.BillingType;
import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.model.SpecialOffer;
import com.jss.jssbackend.modules.tiers.model.TiersCategory;
import com.jss.jssbackend.modules.tiers.model.TiersType;
import com.jss.jssbackend.modules.tiers.repository.BillingItemRepository;
import com.jss.jssbackend.modules.tiers.repository.BillingTypeRepository;
import com.jss.jssbackend.modules.tiers.repository.MailRepository;
import com.jss.jssbackend.modules.tiers.repository.PhoneRepository;
import com.jss.jssbackend.modules.tiers.repository.SpecialOfferRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersCategoryRepository;
import com.jss.jssbackend.modules.tiers.repository.TiersTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO : delete !!!
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
	VatRateRepository vatRateRepository;

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

	@GetMapping(inputEntryPoint + "/create")
	public void create() {
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
		vatRateRepository.deleteAll();
		VatRate vatRate = new VatRate();
		vatRate.setLabel("Non taxable");
		vatRate.setRate(0f);
		vatRateRepository.save(vatRate);

		vatRate = new VatRate();
		vatRate.setLabel("20.0");
		vatRate.setRate(20f);
		vatRateRepository.save(vatRate);

		BillingType billingType = new BillingType();
		billingType.setLabel("Poste 1");
		billingType.setPreTaxPrice(10f);
		billingType.setAccountingCode(10);
		billingTypeRepository.save(billingType);

		BillingItem billingItem = new BillingItem();
		billingItem.setBillingType(billingType);
		billingItem.setDiscountAmount(0f);
		billingItem.setDiscountRate(0f);
		billingItem.setVatRate(vatRate);
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
	}
}
