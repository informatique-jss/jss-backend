package com.jss.osiris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.BodaccStatusService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.SimpleProvisionStatusService;

@Service
@ConditionalOnProperty(value = "schedulling.enabled", matchIfMissing = false, havingValue = "true")
public class OsirisScheduller {

	private static final Logger logger = LoggerFactory.getLogger(OsirisScheduller.class);

	@Autowired
	AccountingRecordService accountingRecordService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	MailHelper mailHelper;

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

	@Bean
	public ThreadPoolTaskScheduler taskExecutor() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(schedullingPoolSize);
		return scheduler;
	}

	@Scheduled(cron = "${schedulling.account.daily.close}")
	// @Scheduled(initialDelay = 1000, fixedDelay = 1000000)
	private void dailyAccountClosing() {
		logger.info("Start of daily account closing");
		accountingRecordService.dailyAccountClosing();
	}

	// @Scheduled(cron = "${schedulling.payment.grab}")
	// @Scheduled(initialDelay = 1000, fixedDelay = 1000000)
	private void paymentGrab() {
		logger.info("Start of payment grab");
		try {
			paymentService.payementGrab();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}

	@Scheduled(cron = "${schedulling.active.directory.user.update}")
	private void activeDirectoryUserUpdate() {
		logger.info("Start of user update from Active Directory");
		employeeService.updateUserFromActiveDirectory();
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.mail.sender}")
	private void mailSender() {
		try {
			mailHelper.sendNextMail();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}

	@Scheduled(cron = "${schedulling.notification.purge}")
	private void purgeNotidication() {
		notificationService.purgeNotification();
	}

	@Scheduled(cron = "${schedulling.log.osiris.quotation.reminder}")
	private void reminderQuotation() {
		try {
			quotationService.sendRemindersForQuotation();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.deposit.reminder}")
	private void reminderCustomerOrderDeposit() {
		try {
			customerOrderService.sendRemindersForCustomerOrderDeposit();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.invoice.reminder}")
	private void reminderCustomerOrderInvoice() {
		try {
			invoiceService.sendRemindersForInvoices();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}

	@Scheduled(cron = "${schedulling.account.receipt.generation.sender}")
	private void sendBillingClosureReceipt() {
		try {
			accountingRecordService.sendBillingClosureReceipt();
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
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
		} catch (OsirisException e) {
			globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
		}
	}
}