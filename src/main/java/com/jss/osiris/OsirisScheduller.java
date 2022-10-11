package com.jss.osiris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class OsirisScheduller {

	@Autowired
	AccountingRecordService accountingRecordService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	EmployeeService employeeService;

	private static final Logger logger = LoggerFactory.getLogger(OsirisScheduller.class);

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
}
