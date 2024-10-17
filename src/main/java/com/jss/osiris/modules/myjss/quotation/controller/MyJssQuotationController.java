package com.jss.osiris.modules.myjss.quotation.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jss.osiris.modules.myjss.quotation.controller.model.MyJssImage;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceTypeFieldTypePossibleValue;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.AssoServiceDocumentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeDocumentService;

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
	ServiceService serviceService;

	@Autowired
	AttachmentService attachmentService;

	@Autowired
	TypeDocumentService typeDocumentService;

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
	ConstantService constantService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CustomerOrderCommentService customerOrderCommentService;

	@PostMapping(inputEntryPoint + "/order/search/current")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<CustomerOrder>> searchOrdersForCurrentUser(
			@RequestBody List<String> customerOrderStatus, @RequestParam Integer page, @RequestParam String sortBy)
			throws OsirisClientMessageException {
		if (customerOrderStatus == null || customerOrderStatus.size() == 0)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("createdDateDesc") && !sortBy.equals("createdDateAsc")
				&& !sortBy.equals("statusAsc"))
			sortBy = "createdDateDesc";

		return new ResponseEntity<List<CustomerOrder>>(
				customerOrderService.searchOrdersForCurrentUser(customerOrderStatus, page, sortBy), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/search/affaire")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<CustomerOrder>> searchOrdersForCurrentUserAndAffaire(@RequestParam Integer idAffaire)
			throws OsirisClientMessageException {
		if (idAffaire == null)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		Affaire affaire = affaireService.getAffaire(idAffaire);
		if (affaire == null)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<CustomerOrder>>(
				customerOrderService.searchOrdersForCurrentUserAndAffaire(affaire), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/search/current")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Quotation>> searchQuotationsForCurrentUser(
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
				quotationService.searchQuotationsForCurrentUser(quotationStatus, page, sortBy), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/asso")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<CustomerOrder> getCustomerOrder(@RequestParam Integer customerOrderId)
			throws OsirisClientMessageException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);

		return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<Quotation> getQuotation(@RequestParam Integer quotationId)
			throws OsirisClientMessageException {

		Quotation quotation = quotationService.getQuotation(quotationId);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<Quotation>(new Quotation(), HttpStatus.OK);

		return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/service/provision/attachments")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Attachment>> getAttachmentsForProvisionOfService(@RequestParam Integer serviceId)
			throws OsirisClientMessageException {

		Service service = serviceService.getService(serviceId);
		IQuotation quotation = service.getAssoAffaireOrder().getCustomerOrder();
		if (quotation == null)
			quotation = service.getAssoAffaireOrder().getQuotation();
		if (service == null || quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<List<Attachment>>(new ArrayList<Attachment>(), HttpStatus.OK);

		return new ResponseEntity<List<Attachment>>(serviceService.getAttachmentsForProvisionOfService(service),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire/attachments")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Attachment>> getAttachmentsForAffaire(@RequestParam Integer idAffaire)
			throws OsirisClientMessageException {

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
		Attachment tiersAttachment = attachmentService.getAttachment(idAttachment);

		boolean canDownload = true;
		if (tiersAttachment.getProvision() == null && tiersAttachment.getAssoServiceDocument() == null
				&& tiersAttachment.getCustomerOrder() == null)
			canDownload = false;

		// Can only download invoice
		if (tiersAttachment.getCustomerOrder() != null && !tiersAttachment.getAttachmentType().getId()
				.equals(constantService.getAttachmentTypeInvoice().getId()))
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getProvision().getService() != null
				&& tiersAttachment.getProvision().getService().getAssoAffaireOrder().getQuotation() != null
				&& tiersAttachment.getAttachmentType().getIsToSentOnUpload()
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						tiersAttachment.getProvision().getService().getAssoAffaireOrder().getQuotation()))
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getProvision().getService() != null
				&& tiersAttachment.getProvision().getService().getAssoAffaireOrder().getCustomerOrder() != null
				&& tiersAttachment.getAttachmentType().getIsToSentOnUpload()
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						tiersAttachment.getProvision().getService().getAssoAffaireOrder().getCustomerOrder()))
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getAssoServiceDocument() != null
				&& tiersAttachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						tiersAttachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getQuotation()))
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getAssoServiceDocument() != null
				&& tiersAttachment.getAssoServiceDocument().getService().getAssoAffaireOrder()
						.getCustomerOrder() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						tiersAttachment.getAssoServiceDocument().getService().getAssoAffaireOrder().getCustomerOrder()))
			canDownload = false;

		if (!canDownload)
			return new ResponseEntity<byte[]>(null, headers, HttpStatus.OK);

		if (tiersAttachment == null || tiersAttachment.getUploadedFile() == null
				|| tiersAttachment.getUploadedFile().getPath() == null)
			throw new OsirisValidationException("tiersAttachment or UploadedFile or Path");

		File file = new File(tiersAttachment.getUploadedFile().getPath());

		if (file != null) {
			try {
				data = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}

			headers = new HttpHeaders();
			headers.add("filename", tiersAttachment.getUploadedFile().getFilename());
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
	public ResponseEntity<Boolean> uploadAttachment(@RequestParam MultipartFile file,
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

		if (!canUpload)
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);

		if (!entityType.equals(AssoServiceDocument.class.getSimpleName()))
			throw new OsirisValidationException("entityType");

		attachmentService.addAttachment(file, idEntity, null, entityType, attachmentType, filename,
				false, null, typeDocument);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<Affaire> getAffaire(@RequestParam Integer id) throws OsirisValidationException {
		if (id == null)
			throw new OsirisValidationException("id");

		return new ResponseEntity<Affaire>(affaireService.getAffaire(id), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/affaire")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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

	@GetMapping(inputEntryPoint + "/quotation/documents")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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

	@GetMapping(inputEntryPoint + "/customer-order-comments/customer")
	@JsonView(JacksonViews.MyJssView.class)
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

		customerOrderComment.setEmployee(null);
		customerOrderComment.setCurrentCustomer(employeeService.getCurrentMyJssUser());
		customerOrderComment.setProvision(null);
		customerOrderComment.setActiveDirectoryGroups(new ArrayList<ActiveDirectoryGroup>());
		customerOrderComment.getActiveDirectoryGroups().add(constantService.getActiveDirectoryGroupSales());
		customerOrderComment.setIsToDisplayToCustomer(true);

		if (customerOrderComment.getId() == null)
			customerOrderComment.setCreatedDateTime(LocalDateTime.now());
		else if (customerOrderCommentOriginal != null)
			customerOrderComment.setCreatedDateTime(customerOrderCommentOriginal.getCreatedDateTime());

		customerOrderCommentService.addOrUpdateCustomerOrderComment(customerOrderComment);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/affaire/search/current")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Affaire>> getAffairesForCurrentUser(@RequestParam Integer page,
			@RequestParam String sortBy, @RequestParam String searchText) {
		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("nameAsc") && !sortBy.equals("nameDesc"))
			sortBy = "nameAsc";

		return new ResponseEntity<List<Affaire>>(affaireService.getAffairesForCurrentUser(page, sortBy, searchText),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/order")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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

	@PostMapping(inputEntryPoint + "/service/fields")
	public ResponseEntity<Boolean> addOrUpdateServiceFields(@RequestBody Service service)
			throws OsirisValidationException, OsirisException {
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

		if (serviceFetched.getAssoAffaireOrder().getQuotation() != null) {
			String quotationStatusCode = serviceFetched.getAssoAffaireOrder().getQuotation().getQuotationStatus()
					.getCode();
			if (quotationStatusCode.equals(QuotationStatus.ABANDONED)
					|| quotationStatusCode.equals(QuotationStatus.REFUSED_BY_CUSTOMER)
					|| quotationStatusCode.equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
				return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}

		for (AssoServiceFieldType assoServiceFieldTypeOld : serviceFetched.getAssoServiceFieldTypes()) {
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

		serviceService.addOrUpdateService(serviceFetched);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}