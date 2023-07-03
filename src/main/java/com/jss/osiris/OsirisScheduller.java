package com.jss.osiris;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.invoicing.service.AzureReceiptService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.OwncloudGreffeDelegate;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.service.EtablissementPublicsDelegate;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.BodaccStatusService;
import com.jss.osiris.modules.quotation.service.CentralPayPaymentRequestService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
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

	@Autowired
	EtablissementPublicsDelegate etablissementPublicsDelegate;

	@Autowired
	OwncloudGreffeDelegate owncloudGreffeDelegate;

	@Autowired
	GuichetUniqueDelegateService guichetUniqueDelegateService;

	@Autowired
	CentralPayPaymentRequestService centralPayPaymentRequestService;

	@Autowired
	AzureInvoiceService azureInvoiceService;

	@Autowired
	AzureReceiptService azureReceiptService;

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
			System.out.println("Start AD update");
			employeeService.updateUserFromActiveDirectory();
			System.out.println("End AD update");
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
			System.out.println("Start quotation reminder");
			quotationService.sendRemindersForQuotation();
			System.out.println("End quotation reminder");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.deposit.reminder}")
	private void reminderCustomerOrderDeposit() {
		try {
			System.out.println("Start customer order deposit");
			customerOrderService.sendRemindersForCustomerOrderDeposit();
			System.out.println("End customer order deposit");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customerOrder.invoice.reminder}")
	private void reminderCustomerOrderInvoice() {
		try {
			System.out.println("Start invoice remidner");
			invoiceService.sendRemindersForInvoices();
			System.out.println("End invoice reminder");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.announcement.confrere.query.reminder}")
	private void reminderConfrereForAnnouncementQuery() {
		try {
			System.out.println("Start reminder for confrere");
			announcementService.sendRemindersToConfrereForAnnouncement();
			System.out.println("End reminder for confrere");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.log.osiris.customer.proof.reading.reminder}")
	private void reminderClientReviewQuery() {
		try {
			System.out.println("Start proof reading reminder");
			announcementService.sendRemindersToCustomerForProofReading();
			System.out.println("End proof reading reminder");

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

	@Scheduled(cron = "${schedulling.competant.authorities.update}")
	private void updateCompetentAuthorities() {
		try {
			etablissementPublicsDelegate.updateCompetentAuthorities();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(cron = "${schedulling.owncloud.greffe.invoice.update}")
	private void updateOwncloudGreffeInvoices() {
		try {
			owncloudGreffeDelegate.grabAllFiles();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.guichet.unique.refresh.opened}")
	private void refreshAllOpenFormalities() {
		try {
			System.out.println("Start GU refresh opened");
			guichetUniqueDelegateService.refreshAllOpenFormalities();
			System.out.println("End GU refresh opened");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.guichet.unique.refresh.update.last.hour}")
	private void refreshFormalitiesFromLastHour() {
		try {
			System.out.println("Start GU refresh last hour");
			guichetUniqueDelegateService.refreshFormalitiesFromLastHour();
			System.out.println("End GU refresh last hour");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${schedulling.central.pay.payment.request.validation.check}")
	private void checkAllCentralPayPaymentRequests() throws OsirisException {
		try {
			System.out.println("Start payment validation check");
			centralPayPaymentRequestService.checkAllPaymentRequests();
			System.out.println("End payment validation check");
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

	@Scheduled(initialDelay = 500, fixedDelayString = "${azure.form.recognizer.invoice.check}")
	private void checkInvoiceToAnalyse() {
		try {
			System.out.println("Start azure invoice");
			azureInvoiceService.checkInvoiceToAnalyse();
			azureReceiptService.checkReceiptToAnalyse();
			System.out.println("End azure invoice");
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
			bodaccStatusService.updateBodaccStatusReferential();
			assignationTypeService.updateAssignationTypes();
			provisionScreenTypeService.updateScreenTypes();
		} catch (Exception e) {
			globalExceptionHandler.handleExceptionOsiris(e, null);
		}
	}

}
