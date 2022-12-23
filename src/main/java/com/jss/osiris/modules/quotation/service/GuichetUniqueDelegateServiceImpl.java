package com.jss.osiris.modules.quotation.service;

import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueLogin;

@Service
public class GuichetUniqueDelegateServiceImpl implements GuichetUniqueDelegateService {

    @Value("${guichet.unique.entry.point}")
    private String guichetUniqueEntryPoint;

    @Value("${guichet.unique.login}")
    private String guichetUniqueLogin;

    @Value("${guichet.unique.password}")
    private String guichetUniquePassword;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String userRequestUrl = "/user";
    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String formalitiesRequestUrl = "/formalities";

    private String bearerValue = null;
    private LocalDateTime bearerExpireDateTime = null;

    HttpHeaders createHeaders() throws OsirisException {
        if (bearerValue == null || bearerExpireDateTime == null || bearerExpireDateTime.isBefore(LocalDateTime.now()))
            loginUser();
        return new HttpHeaders() {
            {
                add("Cookie", "BEARER=" + bearerValue);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    // @Scheduled(initialDelay = 100, fixedDelay = 1000000000)
    public void test() throws OsirisException {
        getFormalities(LocalDateTime.now().minusMonths(0), null);
    }

    private void loginUser() throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GuichetUniqueLogin login = new GuichetUniqueLogin();
        login.setUsername(guichetUniqueLogin);
        login.setPassword(guichetUniquePassword);

        ResponseEntity<GuichetUniqueLogin> res = new RestTemplate().postForEntity(
                guichetUniqueEntryPoint + userRequestUrl + loginRequestUrl + ssoRequestUrl, login,
                GuichetUniqueLogin.class);

        if (res.getHeaders() != null && res.getHeaders().getFirst(HttpHeaders.SET_COOKIE) != null) {
            List<HttpCookie> cookies = HttpCookie.parse(res.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
            bearerValue = cookies.get(0).getValue();
            bearerExpireDateTime = LocalDateTime.now().plusSeconds(cookies.get(0).getMaxAge()).minusMinutes(10);
        } else {
            throw new OsirisException(null, "No bearer cookie found in response");
        }
    }

    @Override
    public List<Formalite> getFormalities(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException {
        List<Formalite> formalites = new ArrayList<Formalite>();
        int page = 1;
        List<Formalite> inFormalites = getFormalitiesPaginated(page, createdAfter, updatedAfter);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getFormalitiesPaginated(page, createdAfter, updatedAfter);
        }

        return formalites;
    }

    private List<Formalite> getFormalitiesPaginated(int page, LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<Formalite>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "?page=" + page + "&created[after]="
                        + (createdAfter != null ? formatter.format(createdAfter) : "")
                        + (updatedAfter != null ? "&updated[after]=" + formatter.format(updatedAfter) : ""),
                HttpMethod.GET, new HttpEntity<String>(headers), new ParameterizedTypeReference<List<Formalite>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }
}
