package com.jss.osiris.modules.myjss.quotation.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.myjss.quotation.controller.model.DashboardUserStatistics;
import com.jss.osiris.modules.myjss.quotation.controller.model.MyJssImage;
import com.jss.osiris.modules.myjss.quotation.service.DashboardUserStatisticsService;
import com.jss.osiris.modules.myjss.quotation.service.MyJssQuotationDelegate;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.service.NewspaperService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.ReadingFolderService;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.crm.service.VoucherService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CountryService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.osiris.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.BuildingDomiciliation;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.osiris.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceTypeFieldTypePossibleValue;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.AssoServiceDocumentService;
import com.jss.osiris.modules.osiris.quotation.service.BuildingDomiciliationService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.DomiciliationContractTypeService;
import com.jss.osiris.modules.osiris.quotation.service.MailRedirectionTypeService;
import com.jss.osiris.modules.osiris.quotation.service.NoticeTypeFamilyService;
import com.jss.osiris.modules.osiris.quotation.service.NoticeTypeService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceFamilyGroupService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceFamilyService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceFieldTypeService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceTypeService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeDocumentService;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.BillingLabelTypeService;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MyJssQuotationController {

	private static final String inputEntryPoint = "/myjss/quotation";

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	QuotationService quotationService;

	@Autowired
	AssoAffaireOrderService assoAffaireOrderService;

	@Autowired
	MyJssQuotationValidationHelper myJssQuotationValidationHelper;

	@Autowired
	QuotationValidationHelper quotationValidationHelper;

	@Autowired
	ServiceService serviceService;

	@Autowired
	AttachmentService attachmentService;

	@Autowired
	TypeDocumentService typeDocumentService;

	@Autowired
	MailService mailService;

	@Autowired
	PhoneService phoneService;

	@Autowired
	AttachmentTypeService attachmentTypeService;

	@Autowired
	AssoServiceDocumentService assoServiceDocumentService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	AffaireService affaireService;

	@Autowired
	DocumentService documentService;

	@Autowired
	BillingLabelTypeService billingLabelTypeService;

	@Autowired
	ConstantService constantService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CustomerOrderCommentService customerOrderCommentService;

	@Autowired
	ResponsableService responsableService;

	@Autowired
	ServiceFamilyGroupService serviceFamilyGroupService;

	@Autowired
	ServiceFamilyService serviceFamilyService;

	@Autowired
	ServiceTypeService serviceTypeService;

	@Autowired
	CountryService countryService;

	@Autowired
	CityService cityService;

	@Autowired
	PricingHelper pricingHelper;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	NoticeTypeService noticeTypeService;

	@Autowired
	NoticeTypeFamilyService noticeTypeFamilyService;

	@Autowired
	CivilityService civilityService;

	@Autowired
	DashboardUserStatisticsService dashboardUserStatisticsService;

	@Autowired
	MyJssQuotationDelegate myJssQuotationDelegate;

	@Autowired
	DomiciliationContractTypeService contractTypeService;

	@Autowired
	LanguageService languageService;

	@Autowired
	MailRedirectionTypeService mailRedirectionTypeService;

	@Autowired
	BuildingDomiciliationService buildingDomiciliationService;

	@Autowired
	LegalFormService legalFormService;

	@Autowired
	PostService postService;

	@Autowired
	NewspaperService newspaperService;

	@Autowired
	ServiceFieldTypeService serviceFieldTypeService;

	@Autowired
	VoucherService voucherService;

	@Autowired
	ReadingFolderService readingFolderService;

	private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
	private final long rateLimit = 1000;
	private LocalDateTime lastFloodFlush = LocalDateTime.now();
	private int floodFlushDelayMinute = 1;

	private ResponseEntity<String> detectFlood(HttpServletRequest request) {
		if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
			requestCount.clear();

		String ipAddress = request.getRemoteAddr();
		AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

		if (count.incrementAndGet() > rateLimit) {
			return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
		}
		return null;
	}

	@GetMapping(inputEntryPoint + "/constants")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<String> getConstants(HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);
		return new ResponseEntity<String>(constantService.getConstantsForMyJss(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/search/current")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<CustomerOrder>> searchOrdersForCurrentUser(
			@RequestBody List<String> customerOrderStatus, @RequestParam boolean withMissingAttachment,
			@RequestParam Integer page, @RequestParam String sortBy,
			@RequestParam(required = false) List<Integer> responsableIdsToFilter)
			throws OsirisException {
		if (customerOrderStatus == null || customerOrderStatus.size() == 0)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("createdDateDesc") && !sortBy.equals("createdDateAsc")
				&& !sortBy.equals("statusAsc"))
			sortBy = "createdDateDesc";

		return new ResponseEntity<List<CustomerOrder>>(
				customerOrderService.searchOrdersForCurrentUser(customerOrderStatus, responsableIdsToFilter,
						withMissingAttachment, page,
						sortBy),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/search/affaire")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<CustomerOrder>> searchOrdersForCurrentUserAndAffaire(@RequestParam Integer idAffaire,
			HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);
		if (idAffaire == null)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		Affaire affaire = affaireService.getAffaire(idAffaire);
		if (affaire == null)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<CustomerOrder>>(
				customerOrderService.searchOrdersForCurrentUserAndAffaire(affaire), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/dashboard/user/statistics")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<DashboardUserStatistics> getDashboardUserStatistics()
			throws OsirisException {
		return new ResponseEntity<DashboardUserStatistics>(dashboardUserStatisticsService.getDashboardUserStatistics(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/search/current")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Quotation>> searchQuotationsForCurrentUser(
			@RequestParam(required = false) List<Integer> responsableIdsToFilter,
			@RequestBody List<String> quotationStatus, @RequestParam Integer page, @RequestParam String sortBy)
			throws OsirisClientMessageException {
		if (quotationStatus == null || quotationStatus.size() == 0)
			return new ResponseEntity<List<Quotation>>(new ArrayList<Quotation>(), HttpStatus.OK);

		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("createdDateDesc") && !sortBy.equals("createdDateAsc")
				&& !sortBy.equals("statusAsc"))
			sortBy = "createdDateDesc";

		return new ResponseEntity<List<Quotation>>(
				quotationService.searchQuotationsForCurrentUser(quotationStatus, responsableIdsToFilter, page, sortBy),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/asso")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<AssoAffaireOrder>> getAssoAffaireOrderForCustomerOrder(
			@RequestParam Integer idCustomerOrder)
			throws OsirisException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<List<AssoAffaireOrder>>(new ArrayList<AssoAffaireOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<AssoAffaireOrder>>(
				assoAffaireOrderService.getAssoAffaireOrderForCustomerOrder(customerOrder), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/asso")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<AssoAffaireOrder>> getAssoAffaireOrderForQuotation(
			@RequestParam Integer idQuotation)
			throws OsirisException {

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<List<AssoAffaireOrder>>(new ArrayList<AssoAffaireOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<AssoAffaireOrder>>(
				assoAffaireOrderService.getAssoAffaireOrderForQuotation(quotation), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<CustomerOrder> getCustomerOrder(@RequestParam Integer customerOrderId)
			throws OsirisException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);

		return new ResponseEntity<CustomerOrder>(
				customerOrderService.completeAdditionnalInformationForCustomerOrder(customerOrder, true),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/emergency")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> setEmergencyOnOrder(@RequestParam Integer orderId,
			@RequestParam Boolean isEmergency, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {

		detectFlood(request);

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(orderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		return new ResponseEntity<Boolean>(customerOrderService.setEmergencyOnOrder(customerOrder, isEmergency),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/document")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> setDocumentOnOrder(@RequestParam Integer orderId,
			@RequestBody Document document, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(orderId);

		document = quotationValidationHelper.validateDocument(document);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		return new ResponseEntity<Boolean>(customerOrderService.setDocumentOnOrder(customerOrder, document),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/emergency")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> setEmergencyOnQuotation(@RequestParam Integer quotationId,
			@RequestParam Boolean isEmergency, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		return new ResponseEntity<Boolean>(quotationService.setEmergencyOnQuotation(quotation, isEmergency),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/document")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> setDocumentOnQuotation(@RequestParam Integer quotationId,
			@RequestBody Document document, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		document = quotationValidationHelper.validateDocument(document);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		return new ResponseEntity<Boolean>(quotationService.setDocumentOnOrder(quotation, document),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Quotation> getQuotation(@RequestParam Integer quotationId)
			throws OsirisException {

		Quotation quotation = quotationService.getQuotation(quotationId);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Quotation>(new Quotation(), HttpStatus.OK);

		return new ResponseEntity<Quotation>(
				quotationService.completeAdditionnalInformationForQuotation(quotation, true),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service/provision/attachments")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Attachment>> getAttachmentsForProvisionOfService(@RequestParam Integer serviceId)
			throws OsirisException {

		Service service = serviceService.getService(serviceId);
		IQuotation quotation = service.getAssoAffaireOrder().getCustomerOrder();
		if (quotation == null)
			quotation = service.getAssoAffaireOrder().getQuotation();
		if (service == null || quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<List<Attachment>>(new ArrayList<Attachment>(), HttpStatus.OK);

		return new ResponseEntity<List<Attachment>>(serviceService.getAttachmentsForProvisionOfService(service),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachments/delete")
	public ResponseEntity<Boolean> deleteAttachment(@RequestParam Integer idAttachment)
			throws OsirisValidationException {

		Attachment attachment = attachmentService.getAttachment(idAttachment);
		if (attachment == null)
			throw new OsirisValidationException("service");

		if ((attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation() != null
				&& myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation()))
				|| (attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getCustomerOrder() != null
						&& myJssQuotationValidationHelper.canSeeQuotation(attachment.getAssoServiceDocument()
								.getService().getAssoAffaireOrder().getCustomerOrder()))) {

			attachmentService.definitivelyDeleteAttachment(attachment);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}

		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/update/date")
	public ResponseEntity<Boolean> updateAttachmentDocumentDate(@RequestParam Integer idAttachment,
			@RequestParam("attachmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attachmentDate)
			throws OsirisValidationException {

		Attachment attachment = attachmentService.getAttachment(idAttachment);
		if (attachment == null)
			throw new OsirisValidationException("service");

		if ((attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation() != null
				&& myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation()))
				|| (attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getCustomerOrder() != null
						&& myJssQuotationValidationHelper.canSeeQuotation(attachment.getAssoServiceDocument()
								.getService().getAssoAffaireOrder().getCustomerOrder()))) {

			attachment.setAttachmentDate(attachmentDate);
			attachmentService.addOrUpdateAttachment(attachment);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}

		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service/delete")
	public ResponseEntity<Boolean> deleteService(@RequestParam Integer idService)
			throws OsirisValidationException {

		Service service = serviceService.getService(idService);
		if (service == null)
			throw new OsirisValidationException("service");

		if ((service.getAssoAffaireOrder().getQuotation() != null
				&& myJssQuotationValidationHelper.canSeeQuotation(service.getAssoAffaireOrder().getQuotation()))
				|| (service.getAssoAffaireOrder().getCustomerOrder() != null && myJssQuotationValidationHelper
						.canSeeQuotation(service.getAssoAffaireOrder().getCustomerOrder()))) {
			serviceService.deleteServiceFromUser(service);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}

		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire/attachments")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Attachment>> getAttachmentsForAffaire(@RequestParam Integer idAffaire)
			throws OsirisException {

		if (idAffaire == null)
			return new ResponseEntity<List<Attachment>>(new ArrayList<Attachment>(), HttpStatus.OK);

		Affaire affaire = affaireService.getAffaire(idAffaire);
		if (affaire == null)
			return new ResponseEntity<List<Attachment>>(new ArrayList<Attachment>(), HttpStatus.OK);

		return new ResponseEntity<List<Attachment>>(affaireService.getAttachmentsForAffaire(affaire),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/download")
	@Transactional
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment)
			throws OsirisValidationException, OsirisException {

		byte[] data = null;
		HttpHeaders headers = null;
		Attachment attachment = attachmentService.getAttachment(idAttachment);

		boolean canDownload = true;
		if (attachment.getProvision() == null && attachment.getAssoServiceDocument() == null
				&& attachment.getCustomerOrder() == null && attachment.getTypeDocumentAttachment() == null)
			canDownload = false;

		if (attachment.getTypeDocumentAttachment() == null && employeeService.getCurrentMyJssUser() == null)
			canDownload = false;

		// Can only download invoice
		if (attachment.getCustomerOrder() != null && !attachment.getAttachmentType().getId()
				.equals(constantService.getAttachmentTypeInvoice().getId()))
			canDownload = false;

		if (attachment.getProvision() != null && attachment.getProvision().getService() != null
				&& attachment.getProvision().getService().getAssoAffaireOrder().getQuotation() != null
				&& attachment.getAttachmentType().getIsToSentOnUpload()
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getProvision().getService().getAssoAffaireOrder().getQuotation()))
			canDownload = false;

		if (attachment.getProvision() != null && attachment.getProvision().getService() != null
				&& attachment.getProvision().getService().getAssoAffaireOrder().getCustomerOrder() != null
				&& attachment.getAttachmentType().getIsToSentOnUpload()
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getProvision().getService().getAssoAffaireOrder().getCustomerOrder()))
			canDownload = false;

		if (attachment.getProvision() != null && attachment.getAssoServiceDocument() != null
				&& attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation()))
			canDownload = false;

		if (attachment.getProvision() != null && attachment.getAssoServiceDocument() != null
				&& attachment.getAssoServiceDocument().getService().getAssoAffaireOrder()
						.getCustomerOrder() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getCustomerOrder()))
			canDownload = false;

		if (!canDownload)
			return new ResponseEntity<byte[]>(null, headers, HttpStatus.OK);

		if (attachment == null || attachment.getUploadedFile() == null
				|| attachment.getUploadedFile().getPath() == null)
			throw new OsirisValidationException("tiersAttachment or UploadedFile or Path");

		File file = new File(attachment.getUploadedFile().getPath());

		if (file != null) {
			try {
				data = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}

			headers = new HttpHeaders();
			headers.add("filename", attachment.getUploadedFile().getFilename());
			headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
			headers.setContentLength(data.length);

			// Compute content type
			String mimeType = null;
			try {
				mimeType = Files.probeContentType(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}
			if (mimeType == null)
				mimeType = "application/octet-stream";
			headers.set("content-type", mimeType);
		}
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/invoice/download")
	@Transactional
	public ResponseEntity<byte[]> downloadAttachmentInvoice(@RequestParam("customerOrderId") Integer customerOrderId)
			throws OsirisValidationException, OsirisException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<byte[]>(null, new HttpHeaders(), HttpStatus.OK);

		if (customerOrder.getInvoices() != null)
			for (Invoice invoice : customerOrder.getInvoices())
				if ((invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId())
						|| invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()))
						&& invoice.getAttachments().size() > 0)
					return downloadAttachment(invoice.getAttachments().get(0).getId());

		return new ResponseEntity<byte[]>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/asso-service-document")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<AssoServiceDocument> getAssoServiceDocument(
			@RequestParam("idAssoServiceDocument") Integer idAssoServiceDocument)
			throws OsirisValidationException, OsirisException {

		AssoServiceDocument assoServiceDocument = assoServiceDocumentService
				.getAssoServiceDocument(idAssoServiceDocument);

		if (assoServiceDocument != null && assoServiceDocument.getService().getAssoAffaireOrder().getQuotation() != null
				&& myJssQuotationValidationHelper
						.canSeeQuotation(assoServiceDocument.getService().getAssoAffaireOrder().getQuotation()))
			return new ResponseEntity<AssoServiceDocument>(assoServiceDocument, new HttpHeaders(), HttpStatus.OK);

		if (assoServiceDocument != null
				&& assoServiceDocument.getService().getAssoAffaireOrder().getCustomerOrder() != null
				&& myJssQuotationValidationHelper
						.canSeeQuotation(assoServiceDocument.getService().getAssoAffaireOrder().getCustomerOrder()))
			return new ResponseEntity<AssoServiceDocument>(assoServiceDocument, new HttpHeaders(), HttpStatus.OK);

		return new ResponseEntity<AssoServiceDocument>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/set-date")
	public ResponseEntity<Boolean> modifyAttachmentDate(
			@RequestParam("attachmentDate") LocalDate attachmentDate,
			@RequestParam("idAttachment") Integer idAttachment) {

		boolean canModify = false;

		Attachment attachmentToModify = attachmentService.getAttachment(idAttachment);

		if (attachmentDate != null && attachmentToModify != null && attachmentToModify.getQuotation() != null
				&& myJssQuotationValidationHelper.canSeeQuotation(attachmentToModify.getQuotation())) {
			attachmentService.modifyAttachmentDate(attachmentDate, idAttachment);
			canModify = true;
		}

		return new ResponseEntity<Boolean>(canModify, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/payment/cb/qrcode")
	@Transactional
	public ResponseEntity<MyJssImage> downloadQrCodeForOrderPayment(
			@RequestParam("customerOrderId") Integer customerOrderId,
			@RequestParam("mail") String mail)
			throws OsirisValidationException, OsirisException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<MyJssImage>(null, new HttpHeaders(), HttpStatus.OK);

		validationHelper.validateMail(mail);

		return new ResponseEntity<MyJssImage>(paymentService.downloadQrCodeForOrderPayment(customerOrder, mail),
				new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/payment/cb/qrcode")
	@Transactional
	public ResponseEntity<MyJssImage> downloadQrCodeForQuotationPayment(
			@RequestParam("quotationId") Integer quotationId,
			@RequestParam("mail") String mail)
			throws OsirisValidationException, OsirisException {

		Quotation quotation = quotationService.getQuotation(quotationId);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<MyJssImage>(null, new HttpHeaders(), HttpStatus.OK);

		validationHelper.validateMail(mail);

		return new ResponseEntity<MyJssImage>(paymentService.downloadQrCodeForQuotationPayment(quotation, mail),
				new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/attachment/disabled")
	public ResponseEntity<Boolean> disableDocument(@RequestParam Integer idAttachment)
			throws OsirisValidationException {
		if (idAttachment == null)
			throw new OsirisValidationException("idAttachment");

		Attachment attachment = attachmentService.getAttachment(idAttachment);
		if (attachment == null)
			throw new OsirisValidationException("attachment");

		boolean canDisabled = true;
		if (attachment.getAssoServiceDocument() == null)
			canDisabled = false;

		if (attachment.getProvision() != null && attachment.getAssoServiceDocument() != null
				&& attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation()))
			canDisabled = false;

		if (attachment.getProvision() != null && attachment.getAssoServiceDocument() != null
				&& attachment.getAssoServiceDocument().getService().getAssoAffaireOrder()
						.getCustomerOrder() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						attachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getCustomerOrder()))
			canDisabled = false;

		if (!canDisabled)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		attachmentService.disableAttachment(attachment);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/attachment/upload")
	public ResponseEntity<List<Integer>> uploadAttachment(@RequestParam MultipartFile file,
			@RequestParam(required = false) Integer idEntity,
			@RequestParam String entityType,
			@RequestParam Integer idAttachmentType,
			@RequestParam String filename,
			@RequestParam(name = "typeDocumentCode", required = false) String typeDocumentCode)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
		if (idAttachmentType == null)
			throw new OsirisValidationException("idAttachmentType");

		AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

		if (attachmentType == null)
			throw new OsirisValidationException("attachmentType");

		if (filename == null || filename.equals(""))
			throw new OsirisValidationException("filename");

		if (idEntity == null)
			throw new OsirisValidationException("idEntity or codeEntity");

		if (entityType == null)
			throw new OsirisValidationException("entityType");

		TypeDocument typeDocument = null;
		if (typeDocumentCode != null) {
			typeDocument = typeDocumentService.getTypeDocumentByCode(typeDocumentCode);
			if (typeDocument == null)
				throw new OsirisValidationException("typeDocument");
		}

		boolean canUpload = true;
		AssoServiceDocument assoServiceDocument = assoServiceDocumentService.getAssoServiceDocument(idEntity);

		if (entityType.equals(AssoServiceDocument.class.getSimpleName())) {
			if (assoServiceDocument == null) {
				canUpload = false;
			} else {
				if (assoServiceDocument.getService().getAssoAffaireOrder().getQuotation() != null
						&& !myJssQuotationValidationHelper
								.canSeeQuotation(assoServiceDocument.getService().getAssoAffaireOrder().getQuotation()))
					canUpload = false;

				if (assoServiceDocument.getService().getAssoAffaireOrder().getQuotation() != null) {
					String quotationStatusCode = assoServiceDocument.getService().getAssoAffaireOrder().getQuotation()
							.getQuotationStatus().getCode();
					if (quotationStatusCode.equals(QuotationStatus.ABANDONED)
							|| quotationStatusCode.equals(QuotationStatus.REFUSED_BY_CUSTOMER)
							|| quotationStatusCode.equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
						canUpload = false;
				}

				if (assoServiceDocument.getService().getAssoAffaireOrder()
						.getCustomerOrder() != null
						&& !myJssQuotationValidationHelper.canSeeQuotation(
								assoServiceDocument.getService().getAssoAffaireOrder().getCustomerOrder()))
					canUpload = false;
			}
		} else if (entityType.equals(Candidacy.class.getSimpleName())) {
			canUpload = true;
		} else {
			canUpload = false;
		}

		if (!canUpload)
			return new ResponseEntity<List<Integer>>(new ArrayList<>(), HttpStatus.OK);

		if (!entityType.equals(AssoServiceDocument.class.getSimpleName())
				&& !entityType.equals(Candidacy.class.getSimpleName()))
			throw new OsirisValidationException("entityType");

		List<Attachment> createdAttachments = attachmentService.addAttachment(file, idEntity, null, entityType,
				attachmentType, filename,
				false, null, typeDocument);

		return new ResponseEntity<List<Integer>>(createdAttachments.stream().map(Attachment::getId).toList(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Affaire> getAffaire(@RequestParam Integer id, HttpServletRequest request)
			throws OsirisValidationException {
		detectFlood(request);
		if (id == null)
			throw new OsirisValidationException("id");

		return new ResponseEntity<Affaire>(affaireService.getAffaire(id), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire/siret")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Affaire>> getAffaireBySiretOrSiren(@RequestParam String siretOrSiren,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		if (siretOrSiren == null)
			throw new OsirisValidationException("id");

		if (siretOrSiren == null || !validationHelper.validateSiret(siretOrSiren.trim().replaceAll(" ", ""))
				&& !validationHelper.validateSiren(siretOrSiren.trim().replaceAll(" ", "")))
			return new ResponseEntity<Page<Affaire>>(Page.empty(), HttpStatus.OK);

		List<Affaire> affaires = affaireService
				.getAffairesFromSiretFromWebsite(siretOrSiren.trim().replaceAll(" ", ""));

		PageRequest newPageRequest = PageRequest.of(0, 50);

		if (affaires == null || affaires.size() == 0)
			return new ResponseEntity<>(null, HttpStatus.OK);

		Page<Affaire> pageResult = new PageImpl<>(affaires, newPageRequest, affaires.size());
		return new ResponseEntity<Page<Affaire>>(pageResult, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/affaire")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> addOrUpdateAffaire(@RequestBody Affaire affaire)
			throws OsirisValidationException, OsirisException, OsirisDuplicateException {

		Affaire affaireOriginal = affaireService.getAffaire(affaire.getId());

		if (affaireOriginal == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		// Customer can only update IBAN / BIC and mails / phone
		validationHelper.validateBic(affaire.getPaymentBic(), false, "BIC");
		validationHelper.validateIban(affaire.getPaymentIban(), false, "IBAN");
		validationHelper.validateMailList(affaire.getMails());
		validationHelper.validatePhoneList(affaire.getPhones());

		affaireOriginal.setPaymentBic(affaire.getPaymentBic());
		affaireOriginal.setPaymentIban(affaire.getPaymentIban());
		affaireOriginal.setMails(affaire.getMails());
		affaireOriginal.setPhones(affaire.getPhones());

		affaireService.addOrUpdateAffaire(affaireOriginal);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Document>> getDocumentForCustomerOrder(@RequestParam Integer idCustomerOrder)
			throws OsirisValidationException {
		if (idCustomerOrder == null)
			throw new OsirisValidationException("id");

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<List<Document>>(new ArrayList<Document>(), HttpStatus.OK);

		return new ResponseEntity<List<Document>>(customerOrder.getDocuments(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> addOrUpdateDocumentsForCustomerOrder(@RequestBody List<Document> documents)
			throws OsirisException {
		if (documents == null)
			throw new OsirisValidationException("documents");

		Integer idCustomerOrder = null;

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());

			if (currentDocument == null || currentDocument.getCustomerOrder() == null
					|| idCustomerOrder != null && !idCustomerOrder.equals(currentDocument.getCustomerOrder().getId()))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			idCustomerOrder = currentDocument.getCustomerOrder().getId();
		}

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());
			if (currentDocument.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypeDigital().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypePaper().getId())) {
				currentDocument.setIsRecipientClient(document.getIsRecipientClient());
				currentDocument.setIsRecipientAffaire(document.getIsRecipientAffaire());
				currentDocument.setMailsAffaire(document.getMailsAffaire());
				currentDocument.setMailsClient(document.getMailsClient());
				currentDocument.setAddToAffaireMailList(document.getAddToAffaireMailList());
				currentDocument.setAddToClientMailList(document.getAddToClientMailList());
				currentDocument.setBillingLabelType(document.getBillingLabelType());
				currentDocument.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
				currentDocument.setCommandNumber(document.getCommandNumber());
				currentDocument.setExternalReference(document.getExternalReference());
				documentService.addOrUpdateDocument(currentDocument);
			}
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/billing-label-types")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<BillingLabelType>> getBillingLabels() {
		return new ResponseEntity<List<BillingLabelType>>(billingLabelTypeService.getBillingLabelTypes(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Document>> getDocumentForQuotation(@RequestParam Integer idQuotation)
			throws OsirisValidationException {
		if (idQuotation == null)
			throw new OsirisValidationException("id");

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<List<Document>>(new ArrayList<Document>(), HttpStatus.OK);

		return new ResponseEntity<List<Document>>(quotation.getDocuments(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> addOrUpdateDocumentsForQuotation(@RequestBody List<Document> documents)
			throws OsirisException {
		if (documents == null)
			throw new OsirisValidationException("documents");

		Integer idQuotation = null;

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());

			if (currentDocument == null || currentDocument.getQuotation() == null
					|| idQuotation != null && !idQuotation.equals(currentDocument.getQuotation().getId()))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			idQuotation = currentDocument.getQuotation().getId();
		}

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		String quotationStatusCode = quotation.getQuotationStatus().getCode();
		if (quotationStatusCode.equals(QuotationStatus.ABANDONED)
				|| quotationStatusCode.equals(QuotationStatus.REFUSED_BY_CUSTOMER)
				|| quotationStatusCode.equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());
			if (currentDocument.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypeDigital().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypePaper().getId())) {
				currentDocument.setIsRecipientClient(document.getIsRecipientClient());
				currentDocument.setIsRecipientAffaire(document.getIsRecipientAffaire());
				currentDocument.setMailsAffaire(document.getMailsAffaire());
				currentDocument.setMailsClient(document.getMailsClient());
				currentDocument.setAddToAffaireMailList(document.getAddToAffaireMailList());
				currentDocument.setAddToClientMailList(document.getAddToClientMailList());
				currentDocument.setBillingLabelType(document.getBillingLabelType());
				currentDocument.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
				currentDocument.setCommandNumber(document.getCommandNumber());
				currentDocument.setExternalReference(document.getExternalReference());
				documentService.addOrUpdateDocument(currentDocument);
			}
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/responsable/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Document>> getDocumentForResonsable(@RequestParam Integer idResponsable)
			throws OsirisValidationException {
		if (idResponsable == null)
			throw new OsirisValidationException("id");

		Responsable responsable = responsableService.getResponsable(idResponsable);
		if (responsable == null || !myJssQuotationValidationHelper.canSeeResponsable(responsable))
			return new ResponseEntity<List<Document>>(new ArrayList<Document>(), HttpStatus.OK);

		return new ResponseEntity<List<Document>>(responsable.getDocuments(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/responsable/documents")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> addOrUpdateDocumentsForResponsable(@RequestBody List<Document> documents)
			throws OsirisException {
		if (documents == null)
			throw new OsirisValidationException("documents");

		Integer idResponsable = null;

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());

			if (currentDocument == null || currentDocument.getResponsable() == null
					|| idResponsable != null && !idResponsable.equals(currentDocument.getResponsable().getId()))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			idResponsable = currentDocument.getResponsable().getId();
		}

		Responsable responsable = responsableService.getResponsable(idResponsable);
		if (responsable == null || !myJssQuotationValidationHelper.canSeeResponsable(responsable))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		for (Document document : documents) {
			Document currentDocument = documentService.getDocument(document.getId());
			if (currentDocument.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypeDigital().getId())
					|| currentDocument.getDocumentType().getId()
							.equals(constantService.getDocumentTypePaper().getId())) {
				currentDocument.setIsRecipientClient(document.getIsRecipientClient());
				currentDocument.setIsRecipientAffaire(document.getIsRecipientAffaire());
				currentDocument.setMailsAffaire(document.getMailsAffaire());
				currentDocument.setMailsClient(document.getMailsClient());
				currentDocument.setReminderMail(mailService.populateMailId(document.getReminderMail()));
				currentDocument.setAddToAffaireMailList(document.getAddToAffaireMailList());
				currentDocument.setAddToClientMailList(document.getAddToClientMailList());
				currentDocument.setBillingLabelType(document.getBillingLabelType());
				currentDocument.setIsCommandNumberMandatory(document.getIsCommandNumberMandatory());
				currentDocument.setCommandNumber(document.getCommandNumber());
				currentDocument.setExternalReference(document.getExternalReference());
				documentService.addOrUpdateDocument(currentDocument);
			}
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/customer-order-comments/customer")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<CustomerOrderComment>> getCustomerOrderCommentsForCustomer(
			@RequestParam Integer idCustomerOrder)
			throws OsirisValidationException {
		if (idCustomerOrder == null)
			throw new OsirisValidationException("id");

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<List<CustomerOrderComment>>(new ArrayList<CustomerOrderComment>(), HttpStatus.OK);

		return new ResponseEntity<List<CustomerOrderComment>>(
				customerOrderService.getCustomerOrderCommentsForCustomer(customerOrder),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/customer-order-comment")
	public ResponseEntity<Boolean> addOrUpdateCustomerOrderComment(
			@RequestBody CustomerOrderComment customerOrderComment) throws OsirisValidationException, OsirisException {
		CustomerOrderComment customerOrderCommentOriginal = null;

		if (customerOrderComment.getCustomerOrder() == null
				|| !myJssQuotationValidationHelper.canSeeQuotation(customerOrderComment.getCustomerOrder()))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		if (customerOrderComment.getId() != null) {
			if (customerOrderComment.getCurrentCustomer() == null || !customerOrderComment.getCurrentCustomer().getId()
					.equals(employeeService.getCurrentMyJssUser().getId()))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			customerOrderCommentOriginal = (CustomerOrderComment) validationHelper.validateReferential(
					customerOrderComment,
					true, "customerOrderComments");
			customerOrderComment.setProvision(customerOrderCommentOriginal.getProvision());
			customerOrderComment.setCustomerOrder(customerOrderCommentOriginal.getCustomerOrder());
			customerOrderComment.setQuotation(customerOrderCommentOriginal.getQuotation());
		}

		if (customerOrderComment.getId() == null) {
			customerOrderCommentService.createCustomerOrderComment(customerOrderComment.getCustomerOrder(),
					customerOrderComment.getComment(), false, true);
		} else if (customerOrderCommentOriginal != null) {
			customerOrderComment.setCreatedDateTime(customerOrderCommentOriginal.getCreatedDateTime());
			customerOrderCommentService.addOrUpdateCustomerOrderComment(customerOrderComment);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire/search/current")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Affaire>> getAffairesForCurrentUser(@RequestParam Integer page,
			@RequestParam String sortBy, @RequestParam String searchText,
			@RequestParam(required = false) List<Integer> responsableIdsToFilter) {
		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("nameAsc") && !sortBy.equals("nameDesc"))
			sortBy = "nameAsc";

		return new ResponseEntity<List<Affaire>>(
				affaireService.getAffairesForCurrentUser(responsableIdsToFilter, page, sortBy, searchText),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/order")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<CustomerOrder> getCustomerOrderForQuotation(@RequestParam Integer idQuotation)
			throws OsirisValidationException {
		if (idQuotation == null)
			throw new OsirisValidationException("id");

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation)
				|| quotation.getCustomerOrders() == null || quotation.getCustomerOrders().size() == 0)
			return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);

		return new ResponseEntity<CustomerOrder>(quotation.getCustomerOrders().get(0), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/quotation")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Quotation> getQuotationForCustomerOrder(@RequestParam Integer idCustomerOrder)
			throws OsirisValidationException {
		if (idCustomerOrder == null)
			throw new OsirisValidationException("id");

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder)
				|| customerOrder.getQuotations() == null || customerOrder.getQuotations().size() == 0)
			return new ResponseEntity<Quotation>(new Quotation(), HttpStatus.OK);

		return new ResponseEntity<Quotation>(customerOrder.getQuotations().get(0), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/service")
	public ResponseEntity<Boolean> addOrUpdateService(@RequestBody Service service) throws OsirisException {
		if (service == null || service.getId() == null || service.getAssoServiceFieldTypes() == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		Service serviceFetched = serviceService.getService(service.getId());

		if (serviceFetched.getAssoAffaireOrder().getCustomerOrder() == null
				&& serviceFetched.getAssoAffaireOrder().getQuotation() == null
				|| !myJssQuotationValidationHelper
						.canSeeQuotation(serviceFetched.getAssoAffaireOrder().getCustomerOrder() != null
								? serviceFetched.getAssoAffaireOrder().getCustomerOrder()
								: serviceFetched.getAssoAffaireOrder().getQuotation()))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		boolean canUpdateProvision = false;
		if (serviceFetched.getAssoAffaireOrder().getQuotation() != null) {
			String quotationStatusCode = serviceFetched.getAssoAffaireOrder().getQuotation().getQuotationStatus()
					.getCode();
			if (quotationStatusCode.equals(QuotationStatus.ABANDONED)
					|| quotationStatusCode.equals(QuotationStatus.REFUSED_BY_CUSTOMER)
					|| quotationStatusCode.equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			if (quotationStatusCode.equals(QuotationStatus.DRAFT))
				canUpdateProvision = true;
		}

		if (serviceFetched.getAssoAffaireOrder().getCustomerOrder() != null) {
			String orderStatusCode = serviceFetched.getAssoAffaireOrder().getCustomerOrder().getCustomerOrderStatus()
					.getCode();
			if (orderStatusCode.equals(CustomerOrderStatus.ABANDONED)
					|| orderStatusCode.equals(CustomerOrderStatus.TO_BILLED)
					|| orderStatusCode.equals(CustomerOrderStatus.BILLED))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);

			if (orderStatusCode.equals(CustomerOrderStatus.DRAFT))
				canUpdateProvision = true;
		}

		for (AssoServiceFieldType assoServiceFieldTypeOld : serviceFetched.getAssoServiceFieldTypes()) {
			assoServiceFieldTypeOld.setService(serviceFetched);
			for (AssoServiceFieldType assoServiceFieldType : service.getAssoServiceFieldTypes()) {
				if (assoServiceFieldType.getId().equals(assoServiceFieldTypeOld.getId())) {
					if (assoServiceFieldType.getServiceFieldType().getDataType()
							.equals(ServiceFieldType.SERVICE_FIELD_TYPE_DATE)) {
						validationHelper.validateDate(assoServiceFieldType.getDateValue(), false,
								assoServiceFieldType.getServiceFieldType().getLabel());
						assoServiceFieldTypeOld.setDateValue(assoServiceFieldType.getDateValue());
					}
					if (assoServiceFieldType.getServiceFieldType().getDataType()
							.equals(ServiceFieldType.SERVICE_FIELD_TYPE_INTEGER)) {
						validationHelper.validateInteger(assoServiceFieldType.getIntegerValue(), false,
								assoServiceFieldType.getServiceFieldType().getLabel());
						assoServiceFieldTypeOld.setIntegerValue(assoServiceFieldType.getIntegerValue());
					}
					if (assoServiceFieldType.getServiceFieldType().getDataType()
							.equals(ServiceFieldType.SERVICE_FIELD_TYPE_TEXT)) {
						validationHelper.validateString(assoServiceFieldType.getStringValue(), false, 250,
								assoServiceFieldType.getServiceFieldType().getLabel());
						assoServiceFieldTypeOld.setStringValue(assoServiceFieldType.getStringValue());
					}
					if (assoServiceFieldType.getServiceFieldType().getDataType()
							.equals(ServiceFieldType.SERVICE_FIELD_TYPE_TEXTAREA)) {
						assoServiceFieldTypeOld.setTextAreaValue(assoServiceFieldType.getTextAreaValue());
					}
					if (assoServiceFieldType.getServiceFieldType().getDataType()
							.equals(ServiceFieldType.SERVICE_FIELD_TYPE_SELECT)
							&& assoServiceFieldType.getSelectValue() != null) {
						boolean found = false;
						for (ServiceTypeFieldTypePossibleValue serviceTypeFieldTypePossibleValue : assoServiceFieldType
								.getServiceFieldType().getServiceFieldTypePossibleValues()) {
							if (serviceTypeFieldTypePossibleValue.getId()
									.equals(assoServiceFieldType.getSelectValue().getId())) {
								found = true;
								break;
							}
						}
						if (!found)
							throw new OsirisValidationException(assoServiceFieldType.getServiceFieldType().getLabel());
						assoServiceFieldTypeOld.setSelectValue(assoServiceFieldType.getSelectValue());
					}
				}
			}
		}

		if (canUpdateProvision) {
			for (Provision provision : serviceFetched.getProvisions()) {
				for (Provision provisionIn : service.getProvisions()) {
					IWorkflowElement status = getProvisionStatus(provision);
					if (status != null && !status.getIsOpenState())
						return new ResponseEntity<Boolean>(false, HttpStatus.OK);

					if (provision.getId().equals(provisionIn.getId())) {
						if (provision.getAnnouncement() != null) {
							provision.getAnnouncement().setNotice(provisionIn.getAnnouncement().getNotice());
							provision.getAnnouncement().setDepartment(provisionIn.getAnnouncement().getDepartment());
							provision.getAnnouncement().setNoticeTypes(provisionIn.getAnnouncement().getNoticeTypes());
							provision.getAnnouncement()
									.setPublicationDate(provisionIn.getAnnouncement().getPublicationDate());
							provision.getAnnouncement()
									.setNoticeTypeFamily(provisionIn.getAnnouncement().getNoticeTypeFamily());
							provision.setIsRedactedByJss(provisionIn.getIsRedactedByJss());
							provision.getAnnouncement()
									.setIsProofReadingDocument(
											provisionIn.getAnnouncement().getIsProofReadingDocument());
						}
						if (provisionIn.getDomiciliation() != null) {
							if (provisionIn.getDomiciliation().getMails() != null)
								mailService.populateMailIds(provisionIn.getDomiciliation().getMails());
							if (provisionIn.getDomiciliation().getLegalGardianMails() != null)
								mailService.populateMailIds(provisionIn.getDomiciliation().getLegalGardianMails());
							if (provisionIn.getDomiciliation().getLegalGardianPhones() != null)
								phoneService.populatePhoneIds(provisionIn.getDomiciliation().getLegalGardianPhones());
							provision.setDomiciliation(provisionIn.getDomiciliation());
						}
					}
				}
			}
		}

		serviceService.addOrUpdateServiceFromUser(serviceFetched);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	private IWorkflowElement getProvisionStatus(Provision provision) {
		if (provision.getAnnouncement() != null)
			return provision.getAnnouncement().getAnnouncementStatus();
		if (provision.getDomiciliation() != null)
			return provision.getDomiciliation().getDomiciliationStatus();
		if (provision.getFormalite() != null)
			return provision.getFormalite().getFormaliteStatus();
		if (provision.getSimpleProvision() != null)
			return provision.getSimpleProvision().getSimpleProvisionStatus();
		return null;
	}

	@GetMapping(inputEntryPoint + "/services")
	public ResponseEntity<Boolean> addOrUpdateServices(@RequestParam List<Integer> serviceTypeIds,
			@RequestParam("affaireOrderId") Integer affaireOrderId)
			throws OsirisException {

		AssoAffaireOrder assoAffaireOrder = null;

		if (affaireOrderId != null) {
			assoAffaireOrder = assoAffaireOrderService.getAssoAffaireOrder(affaireOrderId);
		}

		List<ServiceType> serviceTypes = new ArrayList<ServiceType>();
		if (serviceTypeIds != null) {
			for (Integer serviceTypeId : serviceTypeIds) {
				ServiceType serviceType = serviceTypeService.getServiceType(serviceTypeId);
				if (serviceType == null)
					throw new OsirisValidationException("servicesType");
				serviceTypes.add(serviceType);
			}
		} else {
			throw new OsirisValidationException("servicesTypeIds");
		}

		if (assoAffaireOrder != null) {
			serviceService.addOrUpdateServices(serviceTypes, affaireOrderId, null, assoAffaireOrder.getAffaire());

			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-family-groups")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<ServiceFamilyGroup>> getServiceFamilyGroups(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<ServiceFamilyGroup>>(serviceFamilyGroupService.getServiceFamilyGroups(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-families/service-group")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<ServiceFamily>> getServiceFamiliesForFamilyGroup(Integer idServiceFamilyGroup,
			HttpServletRequest request) {
		detectFlood(request);

		ServiceFamilyGroup serviceFamilyGroup = serviceFamilyGroupService.getServiceFamilyGroup(idServiceFamilyGroup);
		if (serviceFamilyGroup == null)
			return new ResponseEntity<List<ServiceFamily>>(new ArrayList<ServiceFamily>(), HttpStatus.OK);
		return new ResponseEntity<List<ServiceFamily>>(
				serviceFamilyService.getServiceFamiliesForFamilyGroup(serviceFamilyGroup),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-families/mandatory-documents")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<ServiceFamily>> getServiceFamiliesForMandatoryDocuments(
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);

		return new ResponseEntity<List<ServiceFamily>>(
				serviceFamilyService.getServiceFamiliesForMandatoryDocuments(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-type/service-family")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<ServiceType>> getServiceTypesForFamily(Integer idServiceFamily,
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);

		ServiceFamily serviceFamily = serviceFamilyService.getServiceFamily(idServiceFamily);
		if (serviceFamily == null)
			return new ResponseEntity<List<ServiceType>>(new ArrayList<ServiceType>(), HttpStatus.OK);
		return new ResponseEntity<List<ServiceType>>(serviceTypeService.getServiceTypesForFamilyForMyJss(serviceFamily),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-type")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<ServiceType> getServiceType(
			@RequestParam("serviceTypeId") Integer serviceTypeId,
			@RequestParam("isFetchOnlyMandatoryDocuments") Boolean isFetchOnlyMandatoryDocuments,
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);

		if (serviceTypeId == null)
			return new ResponseEntity<ServiceType>(new ServiceType(), HttpStatus.OK);

		return new ResponseEntity<ServiceType>(
				serviceTypeService.getServiceType(serviceTypeId, isFetchOnlyMandatoryDocuments),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/countries")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Country>> getCountries(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<Country>>(countryService.getCountries(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities/search/name/country/postal-code")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<City>> getCitiesByNameAndCountryAndPostalCode(@RequestParam String name,
			@RequestParam Integer countryId, @RequestParam String postalCode, @RequestParam Integer page,
			@RequestParam Integer size, HttpServletRequest request) {
		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.ASC, "label"));

		Country country = countryService.getCountry(countryId);
		if (country == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<City>>(
				cityService.getCitiesByLabelAndCountryAndPostalCode(name, countryId, postalCode, pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities/search/country")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<City>> getCitiesByCountry(@RequestParam Integer countryId, HttpServletRequest request) {
		detectFlood(request);
		Country country = countryService.getCountry(countryId);
		if (country == null)
			return new ResponseEntity<List<City>>(new ArrayList<City>(), HttpStatus.OK);

		return new ResponseEntity<List<City>>(cityService.getCitiesByCountry(countryId),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/cities/search/postal-code")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<City>> getCitiesByPostalCode(@RequestParam String postalCode, HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);

		return new ResponseEntity<List<City>>(
				cityService.getCitiesByCountryAndPostalCode(constantService.getCountryFrance().getId(), postalCode),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/departments")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Department>> getDepartments(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<Department>>(departmentService.getDepartments(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/civilities")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Civility>> getCivilities(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<Civility>>(civilityService.getCivilities(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/notice-types")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<NoticeType>> getNoticeTypes(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<NoticeType>>(noticeTypeService.getNoticeTypes(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/notice-type-families")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<NoticeTypeFamily>> getNoticeTypeFamilies(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<NoticeTypeFamily>>(noticeTypeFamilyService.getNoticeTypeFamilies(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/pricing")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<CustomerOrder> completePricingOfOrder(@RequestBody CustomerOrder customerOrder,
			@RequestParam Boolean isEmergency, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (customerOrder.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<CustomerOrder>(null);

		return new ResponseEntity<CustomerOrder>(
				customerOrderService.completeAdditionnalInformationForCustomerOrder(
						(CustomerOrder) pricingHelper.completePricingOfIQuotation(customerOrder, isEmergency), true),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/pricing")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Quotation> completePricingOfQuotation(@RequestBody Quotation quotation,
			@RequestParam Boolean isEmergency, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Quotation>(null);

		return new ResponseEntity<Quotation>(quotationService.completeAdditionnalInformationForQuotation(
				(Quotation) pricingHelper.completePricingOfIQuotation(quotation, isEmergency), true), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/save-order")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Quotation> saveQuotation(@RequestBody Quotation quotation, @RequestParam Boolean isValidation,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Quotation>(null);

		return new ResponseEntity<Quotation>(
				(Quotation) myJssQuotationDelegate.validateAndCreateQuotation(quotation, isValidation, request),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/switch/responsable")
	public ResponseEntity<Boolean> switchResponsableForQuotation(Integer idQuotation, Integer newResponsable,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		Responsable responsable = responsableService.getResponsable(newResponsable);
		if (responsable == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		quotationService.switchResponsable(quotation, responsable);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/switch/responsable")
	public ResponseEntity<Boolean> switchResponsableForOrder(Integer idOrder, Integer newResponsable,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		CustomerOrder order = customerOrderService.getCustomerOrder(idOrder);
		if (order == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		Responsable responsable = responsableService.getResponsable(newResponsable);
		if (responsable == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		customerOrderService.switchResponsable(order, responsable);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/save-order")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<CustomerOrder> saveCutomerOrder(@RequestBody CustomerOrder order,
			@RequestParam Boolean isValidation,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (order.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(order))
			return new ResponseEntity<CustomerOrder>(null);

		return new ResponseEntity<CustomerOrder>(
				(CustomerOrder) myJssQuotationDelegate.validateAndCreateQuotation(order, isValidation, request),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/order/user/save")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Integer> saveCustomerOrderFromMyJss(@RequestBody CustomerOrder order,
			@RequestParam Boolean isValidation,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (order.getAssoAffaireOrders() == null || order.getAssoAffaireOrders().size() == 0)
			throw new OsirisValidationException("assoAffaireOrders");

		for (AssoAffaireOrder asso : order.getAssoAffaireOrders()) {
			if (asso.getAffaire() == null)
				throw new OsirisValidationException("affaire");

			if (asso.getAffaire().getId() != null) {
				validationHelper.validateReferential(asso.getAffaire(), true, "affaire");
			} else {
				myJssQuotationValidationHelper.validateAffaire(asso.getAffaire());
			}
		}
		return new ResponseEntity<Integer>(
				myJssQuotationDelegate.saveCustomerOrderFromMyJss(order, isValidation, request).getId(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/user/save")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Integer> saveQuotationFromMyJss(@RequestBody Quotation order,
			@RequestParam Boolean isValidation,
			HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (order.getAssoAffaireOrders() == null || order.getAssoAffaireOrders().size() == 0)
			throw new OsirisValidationException("assoAffaireOrders");

		for (AssoAffaireOrder asso : order.getAssoAffaireOrders()) {
			if (asso.getAffaire() == null)
				throw new OsirisValidationException("affaire");

			if (asso.getAffaire().getId() != null) {
				validationHelper.validateReferential(asso.getAffaire(), true, "affaire");
			} else {
				myJssQuotationValidationHelper.validateAffaire(asso.getAffaire());
			}
		}
		return new ResponseEntity<Integer>(
				myJssQuotationDelegate.saveQuotationFromMyJss(order, isValidation, request).getId(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/is-deposit-mandatory")
	public ResponseEntity<Boolean> isDepositMandatory(@RequestParam Integer quotationId,
			HttpServletRequest request) throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		Quotation quotation = null;
		quotation = quotationService.getQuotation(quotationId);

		if (quotation == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(null);

		return new ResponseEntity<Boolean>(quotationService.isDepositMandatory(quotation),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/voucher/delete/order")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> removeVoucherCustomerOrder(@RequestParam Integer customerOrderId,
			HttpServletRequest request) throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		CustomerOrder customerOrder = null;
		customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || customerOrder.getVoucher() == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		if (customerOrder.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Boolean>(null);

		return new ResponseEntity<Boolean>(voucherService.deleteVoucheredPriceOnIQuotation(customerOrder),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/voucher/delete/quotation")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> removeVoucherQuotation(@RequestParam Integer quotationId,
			HttpServletRequest request) throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		Quotation quotation = null;
		quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || quotation.getVoucher() == null)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(null);

		return new ResponseEntity<Boolean>(
				voucherService.deleteVoucheredPriceOnIQuotation(quotation),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/voucher/order-user/apply")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Voucher> checkAndApplyVoucherOnOrder(@RequestParam Integer customerOrderId,
			@RequestParam String voucherCode,
			HttpServletRequest request) throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		CustomerOrder customerOrder = null;
		customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null)
			throw new OsirisValidationException("customerOrder");

		if (customerOrder.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Voucher>(null);

		Voucher voucher = null;
		voucher = voucherService.getVoucherByCode(voucherCode);

		if (voucherCode == null)
			throw new OsirisValidationException("voucher");

		return new ResponseEntity<Voucher>(
				voucherService.checkVoucherValidity(customerOrder, voucher),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/voucher/order/apply")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Voucher> checkAndApplyVoucherOnOrder(@RequestBody CustomerOrder customerOrder,
			@RequestParam String voucherCode, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (voucherCode == null)
			throw new OsirisValidationException("voucherCode");

		Voucher voucher = voucherService.getVoucherByCode(voucherCode);
		if (voucher == null)
			throw new OsirisValidationException("voucher");

		return new ResponseEntity<Voucher>(
				voucherService.checkVoucherValidity(customerOrder, voucher),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/voucher/quotation/apply")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Voucher> checkAndApplyVoucherOnQuotation(@RequestBody Quotation quotation,
			@RequestParam String voucherCode, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		if (voucherCode == null)
			throw new OsirisValidationException("voucherCode");

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Voucher>(null);

		Voucher voucher = voucherService.getVoucherByCode(voucherCode);
		if (voucher == null)
			throw new OsirisValidationException("voucher");

		return new ResponseEntity<Voucher>(voucherService.checkVoucherValidity(quotation, voucher),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/voucher/quotation-user/apply")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Voucher> checkAndApplyVoucherOnQuotation(@RequestParam Integer quotationId,
			@RequestParam String voucherCode,
			HttpServletRequest request) throws OsirisClientMessageException, OsirisException {
		detectFlood(request);
		Quotation quotation = null;
		quotation = quotationService.getQuotation(quotationId);

		if (quotation == null)
			throw new OsirisValidationException("quotation");

		if (quotation.getId() != null && !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Voucher>(null);

		Voucher voucher = null;
		if (voucherCode != null)
			voucher = voucherService.getVoucherByCode(voucherCode);
		if (voucher == null)
			throw new OsirisValidationException("voucher");
		return new ResponseEntity<Voucher>(
				voucherService.checkVoucherValidity(quotation, voucher),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/cancel")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> cancelQuotation(Integer quotationId, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		Quotation quotation = quotationService.getQuotation(quotationId);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		quotationService.addOrUpdateQuotationStatus(quotation, QuotationStatus.ABANDONED);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/validate")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> validateQuotation(Integer quotationId, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		Quotation quotation = quotationService.getQuotation(quotationId);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		quotationService.addOrUpdateQuotationStatus(quotation, QuotationStatus.VALIDATED_BY_CUSTOMER);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/cancel")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> cancelCustomerOrder(Integer customerOrderId, HttpServletRequest request)
			throws OsirisValidationException, OsirisException {
		detectFlood(request);

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		// cancel only if it's payment free
		if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0)
			for (Payment payment : customerOrder.getPayments())
				if (!payment.getIsCancelled())
					return new ResponseEntity<Boolean>(true, HttpStatus.OK);

		customerOrderService.addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.ABANDONED, true);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);

	}

	@GetMapping(inputEntryPoint + "/service-types/provisions")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Service>> getServiceForServiceTypeAndAffaire(@RequestParam List<Integer> serviceTypeIds,
			@RequestParam Integer affaireCityId)
			throws OsirisException {

		List<ServiceType> serviceTypes = new ArrayList<ServiceType>();
		if (serviceTypeIds != null) {
			for (Integer serviceTypeId : serviceTypeIds) {
				ServiceType serviceType = serviceTypeService.getServiceType(serviceTypeId);
				if (serviceType == null)
					throw new OsirisValidationException("servicesType");
				serviceTypes.add(serviceType);
			}
		} else {
			throw new OsirisValidationException("servicesTypeIds");
		}

		City city = cityService.getCity(affaireCityId);
		Affaire affaire = null;
		if (city != null) {
			affaire = new Affaire();
			affaire.setCity(city);
		}

		return new ResponseEntity<List<Service>>(
				serviceService.generateServiceInstanceFromMultiServiceTypes(serviceTypes, null, affaire),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/domiciliation-contract-types")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<DomiciliationContractType>> getContractTypes(HttpServletRequest request) {
		detectFlood(request);

		return new ResponseEntity<List<DomiciliationContractType>>(contractTypeService.getDomiciliationContractTypes(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/languages")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<Language>> getLanguages(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<Language>>(languageService.getLanguages(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/mail-redirection-types")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<MailRedirectionType>> getMailRedirectionTypes(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<MailRedirectionType>>(mailRedirectionTypeService.getMailRedirectionTypes(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/building-domiciliations")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<List<BuildingDomiciliation>> getBuildingDomiciliations(HttpServletRequest request) {
		detectFlood(request);
		return new ResponseEntity<List<BuildingDomiciliation>>(buildingDomiciliationService.getBuildingDomiciliations(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/legal-forms/label")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<LegalForm>> getLegalFormsByLabel(@RequestParam String label, @RequestParam Integer page,
			@RequestParam Integer size, HttpServletRequest request) {
		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.ASC, "label"));

		return new ResponseEntity<Page<LegalForm>>(
				legalFormService.getLegalFormsByName(label, pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/subscription")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<CustomerOrder> getCustomerOrderForSubscription(@RequestParam String subscriptionType,
			@RequestParam Boolean isPriceReductionForSubscription, @RequestParam(required = false) Integer idArticle,
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);

		if (subscriptionType == null)
			throw new OsirisValidationException("subscriptionType");

		if ((subscriptionType.equals(Subscription.ONE_POST_SUBSCRIPTION)
				|| subscriptionType.equals(Subscription.NEWSPAPER_KIOSK_BUY)) && idArticle == null) {
			throw new OsirisValidationException("subscriptionType need an idArticle for doing the checkout");
		}

		if (subscriptionType.equals(Subscription.ONE_POST_SUBSCRIPTION) && idArticle != null) {
			Post post = postService.getPost(idArticle);
			if (post == null) {
				throw new OsirisValidationException("Post does not exist");
			}
		}

		if (subscriptionType.equals(Subscription.NEWSPAPER_KIOSK_BUY) && idArticle != null) {
			Newspaper newspaper = newspaperService.getNewspaper(idArticle);
			if (newspaper == null) {
				throw new OsirisValidationException("Newspaper in kiosk does not exist");
			}
		}

		return new ResponseEntity<CustomerOrder>(
				customerOrderService.getCustomerOrderForSubscription(subscriptionType, isPriceReductionForSubscription,
						idArticle),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service-field-types")
	public ResponseEntity<List<ServiceFieldType>> getServiceFieldTypes(HttpServletRequest request) {
		detectFlood(request);

		return new ResponseEntity<List<ServiceFieldType>>(serviceFieldTypeService.getServiceFieldTypes(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/reading-folder")
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	public ResponseEntity<ReadingFolder> addOrUpdateReadingFolder(@RequestBody ReadingFolder readingFolder,
			HttpServletRequest request)
			throws OsirisValidationException {
		detectFlood(request);

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null)
			throw new OsirisValidationException("responsable");

		if (readingFolder.getLabel() != null && readingFolder.getLabel().trim().length() > 0)
			validationHelper.validateString(readingFolder.getLabel(), null, "readingFolderLabel");

		if (readingFolder.getMail() != null && !readingFolder.getMail().getId().equals(responsable.getMail().getId()))
			return new ResponseEntity<ReadingFolder>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		return new ResponseEntity<ReadingFolder>(
				readingFolderService.addOrUpdateReadingFolder(readingFolder, responsable),
				HttpStatus.OK);

	}

	@GetMapping(inputEntryPoint + "/reading-folder/delete")
	public ResponseEntity<Boolean> deleteReadingFolder(@RequestParam Integer idReadingFolder,
			HttpServletRequest request) throws OsirisValidationException {
		detectFlood(request);
		ReadingFolder readingFolder = readingFolderService.getReadingFolder(idReadingFolder);
		if (readingFolder == null)
			throw new OsirisValidationException("readingFolder");

		Responsable currentUser = employeeService.getCurrentMyJssUser();
		if (currentUser != null && !currentUser.getMail().getId().equals(readingFolder.getMail().getId()))
			throw new OsirisValidationException("readingFolder");

		if (readingFolder.getMail() != null && readingFolder.getMail().getId().equals(currentUser.getMail().getId())) {
			readingFolderService.deleteReadingFolder(readingFolder);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
	}

	@GetMapping(inputEntryPoint + "/reading-folders")
	@JsonView({ JacksonViews.MyJssListView.class })
	public ResponseEntity<List<ReadingFolder>> getReadingFolders(HttpServletRequest request)
			throws OsirisValidationException {
		detectFlood(request);

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null)
			throw new OsirisValidationException("responsable");

		return new ResponseEntity<List<ReadingFolder>>(
				readingFolderService.getAvailableReadingFoldersByResponsable(responsable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reading-folder")
	@JsonView({ JacksonViews.MyJssListView.class })
	public ResponseEntity<ReadingFolder> getReadingFolder(Integer idReadingFolder, HttpServletRequest request)
			throws OsirisValidationException {
		detectFlood(request);

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null)
			throw new OsirisValidationException("responsable");

		ReadingFolder readingFolder = readingFolderService.getReadingFolder(idReadingFolder);
		if (readingFolder == null)
			throw new OsirisValidationException("reading folder");

		if (!readingFolder.getMail().getMail().equals(responsable.getMail().getMail()))
			throw new OsirisValidationException("responsableMail");

		return new ResponseEntity<ReadingFolder>(
				readingFolderService.getReadingFolder(idReadingFolder),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/payment/cb/invoice")
	public ResponseEntity<String> getCardPaymentLinkForPaymentInvoices(@RequestBody List<Integer> customerOrderIds)
			throws OsirisClientMessageException, OsirisException {
		List<CustomerOrder> orders = new ArrayList<CustomerOrder>();
		for (Integer orderId : customerOrderIds) {
			CustomerOrder customerOrder = customerOrderService.getCustomerOrder(orderId);
			if (customerOrder == null)
				throw new OsirisValidationException("customerOrder");
			orders.add(customerOrder);
		}

		Responsable currentUser = employeeService.getCurrentMyJssUser();
		if (currentUser == null)
			throw new OsirisValidationException("currentUser");

		String link = customerOrderService.getCardPaymentLinkForPaymentInvoice(orders, currentUser.getMail().getMail(),
				"Paiement de la facture pour la commande n°" + String.join(", ",
						orders.stream().map(c -> c.getId().toString()).collect(Collectors.toList())));
		return new ResponseEntity<>("{\"link\":\"" + link + "\"}", HttpStatus.OK);
	}
}