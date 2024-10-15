package com.jss.osiris.modules.myjss.quotation.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.AssoServiceDocumentService;
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
			throws OsirisClientMessageException {

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
			throws OsirisClientMessageException {

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

	@GetMapping(inputEntryPoint + "/attachment/download")
	@Transactional
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment)
			throws OsirisValidationException, OsirisException {

		byte[] data = null;
		HttpHeaders headers = null;
		Attachment tiersAttachment = attachmentService.getAttachment(idAttachment);

		boolean canDownload = true;
		if (tiersAttachment.getProvision() == null && tiersAttachment.getAssoServiceDocument() == null)
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getProvision().getService() != null
				&& tiersAttachment.getProvision().getService().getAssoAffaireOrder().getQuotation() != null
				&& !myJssQuotationValidationHelper.canSeeQuotation(
						tiersAttachment.getProvision().getService().getAssoAffaireOrder().getQuotation()))
			canDownload = false;

		if (tiersAttachment.getProvision() != null && tiersAttachment.getProvision().getService() != null
				&& tiersAttachment.getProvision().getService().getAssoAffaireOrder().getCustomerOrder() != null
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

}
