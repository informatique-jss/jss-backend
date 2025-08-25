package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.laPoste.LaPosteTracking;

@Service
public class LaPosteDelegateServiceImpl implements LaPosteDelegateService {

    @Value("${la.poste.entry.point}")
    private String laPosteEntryPoint;

    @Value("${la.poste.token}")
    private String laPosteToken;

    private String trackingUrl = "/suivi/v2/idships";

    HttpHeaders createHeaders() throws OsirisException, OsirisClientMessageException {
        return new HttpHeaders() {
            {
                add("Authorization", "Bearer " + laPosteToken);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    @Override
    public LaPosteTracking getLaPosteTracking(String trackingNumber)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<LaPosteTracking> res = new RestTemplate().exchange(
                laPosteEntryPoint + trackingUrl + "/" + trackingNumber, HttpMethod.GET, new HttpEntity<Object>(headers),
                LaPosteTracking.class);
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

}