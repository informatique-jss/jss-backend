package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompanyResponse;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneLogInResponse;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneLogin;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneResult;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementNoticeTemplateRepository;

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
    public List<RneCompany> getCompanyBySirens(List<String> sirens)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<RneCompany>> res = new RestTemplate().exchange(
                rneEntryPoint + entrepriseRequest + "?pageSize=500&siren[]=" + String.join("&siren[]=", sirens),
                HttpMethod.GET,
                new HttpEntity<Object>(headers),
                new ParameterizedTypeReference<List<RneCompany>>() {
                });
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @Override
    public List<RneCompany> getCompanyBySiret(String siret)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<RneCompany>> res = new RestTemplate().exchange(
                rneEntryPoint + entrepriseRequest + "?siret=" + siret, HttpMethod.GET,
                new HttpEntity<Object>(headers),
                new ParameterizedTypeReference<List<RneCompany>>() {
                });
        if (res.getBody() != null && res.getBody() != null) {
            return res.getBody();
        }
        return null;
    }

    @Override
    public List<RneCompany> getCompanyByDenominationAndPostalCode(String denomination, String postalCode)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<List<RneCompany>> res = new RestTemplate().exchange(
                    rneEntryPoint + entrepriseRequest + "?companyName=" + denomination + "&zipCodes[]=" + postalCode,
                    HttpMethod.GET,
                    new HttpEntity<Object>(headers),
                    new ParameterizedTypeReference<List<RneCompany>>() {
                    });
            if (res.getBody() != null && res.getBody() != null) {
                return res.getBody();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public RneResult getCompanyModifiedForDay(LocalDate dateSearched, String lastSiret, List<String> sirens)
            throws OsirisException, OsirisClientMessageException {

        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<RneCompany> results = new ArrayList<>();

        String searchAfter = lastSiret;
        int pageSize = 250;
        boolean hasMore = true;

        // Minus 1 because from date is excluded
        String url = rneEntryPoint
                + "/companies/diff?pageSize=" + pageSize
                + "&from=" + dateSearched.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + "&to=" + dateSearched.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        HttpHeaders requestHeaders = new HttpHeaders(headers);
        if (searchAfter != null) {
            url += "&searchAfter=" + searchAfter;
        }

        if (sirens != null && sirens.size() > 0) {
            url += "&siren[]=" + String.join("&siren[]=", sirens);
        }

        ResponseEntity<List<RneCompanyResponse>> res = new RestTemplate().exchange(url, HttpMethod.GET,
                new HttpEntity<>(requestHeaders), new ParameterizedTypeReference<List<RneCompanyResponse>>() {
                });

        List<RneCompanyResponse> body = res.getBody();
        if (body != null) {
            results.addAll(body.stream().map(RneCompanyResponse::getCompany).toList());
        }

        // Handle pagination
        List<String> paginationHeaders = res.getHeaders().get("pagination-search-after");
        if (paginationHeaders != null && !paginationHeaders.isEmpty()) {
            searchAfter = paginationHeaders.get(0);
        } else {
            hasMore = false;
        }

        RneResult result = new RneResult();
        result.setCompanies(results);
        result.setLastSiret(hasMore ? searchAfter : null);
        return result;
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