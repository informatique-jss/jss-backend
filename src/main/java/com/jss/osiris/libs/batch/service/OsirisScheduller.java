package com.jss.osiris.libs.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.libs.node.service.NodeService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.EtablissementPublicsDelegate;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.CentralPayPaymentRequestService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.MissingAttachmentQueryService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.SimpleProvisionStatusService;
import com.jss.osiris.modules.quotation.service.guichetUnique.GuichetUniqueDelegateService;

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
	GlobalExceptionHandler globalExceptionHandler;

	@Value("${schedulling.pool.size}")
	private Integer schedullingPoolSize;

	@Value("${dev.mode}")
	private Boolean devMode;

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
	AssignationTypeService assignationTypeService;

	@Autowired
	ProvisionScreenTypeService provisionScreenTypeService;

	@Autowired
	QuotationService quotationService;

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	CustomerMailService customerMailService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	AnnouncementService announcementService;

	@Autowired
	MissingAttachmentQueryService missingAttachmentQueryService;

	@Autowired
	EtablissementPublicsDelegate etablissementPublicsDelegate;

	@Autowired
	AffaireService affaireService;

	@Autowired
	GuichetUniqueDelegateService guichetUniqueDelegateService;

	@Autowired
	CentralPayPaymentRequestService centralPayPaymentRequestService;

	@Autowired
	BatchSettingsService batchSettingsService;

	@Autowired
	BatchService batchService;

	@Autowired
	CompetentAuthorityService competentAuthorityService;

	@Autowired
	NodeService nodeService;

	@Autowired
	ConstantService constantService;

	@Bean
	public ThreadPoolTaskScheduler taskExecutor() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(schedullingPoolSize);
		return scheduler;
	}

	@Scheduled(cron = "${schedulling.account.daily.close}")
	private void dailyAccountClosing() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.DAILY_ACCOUNT_CLOSING, null);
	}

	@Scheduled(cron = "${schedulling.active.directory.user.update}")
	private void activeDirectoryUserUpdate() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.ACTIVE_DIRECTORY_USER_UPDATE, null);
	}

	@Scheduled(cron = "${schedulling.notification.purge}")
	private void purgeNotidication() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.PURGE_NOTIFICATION, null);
	}

	@Scheduled(cron = "${schedulling.log.purge}")
	private void purgeLogs() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.PURGE_LOGS, null);
	}

	@Scheduled(cron = "${schedulling.batch.purge}")
	private void purgeBatch() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.PURGE_BATCH, null);
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.central.pay.payment.request.validation.check}")
	private void checkAllCentralPayPaymentRequests()
			throws OsirisException, OsirisClientMessageException,
			OsirisValidationException, OsirisDuplicateException {
		try {
			if (nodeService.shouldIBatch())
				centralPayPaymentRequestService.checkAllPaymentRequests();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.quotation.reminder}")
	private void reminderQuotation() {
		try {
			if (nodeService.shouldIBatch())
				quotationService.sendRemindersForQuotation();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.deposit.reminder}")
	private void reminderCustomerOrderDeposit() {
		try {
			if (nodeService.shouldIBatch())
				customerOrderService.sendRemindersForCustomerOrderDeposit();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.invoice.reminder}")
	private void reminderCustomerOrderInvoice() {
		try {
			if (nodeService.shouldIBatch()) {
				invoiceService.sendRemindersForInvoices(constantService.getBillingLabelTypeCodeAffaire());
				invoiceService.sendRemindersForInvoices(constantService.getBillingLabelTypeOther());
			}
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.announcement.confrere.query.reminder}")
	private void reminderConfrereForAnnouncementQuery() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.sendRemindersToConfrereForAnnouncement();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.invoice.confrere.query.reminder}")
	private void reminderConfrereForProviderInvoice() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.sendRemindersToConfrereForProviderInvoice();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customer.proof.reading.reminder}")
	private void reminderClientReviewQuery() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.sendRemindersToCustomerForProofReading();

		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customer.bilan.publication.reminder}")
	private void reminderToCustomerForBilanPublication() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.sendRemindersToCustomerForBilanPublication();

		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customer.missing.attachment.queries}")
	private void sendRemindersToCustomerForMissingAttachmentQuery() {
		try {
			if (nodeService.shouldIBatch())
				missingAttachmentQueryService.sendRemindersToCustomerForMissingAttachmentQuery();

		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.competent.authority.reminder}")
	private void sendRemindersToCompetentAuthorities() {
		try {
			if (nodeService.shouldIBatch())
				competentAuthorityService.sendRemindersToCompetentAuthorities();

		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.announcement.publish.actu.legale}")
	private void publishAnnouncementToActuLegale() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.publishAnnouncementsToActuLegale();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.announcement.publication.flag}")
	private void sendPublicationFlagNotSent() {
		try {
			if (nodeService.shouldIBatch())
				announcementService.sendPublicationFlagsNotSent();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.audit.clean}")
	private void cleanAudit() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.CLEAN_AUDIT, null);
	}

	@Scheduled(cron = "${schedulling.competant.authorities.update}")
	private void updateCompetentAuthorities() throws OsirisException {
		if (nodeService.shouldIBatch())
			batchService.declareNewBatch(Batch.UPDATE_COMPETENT_AUTHORITY, null);
	}

	// @Scheduled(cron = "${schedulling.affaire.rne.update}")
	private void updateAffaireFromRne() {
		try {
			if (nodeService.shouldIBatch())
				affaireService.updateAffairesFromRne();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.guichet.unique.refresh.opened}")
	private void refreshAllOpenFormalities() {
		try {
			if (nodeService.shouldIBatch() && !devMode)
				guichetUniqueDelegateService.refreshAllOpenFormalities();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.guichet.unique.refresh.update.last.hour}")
	private void refreshFormalitiesFromLastHour() {
		try {
			if (nodeService.shouldIBatch() && !devMode)
				guichetUniqueDelegateService.refreshFormalitiesFromLastHour();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelayString = "${schedulling.infogreffe.refresh.last.day}")
	private void refreshAllFormalitiesInfogreffeFromLastDay() {
		try {
			if (nodeService.shouldIBatch())
				batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE, 1);
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.infogreffe.refresh.all}")
	private void refreshAllFormalitiesInfogreffe() {
		try {
			if (nodeService.shouldIBatch())
				batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE, 0);
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.payment.automatch}")
	private void automatchPayments() {
		try {
			if (nodeService.shouldIBatch())
				paymentService.paymentGrab();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.account.receipt.generation.sender}")
	private void sendBillingClosureReceipt() {
		try {
			if (nodeService.shouldIBatch())
				accountingRecordService.sendBillingClosureReceipt();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(cron = "${schedulling.customer.order.recurring.generation}")
	private void generateRecurringCustomerOrders() {
		try {
			if (nodeService.shouldIBatch())
				customerOrderService.generateRecurringCustomerOrders();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60)
	private void sendTemporizedMails() {
		try {
			if (nodeService.shouldIBatch())
				customerMailService.sendTemporizedMails();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
	private void updateAllStatusEntityReferentials() {
		try {
			quotationStatusService.updateStatusReferential();
			customerOrderStatusService.updateStatusReferential();
			announcementStatusService.updateStatusReferential();
			formaliteStatusService.updateStatusReferential();
			domiciliationStatusService.updateStatusReferential();
			simpleProvisionStatusService.updateStatusReferential();
			assignationTypeService.updateAssignationTypes();
			provisionScreenTypeService.updateScreenTypes();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
	private void initializeBatchSettings() {
		try {
			batchSettingsService.initializeBatchSettings();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1, fixedDelay = 1000)
	private void checkBatch() {
		try {
			batchService.checkBatch();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

	@Scheduled(initialDelay = 1000, fixedDelay = 5000)
	private void updateNodeStatus() {
		try {
			nodeService.updateNodeStatus();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e);
		}
	}

}
