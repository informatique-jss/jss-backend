package com.jss.osiris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.PaymentService;

@Service
public class OsirisScheduller {

	@Autowired
	AccountingRecordService accountingRecordService;

	@Autowired
	PaymentService paymentService;

	private static final Logger logger = LoggerFactory.getLogger(OsirisScheduller.class);

	@Scheduled(cron = "${schedulling.account.daily.close}")
	private void dailyAccountClosing() {
		logger.info("Start of daily account closing");
		accountingRecordService.dailyAccountClosing();
	}

	// @Scheduled(cron = "${schedulling.payment.grab}")
	@Scheduled(initialDelay = 100, fixedDelay = 1000000000)
	private void paymentGrab() throws Exception {
		logger.info("Start of payment grab");
		paymentService.payementGrab();
	}
}
