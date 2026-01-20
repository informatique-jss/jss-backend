package com.jss.osiris.modules.myjss.miscellaneous.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
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

    @Override
    public void trackPurchase(CustomerOrder customerOrder)
            throws OsirisException {
        if (customerOrder != null) {
            if (customerOrder.getQuotations() != null && !customerOrder.getQuotations().isEmpty()) {
                trackPurchase(customerOrder.getQuotations().get(0), true,
                        customerOrder.getQuotations().get(0).getLastGaClientId());
            } else {
                trackPurchase(customerOrder, true, customerOrder.getLastGaClientId());
            }
        }
    }

    private void trackPurchase(IQuotation quotation, boolean isValidation, String gaClientId)
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

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

        params.setPageName("checkout");
        params.setPageType("quotation");
        params.setPageWebsite("myjss");

        // E-commerce
        params.setTransactionId(String.valueOf(quotation.getId()));
        params.setValue(invoicingSummary.getTotalPrice());
        params.setTax(invoicingSummary.getVatTotal());
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

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(purchaseEvent));

        sendToGoogle(request);
    }

    @Override
    public void trackViewItemList(List<ServiceType> serviceTypes, Affaire affaire, ServiceFamily serviceFamily,
            String gaClientId) throws OsirisException {

        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceTypes == null || serviceTypes.isEmpty() || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(serviceTypes, affaire);

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

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
    public void trackAddToCart(ServiceType serviceType, Affaire affaire, String gaClientId) throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceType == null || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(Arrays.asList(serviceType), affaire);

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

        // Add to cart
        params.setCurrency(EUR_CURRENCY);
        if (items.stream().filter(item -> item.getPrice() != null).toList().size() != 0)
            params.setValue(items.stream().filter(item -> item.getPrice() != null).map(GoogleAnalyticsItem::getPrice)
                    .reduce((x, y) -> x.add(y)).get());
        else
            params.setValue(BigDecimal.ZERO);
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
    public void trackRemoveFromCart(ServiceType serviceType, Affaire affaire, String gaClientId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (serviceType == null || affaire == null) {
            return;
        }

        List<GoogleAnalyticsItem> items = mapServicesTypesAndAffaireToItems(Arrays.asList(serviceType), affaire);

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

        // Remove from cart
        params.setCurrency(EUR_CURRENCY);
        if (items.stream().filter(item -> item.getPrice() != null).toList().size() != 0)
            params.setValue(items.stream().filter(item -> item.getPrice() != null).map(GoogleAnalyticsItem::getPrice)
                    .reduce((x, y) -> x.add(y)).get());
        else
            params.setValue(BigDecimal.ZERO);
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
    public void trackBeginCheckout(IQuotation quotation, String gaClientId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        InvoicingSummary invoicingSummary = pricingHelper.getIQuotationTotalPrice(quotation);
        List<GoogleAnalyticsItem> items = mapQuotationToItems(quotation);

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

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
    public void trackAddPaymentInfo(IQuotation quotation, String gaClientId)
            throws OsirisException {
        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        InvoicingSummary invoicingSummary = pricingHelper.getIQuotationTotalPrice(quotation);
        List<GoogleAnalyticsItem> items = mapQuotationToItems(quotation);

        GoogleAnalyticsParams params = new GoogleAnalyticsParams();

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

                        BigDecimal nbServiceType = BigDecimal.valueOf(service.getServiceTypes().size());

                        if (service.getServiceTypes() != null) {
                            for (ServiceType serviceType : service.getServiceTypes()) {
                                String voucherCode = null;
                                if (quotation.getVoucher() != null) {
                                    voucherCode = quotation.getVoucher().getCode();
                                }
                                GoogleAnalyticsItem item = mapItem(serviceType, service, voucherCode, nbServiceType);
                                items.add(item);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    private List<GoogleAnalyticsItem> mapServicesTypesAndAffaireToItems(List<ServiceType> serviceTypes, Affaire affaire)
            throws OsirisException {
        List<GoogleAnalyticsItem> items = new ArrayList<>();

        BigDecimal nbServiceType = BigDecimal.valueOf(serviceTypes.size());
        Service instanciatedService = new Service();

        List<Service> instanciatedServices = new ArrayList<>();
        for (ServiceType serviceType : serviceTypes) {
            serviceType = serviceTypeService.getServiceType(serviceType.getId());
            instanciatedService = serviceService.generateServiceInstanceFromMultiServiceTypes(
                    Arrays.asList(serviceType), "", affaire).get(0);
            instanciatedServices.add(instanciatedService);
        }

        instanciatedServices = serviceService.populateTransientField(instanciatedServices);

        for (Service service : instanciatedServices) {
            for (ServiceType serviceType : service.getServiceTypes()) {
                GoogleAnalyticsItem item = mapItem(serviceType, service, null, nbServiceType);
                items.add(item);
            }
        }
        return items;
    }

    private GoogleAnalyticsItem mapItem(ServiceType serviceType, Service service, String voucherCode,
            BigDecimal nbServiceType) {
        GoogleAnalyticsItem item = new GoogleAnalyticsItem();
        item.setItemId(serviceType.getId().toString());
        item.setItemName(serviceType.getLabel());
        item.setCoupon(voucherCode);
        if (service.getServiceDiscountAmount() != null)
            item.setDiscount(service.getServiceDiscountAmount().divide(nbServiceType));
        item.setItemBrand("JSS");
        item.setItemCategory(serviceType.getServiceFamily() != null ? serviceType.getServiceFamily().getLabel() : null);
        item.setItemGroupCategory((serviceType.getServiceFamily() != null
                && serviceType.getServiceFamily().getServiceFamilyGroup() != null)
                        ? serviceType.getServiceFamily().getServiceFamilyGroup().getLabel()
                        : null);
        if (service.getServiceTotalPrice() != null)
            item.setPrice(service.getServiceTotalPrice().divide(nbServiceType, 2, RoundingMode.HALF_UP));
        item.setQuantity(1);
        return item;
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

        return response;
    }
}