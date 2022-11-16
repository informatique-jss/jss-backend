package com.jss.osiris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.BodaccStatusService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;

@Service
public class OsirisScheduller {

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
	BodaccStatusService bodaccStatusService;

	@Autowired
	AssignationTypeService assignationTypeService;

	@Autowired
	ProvisionScreenTypeService provisionScreenTypeService;

	private static final Logger logger = LoggerFactory.getLogger(OsirisScheduller.class);

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

	@Scheduled(cron = "${schedulling.payment.grab}")
	private void paymentGrab() throws Exception {
		logger.info("Start of payment grab");
		paymentService.payementGrab();
	}

	@Scheduled(cron = "${schedulling.active.directory.user.update}")
	private void activeDirectoryUserUpdate() throws Exception {
		logger.info("Start of user update from Active Directory");
		employeeService.updateUserFromActiveDirectory();
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.mail.sender}")
	private void mailSender() throws Exception {
		mailHelper.sendNextMail();
	}

	@Scheduled(cron = "${schedulling.notification.purge}")
	private void purgeNotidication() throws Exception {
		notificationService.purgeNotification();
	}

	// @Scheduled(initialDelay = 1000, fixedDelay = 1000000000)
	private void updateAllStatusEntityReferentials() throws Exception {
		quotationStatusService.updateStatusReferential();
		customerOrderStatusService.updateStatusReferential();
		announcementStatusService.updateStatusReferential();
		formaliteStatusService.updateStatusReferential();
		domiciliationStatusService.updateStatusReferential();
		bodaccStatusService.updateStatusReferential();
		assignationTypeService.updateAssignationTypes();
		provisionScreenTypeService.updateScreenTypes();
	}
}