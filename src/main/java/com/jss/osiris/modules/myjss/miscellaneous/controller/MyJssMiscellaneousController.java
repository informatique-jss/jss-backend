package com.jss.osiris.modules.myjss.miscellaneous.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.miscellaneous.model.Sitemap;
import com.jss.osiris.modules.myjss.miscellaneous.service.GoogleAnalyticsService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.StorageFileService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceFamilyService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MyJssMiscellaneousController {

	private static final String inputEntryPoint = "/myjss/miscellaneous";

	@Autowired
	StorageFileService storageFileService;

	@Autowired
	GoogleAnalyticsService googleAnalyticsService;

	@Autowired
	AffaireService affaireService;

	@Autowired
	ServiceFamilyService serviceFamilyService;

	@Autowired
	ConstantService constantService;

	@Value("${upload.file.directory}")
	private String uploadFolder;

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

	@GetMapping(value = "/{filename:.+\\.xml}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Resource> getSitemap(@PathVariable String filename)
			throws OsirisValidationException, OsirisException {
		File file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder, filename);

		if (!file.exists()) {
			return ResponseEntity.badRequest().build();
		} else {
			Resource resource = new FileSystemResource(file);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
					.contentType(MediaType.APPLICATION_XML)
					.body(resource);
		}
	}

	@PostMapping(inputEntryPoint + "/google-analytics/view-list-item")
	public ResponseEntity<Boolean> trackViewListItem(@RequestBody List<ServiceType> serviceTypes,
			@RequestParam(required = false) Integer affaireId, @RequestParam Integer serviceFamilyId,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		Affaire affaire = null;
		if (affaireId == null)
			affaire = constantService.getAffaireDummyForSubscription();
		else
			affaire = affaireService.getAffaire(affaireId);

		if (affaire == null)
			throw new OsirisValidationException("affaire");

		ServiceFamily serviceFamily = serviceFamilyService.getServiceFamily(serviceFamilyId);

		if (serviceFamily == null)
			throw new OsirisValidationException("serviceFamilyId");

		googleAnalyticsService.trackViewItemList(serviceTypes, affaire, serviceFamily, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/add-to-cart")
	public ResponseEntity<Boolean> trackAddToCart(@RequestBody ServiceType serviceType,
			@RequestParam(required = false) Integer affaireId,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		Affaire affaire = null;
		if (affaireId == null)
			affaire = constantService.getAffaireDummyForSubscription();
		else
			affaire = affaireService.getAffaire(affaireId);

		if (affaire == null)
			throw new OsirisValidationException("affaire");

		googleAnalyticsService.trackAddToCart(serviceType, affaire, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/remove-from-cart")
	public ResponseEntity<Boolean> trackRemoveFromCart(@RequestBody ServiceType serviceType,
			@RequestParam(required = false) Integer affaireId, HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		Affaire affaire = null;
		if (affaireId == null)
			affaire = constantService.getAffaireDummyForSubscription();
		else
			affaire = affaireService.getAffaire(affaireId);

		if (affaire == null)
			throw new OsirisValidationException("affaire");

		googleAnalyticsService.trackRemoveFromCart(serviceType, affaire, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/begin-checkout/quotation")
	public ResponseEntity<Boolean> trackBeginCheckoutQuotation(@RequestBody Quotation quotation,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		googleAnalyticsService.trackBeginCheckout(quotation, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/begin-checkout/customer-order")
	public ResponseEntity<Boolean> trackBeginCheckoutOrder(@RequestBody CustomerOrder customerOrder,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		googleAnalyticsService.trackBeginCheckout(customerOrder, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/add-payment-info/quotation")
	public ResponseEntity<Boolean> trackAddPaymentInfoQuotation(@RequestBody Quotation quotation,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		googleAnalyticsService.trackAddPaymentInfo(quotation, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/google-analytics/add-payment-info/customer-order")
	public ResponseEntity<Boolean> trackAddPaymentInfoOrder(@RequestBody CustomerOrder customerOrder,
			HttpServletRequest request)
			throws OsirisClientMessageException, OsirisException {
		detectFlood(request);

		String gaClientId = request.getHeader("gaClientId");

		googleAnalyticsService.trackAddPaymentInfo(customerOrder, gaClientId);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

}
