package com.jss.osiris.libs.azure;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.audit.model.TranslationResponse;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class TranslationServiceImpl implements TranslationService {

    @Value("${azure.translator.api.key}")
    private String azureTranslatorApiKey;

    @Value("${azure.translator.api.endpoint}")
    private String azureTranslatorApiEndPoint;

    @Value("${azure.translator.api.route}")
    private String azureTranslatorApiRoute;

    @Value("${azure.translator.api.region}")
    private String azureTranslatorApiRegion;

    HttpHeaders createHeaders() throws OsirisException, OsirisClientMessageException {
        return new HttpHeaders() {
            {
                add("Ocp-Apim-Subscription-Key", azureTranslatorApiKey);
                add("Ocp-Apim-Subscription-Region", azureTranslatorApiRegion);
                add("Content-Type", "application/json; charset=UTF-8");
            }
        };
    }

    @Override
    public String translateTextToEnglish(String text) throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>("[{\"Text\": \"" + text + "\"}]", headers);

        ResponseEntity<List<TranslationResponse>> res = new RestTemplate().exchange(
                azureTranslatorApiEndPoint + azureTranslatorApiRoute, HttpMethod.POST, request,
                new ParameterizedTypeReference<List<TranslationResponse>>() {
                });
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody().get(0).getTranslations().get(0).getText();
        }

        return null;
    }
}
