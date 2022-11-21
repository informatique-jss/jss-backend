package com.jss.osiris.modules.quotation.service;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;

@Service
public class CentralPayDelegateServiceImpl implements CentralPayDelegateService {

    @Value("${central.pay.entrypoint}")
    private String centralPayEndpoint;

    @Value("${central.pay.api.key}")
    private String centralPayKey;

    @Value("${central.pay.api.password}")
    private String centralPayPassword;

    private String paymentRequestUrl = "/paymentRequest";
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
    public CentralPayPaymentRequest getPaymentRequest(String centralPayPaymentRequestId) {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<CentralPayPaymentRequest> res = new RestTemplate().exchange(
                centralPayEndpoint + paymentRequestUrl + "/" + centralPayPaymentRequestId, HttpMethod.GET,
                new HttpEntity<Object>(headers),
                CentralPayPaymentRequest.class);

        if (res.getBody() != null) {
            return res.getBody();
        }
        return null;
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
    public CentralPayPaymentRequest generatePayPaymentRequest(Float amount, String mail, String entityId,
            String subject) {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Convert in cents
        Integer finalAmount = Math.round(amount * 100);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("paymentMethod[]", "TRANSACTION");
        map.add("currency", "EUR");
        map.add("endUserLanguage", "FRA");
        map.add("totalAmount", finalAmount + "");
        map.add("breakdown[]", "{amount:" + finalAmount + ", email:" + mail + "}");
        map.add("merchantPaymentRequestId", entityId);
        map.add("description", subject);

        // Deadline 1h after
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        map.add("deadline", ZonedDateTime.now().plusHours(1).format(formatter));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<CentralPayPaymentRequest> res = new RestTemplate().postForEntity(
                centralPayEndpoint + paymentRequestUrl,
                request,
                CentralPayPaymentRequest.class);

        if (res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

}
