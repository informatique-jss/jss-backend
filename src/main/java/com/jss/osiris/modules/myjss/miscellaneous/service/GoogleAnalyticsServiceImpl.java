package com.jss.osiris.modules.myjss.miscellaneous.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.jss.osiris.modules.myjss.miscellaneous.model.Ga4Event;
import com.jss.osiris.modules.myjss.miscellaneous.model.Ga4Item;
import com.jss.osiris.modules.myjss.miscellaneous.model.Ga4ParamPurchase;
import com.jss.osiris.modules.myjss.miscellaneous.model.Ga4Request;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@org.springframework.stereotype.Service
public class GoogleAnalyticsServiceImpl implements GoogleAnalyticsService {

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

    @Override
    public void trackPurchase(IQuotation quotation, boolean isValidation, String gaClientId)
            throws OsirisException {

        // If not in prod, we do not want to send data to Google Analytics
        if (gaUrl == null || gaUrl.isBlank()) {
            return;
        }

        if (quotation == null)
            return;

        InvoicingSummary invoicingSummary = customerOrderService.getInvoicingSummaryForIQuotation(quotation);

        List<Ga4Item> items = mapItems(quotation);

        Ga4ParamPurchase params = new Ga4ParamPurchase();

        params.setPageName("checkout");
        params.setPageType("quotation");
        params.setPageWebsite("myjss");

        // E-commerce
        params.setTransactionId(String.valueOf(quotation.getId()));
        params.setValue(invoicingSummary.getTotalPrice());
        params.setTax(invoicingSummary.getVatTotal());
        params.setShipping(BigDecimal.ZERO);
        params.setCurrency("EUR");
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
        Ga4Event purchaseEvent = new Ga4Event();
        purchaseEvent.setName("purchase");
        purchaseEvent.setParams(params);

        // Request creation
        Ga4Request request = new Ga4Request();

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null)
            request.setUserId(currentUser.getId().toString());

        request.setClientId(gaClientId);
        request.setEvents(Collections.singletonList(purchaseEvent));

        sendToGoogle(request);
    }

    private List<Ga4Item> mapItems(IQuotation quotation) throws OsirisException {
        List<Ga4Item> items = new ArrayList<>();

        if (quotation.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                if (asso.getServices() != null) {
                    asso.setServices(serviceService.populateTransientField(asso.getServices()));
                    for (Service service : asso.getServices()) {

                        BigDecimal nbServiceType = BigDecimal.valueOf(service.getServiceTypes().size());

                        if (service.getServiceTypes() != null) {
                            for (ServiceType serviceType : service.getServiceTypes()) {

                                Ga4Item item = new Ga4Item();
                                item.setItemId(service.getId() + "_" + serviceType.getId());
                                item.setItemName(serviceType.getLabel());
                                if (quotation.getVoucher() != null) {
                                    item.setCoupon(quotation.getVoucher().getCode());
                                }
                                if (service.getServiceDiscountAmount() != null)
                                    item.setDiscount(service.getServiceDiscountAmount().divide(nbServiceType));
                                item.setItemBrand("JSS");
                                item.setItemCategory(serviceType.getServiceFamily().getLabel());
                                item.setItemGroupCategory(
                                        serviceType.getServiceFamily().getServiceFamilyGroup().getLabel());
                                item.setPrice(service.getServiceTotalPrice().divide(nbServiceType));
                                item.setQuantity(1);

                                items.add(item);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    private ResponseEntity<String> sendToGoogle(Ga4Request requestBody) {
        SSLHelper.disableCertificateValidation();

        // Building URL query
        String urlWithParams = String.format("%s?measurement_id=%s&api_secret=%s",
                gaUrl, measurementId, apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Ga4Request> entity = new HttpEntity<>(requestBody, headers);

        // Sending POST request
        ResponseEntity<String> response = new RestTemplate().exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                String.class);

        return response;
    }
}
