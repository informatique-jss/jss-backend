package com.jss.osiris.modules.myjss.miscellaneous.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.miscellaneous.model.GoogleAnalyticsEvent;
import com.jss.osiris.modules.myjss.miscellaneous.model.GoogleAnalyticsItem;
import com.jss.osiris.modules.myjss.miscellaneous.model.GoogleAnalyticsParams;
import com.jss.osiris.modules.myjss.miscellaneous.model.GoogleAnalyticsRequest;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.CharacterPrice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.service.CharacterPriceService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceTypeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@org.springframework.stereotype.Service
public class GoogleAnalyticsServiceImpl implements GoogleAnalyticsService {

    private static final String EUR_CURRENCY = "EUR";
    private final static String QUOTATION = "QUOTATION";
    private final static String ORDER = "ORDER";

    @Value("${dev.mode}")
    private Boolean devMode;

    @Value("${google.analytics.mp.url}")
    private String gaUrl;

    @Value("${google.analytics.api-secret}")
    private String apiSecret;

    @Value("${google.analytics.measurement-id}")
    private String measurementId;

    @Autowired
    private CustomerOrderService customerOrderService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    ConstantService constantService;

    @Autowired
    CharacterPriceService characterPriceService;

    @Override
    public void trackLoginLogout(String eventName, String pageName, String pageType, String gaClientId,
            String gaSessionId)
            throws OsirisException {

        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

        params.setPageName(pageName);
        params.setPageType(pageType);
        params.setPageWebsite("myjss");

        if (gaSessionId != null && !gaSessionId.equals(""))
            params.setSessionId(Long.parseLong(gaSessionId));
        if (devMode)
            params.setDebugMode(true);

        // Event creation
        GoogleAnalyticsEvent loginLogoutEvent = new GoogleAnalyticsEvent();
        loginLogoutEvent.setName(eventName);
        loginLogoutEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(loginLogoutEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackPurchase(CustomerOrder customerOrder)
            throws OsirisException {
        if (customerOrder != null) {
            String sessionId = customerOrder.getGaSessionId();
            LocalDateTime sessionExpirationDate = null;
            if (sessionId != null && !sessionId.equals("")) {
                Instant instant = Instant.ofEpochSecond(Long.parseLong(sessionId));
                sessionExpirationDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            if (sessionExpirationDate == null
                    || sessionExpirationDate.plusMinutes(20).isBefore(LocalDateTime.now()))
                sessionId = null;

            if (customerOrder.getQuotations() != null && !customerOrder.getQuotations().isEmpty()) {
                trackPurchase(customerOrder.getQuotations().get(0), true,
                        customerOrder.getQuotations().get(0).getLastGaClientId(), sessionId);
            } else {
                trackPurchase(customerOrder, true, customerOrder.getLastGaClientId(), sessionId);
            }
        }
    }

    private GoogleAnalyticsParams getNewAnalyticParamsObject(String gaSessionId) {
        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

        params.setPageName("checkout");
        params.setPageType("quotation");
        params.setPageWebsite("myjss");

        if (gaSessionId != null && !gaSessionId.equals(""))
            params.setSessionId(Long.parseLong(gaSessionId));
        if (devMode)
            params.setDebugMode(true);

        return params;
    }

    private void trackPurchase(IQuotation quotation, boolean isValidation, String gaClientId, String gaSessionId)
            throws OsirisException {

        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        if (quotation.getIsQuotation()) {
            QuotationStatus statusDraft = quotationStatusService.getQuotationStatusByCode(QuotationStatus.DRAFT);
            ((Quotation) quotation).setQuotationStatus(statusDraft);
        } else {
            CustomerOrderStatus statusDraft = customerOrderStatusService
                    .getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT);
            ((CustomerOrder) quotation).setCustomerOrderStatus(statusDraft);
        }

        InvoicingSummary invoicingSummary = customerOrderService.getInvoicingSummaryForIQuotation(quotation);

        List<GoogleAnalyticsItem> items = mapQuotationToItems(quotation);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        // E-commerce
        params.setTransactionId(String.valueOf(quotation.getId()));
        params.setValue(invoicingSummary.getTotalPrice());
        if (params.getValue().equals(new BigDecimal(0)))
            params.setValue(BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP));

        params.setTax(invoicingSummary.getVatTotal());
        if (params.getTax().equals(new BigDecimal(0)))
            params.setTax(BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP));
        params.setShipping(BigDecimal.ZERO);
        params.setCurrency(EUR_CURRENCY);
        params.setItems(items);
        if (quotation.getVoucher() != null) {
            params.setCoupon(quotation.getVoucher().getCode());
        }

        // Business
        params.setBusinessType(quotation.getIsQuotation() ? QUOTATION : ORDER);
        params.setBusinessOderId(quotation.getId());
        params.setBusinessIsDraft(!isValidation);
        params.setBusinessAmount(invoicingSummary.getTotalPrice());
        if (quotation.getServiceFamilyGroup() != null) {
            params.setBusinessService(quotation.getServiceFamilyGroup().getLabel());
        }

        // Event creation
        GoogleAnalyticsEvent purchaseEvent = new GoogleAnalyticsEvent();
        purchaseEvent.setName("purchase");
        purchaseEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = quotation.getResponsable();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());
        else
            throw new OsirisException("No user found for IQuotation " + quotation.getId());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(purchaseEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackViewItemList(List<ServiceType> serviceTypes, Affaire affaire, ServiceFamily serviceFamily,
            String gaClientId, String gaSessionId) throws OsirisException {

        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceTypes == null || serviceTypes.isEmpty() || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(serviceTypes, affaire, null);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        // View Item List
        params.setItemListId(serviceFamily.getCode());
        params.setItemListName(serviceFamily.getLabel());
        params.setCurrency(EUR_CURRENCY);
        params.setItems(items);

        // Event creation
        GoogleAnalyticsEvent viewListItemEvent = new GoogleAnalyticsEvent();
        viewListItemEvent.setName("view_item_list");
        viewListItemEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(viewListItemEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackAddToCart(ServiceType serviceType, Affaire affaire, String gaClientId, String gaSessionId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceType == null || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(Arrays.asList(serviceType), affaire, null);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        // Add to cart
        params.setCurrency(EUR_CURRENCY);
        if (items.stream().filter(item -> item.getPrice() != null).toList().size() != 0)
            params.setValue(items.stream().filter(item -> item.getPrice() != null).map(GoogleAnalyticsItem::getPrice)
                    .reduce((x, y) -> x.add(y)).get());
        else
            params.setValue(BigDecimal.ONE);

        params.setItems(items);

        // Event creation
        GoogleAnalyticsEvent viewListItemEvent = new GoogleAnalyticsEvent();
        viewListItemEvent.setName("add_to_cart");
        viewListItemEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(viewListItemEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackRemoveFromCart(ServiceType serviceType, Affaire affaire, String gaClientId, String gaSessionId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceType == null || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(Arrays.asList(serviceType), affaire, null);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        // Remove from cart
        params.setCurrency(EUR_CURRENCY);
        if (items.stream().filter(item -> item.getPrice() != null).toList().size() != 0)
            params.setValue(items.stream().filter(item -> item.getPrice() != null).map(GoogleAnalyticsItem::getPrice)
                    .reduce((x, y) -> x.add(y)).get());
        else
            params.setValue(BigDecimal.ONE);

        params.setItems(items);

        // Event creation
        GoogleAnalyticsEvent viewListItemEvent = new GoogleAnalyticsEvent();
        viewListItemEvent.setName("remove_from_cart");
        viewListItemEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(viewListItemEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackBeginCheckout(IQuotation quotation, String gaClientId, String gaSessionId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        InvoicingSummary invoicingSummary = pricingHelper.getIQuotationTotalPrice(quotation);
        List<GoogleAnalyticsItem> items = mapQuotationToItems(quotation);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        params.setCurrency(EUR_CURRENCY);
        params.setValue(invoicingSummary.getTotalPrice());
        params.setItems(items);

        // Event creation
        GoogleAnalyticsEvent beginCheckoutEvent = new GoogleAnalyticsEvent();
        beginCheckoutEvent.setName("begin_checkout");
        beginCheckoutEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(beginCheckoutEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackAddPaymentInfo(IQuotation quotation, String gaClientId, String gaSessionId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        InvoicingSummary invoicingSummary = pricingHelper.getIQuotationTotalPrice(quotation);
        List<GoogleAnalyticsItem> items = mapQuotationToItems(quotation);

        quotation.setLastGaClientId(gaClientId);
        quotation.setGaSessionId(gaSessionId);

        GoogleAnalyticsParams params = getNewAnalyticParamsObject(gaSessionId);

        params.setCurrency(EUR_CURRENCY);
        params.setValue(invoicingSummary.getTotalPrice());
        params.setItems(items);

        // Event creation
        GoogleAnalyticsEvent beginCheckoutEvent = new GoogleAnalyticsEvent();
        beginCheckoutEvent.setName("add_payment_info");
        beginCheckoutEvent.setParams(params);

        // Request creation
        GoogleAnalyticsRequest request = new GoogleAnalyticsRequest();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(beginCheckoutEvent));

        sendToGoogle(request);
    }

    private List<GoogleAnalyticsItem> mapQuotationToItems(IQuotation quotation) throws OsirisException {
        List<GoogleAnalyticsItem> items = new ArrayList<>();

        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                if (asso.getServices() != null) {
                    asso.setServices(serviceService.populateTransientField(asso.getServices()));
                    for (Service service : asso.getServices()) {
                        Provision announcementProvision = null;
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions())
                                if (provision.getAnnouncement() != null)
                                    announcementProvision = provision;
                        items.addAll(mapServicesTypesAndAffaireToItems(service.getServiceTypes(), asso.getAffaire(),
                                announcementProvision));
                    }
                }
            }
        }
        return items;
    }

    private List<GoogleAnalyticsItem> mapServicesTypesAndAffaireToItems(List<ServiceType> serviceTypes, Affaire affaire,
            Provision announcementProvision)
            throws OsirisException {
        List<GoogleAnalyticsItem> items = new ArrayList<>();
        int itemsCount = 0;

        CustomerOrder dummyCustomerOrder = new CustomerOrder();
        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED);
        dummyCustomerOrder.setCustomerOrderStatus(customerOrderStatus);
        dummyCustomerOrder.setResponsable(constantService.getResponsableDummyCustomerFrance());

        AssoAffaireOrder assoAffaireOrder = new AssoAffaireOrder();
        assoAffaireOrder.setAffaire(affaire);
        assoAffaireOrder.setCustomerOrder(dummyCustomerOrder);

        BigDecimal nbServiceType = BigDecimal.valueOf(serviceTypes.size());

        for (ServiceType serviceType : serviceTypes) {
            GoogleAnalyticsItem item = mapItem(serviceType, null, nbServiceType, announcementProvision);
            items.add(item);
            itemsCount++;
            if (itemsCount > 10)
                return items;
        }
        return items;
    }

    private GoogleAnalyticsItem mapItem(ServiceType serviceType, String voucherCode, BigDecimal nbServiceType,
            Provision announcementProvision)
            throws OsirisException {
        GoogleAnalyticsItem item = new GoogleAnalyticsItem();
        item.setItemId(serviceType.getId().toString());
        item.setItemName(serviceType.getLabel());
        item.setCoupon(voucherCode);
        item.setItemBrand("JSS");
        item.setItemCategory(serviceType.getServiceFamily() != null ? serviceType.getServiceFamily().getLabel() : null);
        item.setItemGroupCategory((serviceType.getServiceFamily() != null
                && serviceType.getServiceFamily().getServiceFamilyGroup() != null)
                        ? serviceType.getServiceFamily().getServiceFamilyGroup().getLabel()
                        : null);
        item.setPrice(getServiceTypePrice(serviceType, announcementProvision));
        item.setQuantity(1);
        return item;
    }

    private BigDecimal getServiceTypePrice(ServiceType serviceType, Provision announcementProvision)
            throws OsirisException {
        BigDecimal price = BigDecimal.ZERO;
        if (serviceType != null) {
            serviceType = serviceTypeService.getServiceType(serviceType.getId());
            if (serviceType.getAssoServiceProvisionTypes() != null) {
                for (AssoServiceProvisionType assoServiceProvisionType : serviceType.getAssoServiceProvisionTypes()) {
                    if (assoServiceProvisionType.getDefaultDeboursPriceNonTaxable() != null) {
                        price = price.add(assoServiceProvisionType.getDefaultDeboursPriceNonTaxable());
                    }
                    if (assoServiceProvisionType.getDefaultDeboursPrice() != null) {
                        BigDecimal debourPreTax = assoServiceProvisionType.getDefaultDeboursPrice();
                        BigDecimal debourVat = debourPreTax.multiply(constantService.getVatTwenty().getRate())
                                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        price = price.add(debourPreTax).add(debourVat);
                    }
                    if (assoServiceProvisionType.getProvisionType() != null
                            && assoServiceProvisionType.getProvisionType().getBillingTypes() != null) {
                        for (BillingType billingType : assoServiceProvisionType.getProvisionType().getBillingTypes()) {
                            if (Boolean.TRUE.equals(billingType.getIsOptionnal()))
                                continue;

                            BigDecimal preTaxPrice = pricingHelper.getAppliableBillingItem(billingType, null)
                                    .getPreTaxPrice();

                            if (announcementProvision != null
                                    && Boolean.TRUE.equals(billingType.getIsPriceBasedOnCharacterNumber())) {
                                CharacterPrice characterPrice = characterPriceService
                                        .getCharacterPrice(announcementProvision);
                                if (characterPrice != null) {
                                    Integer characterNumber = characterPriceService
                                            .getCharacterNumber(announcementProvision, false);
                                    preTaxPrice = characterPrice.getPrice()
                                            .multiply(BigDecimal.valueOf(characterNumber));
                                }
                            }

                            BigDecimal vatPrice = BigDecimal.ZERO;
                            if (!Boolean.TRUE.equals(billingType.getIsNonTaxable())) {
                                if (billingType.getVat() != null)
                                    vatPrice = preTaxPrice.multiply(billingType.getVat().getRate())
                                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                else
                                    vatPrice = preTaxPrice.multiply(constantService.getVatTwenty().getRate())
                                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                            }
                            price = price.add(preTaxPrice).add(vatPrice);
                        }
                    }
                }
            }
        }

        if (price.equals(BigDecimal.ZERO))
            return BigDecimal.ONE;
        return price;
    }

    private ResponseEntity<String> sendToGoogle(GoogleAnalyticsRequest requestBody) {
        SSLHelper.disableCertificateValidation();

        // Building URL query
        String urlWithParams = String.format("%s?measurement_id=%s&api_secret=%s",
                gaUrl, measurementId, apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GoogleAnalyticsRequest> entity = new HttpEntity<>(requestBody, headers);

        // Sending POST request
        ResponseEntity<String> response = new RestTemplate().exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                String.class);

        // try {
        // String body = new
        // ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(entity);
        // } catch (JsonProcessingException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        return response;
    }
}