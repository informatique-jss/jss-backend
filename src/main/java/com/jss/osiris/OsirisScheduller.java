package com.jss.osiris;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.repository.ProviderRepository;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.ProviderService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.repository.ConfrereRepository;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.BodaccStatusService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.SimpleProvisionStatusService;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.repository.TiersRepository;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
@ConditionalOnProperty(value = "schedulling.enabled", matchIfMissing = false, havingValue = "true")
public class OsirisScheduller {

	@Autowired
	AccountingRecordService accountingRecordService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CustomerMailService customerMailService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	GlobalExceptionHandler globalExceptionHandler;

	@Value("${schedulling.pool.size}")
	private Integer schedullingPoolSize;

	@Autowired
	QuotationStatusService quotationStatusService;

	@Autowired
	CustomerOrderStatusService customerOrderStatusService;

	@Autowired
	AnnouncementStatusService announcementStatusService;

	@Autowired
	FormaliteStatusService formaliteStatusService;

	@Autowired
	DomiciliationStatusService domiciliationStatusService;

	@Autowired
	SimpleProvisionStatusService simpleProvisionStatusService;

	@Autowired
	BodaccStatusService bodaccStatusService;

	@Autowired
	AssignationTypeService assignationTypeService;

	@Autowired
	ProvisionScreenTypeService provisionScreenTypeService;

	@Autowired
	QuotationService quotationService;

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	AnnouncementService announcementService;

	@Bean
	public ThreadPoolTaskScheduler taskExecutor() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(schedullingPoolSize);
		return scheduler;
	}

	@Scheduled(cron = "${schedulling.account.daily.close}")
	private void dailyAccountClosing() {
		try {
			accountingRecordService.dailyAccountClosing();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.active.directory.user.update}")
	private void activeDirectoryUserUpdate() {
		try {
			employeeService.updateUserFromActiveDirectory();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.mail.sender}")
	private void mailSender() {
		try {
			customerMailService.sendNextMail();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.notification.purge}")
	private void purgeNotidication() {
		try {
			notificationService.purgeNotification();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.quotation.reminder}")
	private void reminderQuotation() {
		try {
			quotationService.sendRemindersForQuotation();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.deposit.reminder}")
	private void reminderCustomerOrderDeposit() {
		try {
			customerOrderService.sendRemindersForCustomerOrderDeposit();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.invoice.reminder}")
	private void reminderCustomerOrderInvoice() {
		try {
			invoiceService.sendRemindersForInvoices();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.account.receipt.generation.sender}")
	private void sendBillingClosureReceipt() {
		try {
			accountingRecordService.sendBillingClosureReceipt();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.announcement.publish.actu.legale}")
	private void publishAnnouncementToActuLegale() {
		try {
			announcementService.publishAnnouncementsToActuLegale();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.announcement.publication.flag}")
	private void sendPublicationFlagNotSent() {
		try {
			announcementService.sendPublicationFlagNotSent();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelay = 1000000000)
	private void updateAllStatusEntityReferentials() {
		try {
			quotationStatusService.updateStatusReferential();
			customerOrderStatusService.updateStatusReferential();
			announcementStatusService.updateStatusReferential();
			formaliteStatusService.updateStatusReferential();
			domiciliationStatusService.updateStatusReferential();
			simpleProvisionStatusService.updateStatusReferential();
			bodaccStatusService.updateStatusReferential();
			assignationTypeService.updateAssignationTypes();
			provisionScreenTypeService.updateScreenTypes();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	// TODO delete
	@Autowired
	TiersService tiersService;

	@Autowired
	ProviderService providerService;

	@Autowired
	ConfrereService confrereService;

	@Autowired
	AccountingAccountService accountingAccountService;

	@Autowired
	ConfrereRepository confrereRepository;

	@Autowired
	ProviderRepository providerRepository;

	@Autowired
	TiersRepository tiersRepository;

	@Scheduled(initialDelay = 1000, fixedDelay = 1000000000)
	private void updateAccontingAccount() {
		try {
			List<Provider> providers = providerService.getProviders();
			if (providers != null)
				for (Provider provider : providers)
					if (provider.getAccountingAccountCustomer() == null
							&& provider.getAccountingAccountDeposit() == null
							&& provider.getAccountingAccountProvider() == null) {
						AccountingAccountTrouple accountingAccountCouple = accountingAccountService
								.generateAccountingAccountsForEntity(provider.getLabel());
						provider.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
						provider.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
						provider.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
						providerRepository.save(provider);
					}

			List<Confrere> confreres = confrereService.getConfreres();
			for (Confrere confrere : confreres)
				if (confrere.getAccountingAccountCustomer() == null && confrere.getAccountingAccountDeposit() == null
						&& confrere.getAccountingAccountProvider() == null) {

					AccountingAccountTrouple accountingAccountCouple = accountingAccountService
							.generateAccountingAccountsForEntity(confrere.getLabel());
					confrere.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
					confrere.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
					confrere.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
					confrereRepository.save(confrere);

				}

			List<Tiers> tiers = tiersService.getTiers();
			int i = 0;
			for (Tiers tier : tiers) {
				i++;
				System.out.println(i);
				if (tier.getAccountingAccountCustomer() == null && tier.getAccountingAccountDeposit() == null
						&& tier.getAccountingAccountProvider() == null) {
					String label = "";
					if (tier.getIsIndividual()) {
						label = tier.getFirstname() + " " + tier.getLastname();
					} else {
						label = tier.getDenomination();
					}

					try {
						AccountingAccountTrouple accountingAccountCouple = accountingAccountService
								.generateAccountingAccountsForEntity(label);
						tier.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
						tier.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
						tier.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
						tiersRepository.save(tier);
					} catch (Exception e) {
					}
				}
			}

		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}
}
