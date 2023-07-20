package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneLogInResponse;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneLogin;
import com.jss.osiris.modules.quotation.repository.AnnouncementNoticeTemplateRepository;
import com.jss.osiris.modules.quotation.service.guichetUnique.FormaliteGuichetUniqueService;

@Service
public class RneDelegateServiceImpl implements RneDelegateService {

    @Autowired
    AnnouncementNoticeTemplateRepository announcementNoticeTemplateRepository;

    @Value("${rne.entry.point}")
    private String rneEntryPoint;

    @Value("${rne.password}")
    private String rneUniquePassword;

    @Value("${rne.login}")
    private String rneLogin;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FormaliteService formaliteService;

    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String apiRequestUrl = "/api";
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
    public RneCompany getCompanyBySiren(String siren)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<RneCompany> resAffaireInfos = new RestTemplate().exchange(
                rneEntryPoint + apiRequestUrl + entrepriseRequest + "/" + siren, HttpMethod.GET,
                new HttpEntity<Object>(headers),
                RneCompany.class);
        System.out.println(resAffaireInfos);
        if (resAffaireInfos.getBody() != null) {
            return resAffaireInfos.getBody();
        }
        return null;
    }

    private void loginUser() throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RneLogin login = new RneLogin();

        login.setUsername(rneLogin);
        login.setPassword(rneUniquePassword);

        ResponseEntity<RneLogInResponse> res;

        try {
            res = new RestTemplate().postForEntity(
                    rneEntryPoint + apiRequestUrl + ssoRequestUrl + loginRequestUrl,
                    new HttpEntity<Object>(login, headers),
                    RneLogInResponse.class);

        } catch (Exception e) {
            throw new OsirisClientMessageException("Impossible de se connecter pour l'utilisateur " + rneLogin
                    + ". Merci de vérifier les identifiants");
        }

        if (res.getBody().getToken() != null) {
            bearerValue = res.getBody().getToken();
        }
    }
}