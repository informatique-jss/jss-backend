package com.jss.osiris.modules.osiris.quotation.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayBreakdown;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPayment;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayTransaction;

@Service
public class CentralPayDelegateServiceImpl implements CentralPayDelegateService {

    @Value("${central.pay.entrypoint}")
    private String centralPayEndpoint;

    @Value("${central.pay.api.key}")
    private String centralPayKey;

    @Value("${central.pay.api.password}")
    private String centralPayPassword;

    @Value("${my.jss.entry.point}")
    private String myJssEntryPoint;

    private String paymentRequestUrl = "/paymentRequest";
    private String transactiontUrl = "/transaction";
    private String paymentRequestCancelUrl = "/cancel";

    HttpHeaders createHeaders() {
        return new HttpHeaders() {
            {
                String auth = centralPayKey + ":" + centralPayPassword;
                byte[] encodedAuth = Base64.getEncoder().encode(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    @Override
    public CentralPayPaymentRequest getPaymentRequest(String centralPayPaymentRequestId) throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<CentralPayPaymentRequest> res;
        try {
            res = new RestTemplate().exchange(
                    centralPayEndpoint + paymentRequestUrl + "/" + centralPayPaymentRequestId, HttpMethod.GET,
                    new HttpEntity<Object>(headers),
                    CentralPayPaymentRequest.class);
        } catch (Exception e) {
            throw new OsirisException(e,
                    "Impossible to fetch central pay payment request n°" + centralPayPaymentRequestId);
        }

        if (res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "null" })
    public CentralPayTransaction getTransaction(CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        if (centralPayPaymentRequest.getBreakdowns() == null || centralPayPaymentRequest.getBreakdowns().size() == 0)
            throw new OsirisException(null, "CentralPayBreakdown not found in Payment Request n°"
                    + centralPayPaymentRequest.getPaymentRequestId());

        String transactionId = null;
        for (CentralPayBreakdown centralPayBreakdown : centralPayPaymentRequest.getBreakdowns()) {
            if (centralPayBreakdown.getPayments() != null && centralPayBreakdown.getPayments().size() > 0)
                for (CentralPayPayment centralPayPayment : centralPayBreakdown.getPayments())
                    if (centralPayPayment.getPaymentMethod().equals("TRANSACTION")) {
                        transactionId = centralPayPayment.getUuid();

                        ResponseEntity<CentralPayTransaction> res = new RestTemplate().exchange(
                                centralPayEndpoint + transactiontUrl + "/" + transactionId, HttpMethod.GET,
                                new HttpEntity<Object>(headers),
                                CentralPayTransaction.class);

                        if (res.getBody() != null && res.getBody().getTransactionStatus().equals("SUCCESS")) {
                            return res.getBody();
                        }
                    }
        }

        throw new OsirisException(null, "No valid transaction found not found in Payment Request n°"
                + centralPayPaymentRequest.getPaymentRequestId());
    }

    @Override
    public CentralPayPaymentRequest cancelPaymentRequest(String centralPayPaymentRequestId) {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
                headers);

        ResponseEntity<CentralPayPaymentRequest> res = new RestTemplate().postForEntity(
                centralPayEndpoint + paymentRequestUrl + "/" + centralPayPaymentRequestId + paymentRequestCancelUrl,
                request,
                CentralPayPaymentRequest.class);

        if (res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @Override
    public CentralPayPaymentRequest generatePayPaymentRequest(BigDecimal amount, String mail, String entityId,
            String subject, boolean isQuotation) {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Convert in cents
        Integer finalAmount = amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_EVEN).intValue();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("paymentMethod[]", "TRANSACTION");
        map.add("currency", "EUR");
        map.add("endUserLanguage", "fr");
        map.add("browserAcceptLanguage", "fr-FR");
        map.add("totalAmount", finalAmount + "");
        map.add("breakdown[]", "{amount:" + finalAmount + ", email:" + mail + "}");
        map.add("merchantPaymentRequestId", entityId);
        map.add("description", subject);

        // Deadline 1h after
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        map.add("deadline", ZonedDateTime.now().plusHours(1).format(formatter));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BufferingClientHttpRequestInterceptor());

        ResponseEntity<CentralPayPaymentRequest> res;
        if (isQuotation)
            res = restTemplate.postForEntity(
                    centralPayEndpoint + paymentRequestUrl + "?delay=5&urlRedirect=" + myJssEntryPoint
                            + "/quotations/details/" + entityId + "&urlOnCanceled=" + myJssEntryPoint
                            + "/quotations/details/" + entityId,
                    request,
                    CentralPayPaymentRequest.class);
        else
            res = restTemplate.postForEntity(
                    centralPayEndpoint + paymentRequestUrl + "?delay=5&urlRedirect=" + myJssEntryPoint
                            + "/orders/details/" + entityId + "&urlRedirect=" + myJssEntryPoint + "/orders/details/"
                            + entityId,
                    request,
                    CentralPayPaymentRequest.class);

        if (res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    private class BufferingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            return execution.execute(request, body);
        }
    }
}
