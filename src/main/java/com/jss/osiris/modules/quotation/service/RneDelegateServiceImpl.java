package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneLogInResponse;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneLogin;
import com.jss.osiris.modules.quotation.repository.AnnouncementNoticeTemplateRepository;

@Service
public class RneDelegateServiceImpl implements RneDelegateService {

    @Autowired
    AnnouncementNoticeTemplateRepository announcementNoticeTemplateRepository;

    @Value("${guichet.unique.rne.entry.point}")
    private String rneEntryPoint;

    @Value("${guichet.unique.rne.password}")
    private String rneUniquePassword;

    @Value("${guichet.unique.rne.login}")
    private String rneLogin;

    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String entrepriseRequest = "/companies";

    private String bearerValue = null;
    private LocalDateTime bearerExpireDateTime = LocalDateTime.now().minusYears(100);

    HttpHeaders createHeaders() throws OsirisException, OsirisClientMessageException {
        if (bearerValue == null || bearerExpireDateTime.isBefore(LocalDateTime.now()))
            loginUser();
        return new HttpHeaders() {
            {
                add("Authorization", "Bearer " + bearerValue);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    @Override
    public List<RneCompany> getCompanyBySiren(String siren)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<RneCompany>> res = new RestTemplate().exchange(
                rneEntryPoint + entrepriseRequest + "?siren[]=" + siren, HttpMethod.GET,
                new HttpEntity<Object>(headers),
                new ParameterizedTypeReference<List<RneCompany>>() {
                });
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @Override
    public List<RneCompany> getCompanyBySiret(String siren)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<RneCompany>> res = new RestTemplate().exchange(
                rneEntryPoint + entrepriseRequest + "?siret=" + siren, HttpMethod.GET,
                new HttpEntity<Object>(headers),
                new ParameterizedTypeReference<List<RneCompany>>() {
                });
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @SuppressWarnings({ "null" })
    private void loginUser() throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RneLogin login = new RneLogin();

        login.setUsername(rneLogin);
        login.setPassword(rneUniquePassword);

        ResponseEntity<RneLogInResponse> res;

        res = new RestTemplate().postForEntity(
                rneEntryPoint + ssoRequestUrl + loginRequestUrl,
                new HttpEntity<Object>(login, headers),
                RneLogInResponse.class);

        if (res.getBody() != null && res.getBody().getToken() != null) {
            bearerValue = res.getBody().getToken();
            bearerExpireDateTime = LocalDateTime.now().plusMinutes(30);
        }
    }
}