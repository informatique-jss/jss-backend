package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.net.HttpCookie;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueLogin;
import com.jss.osiris.modules.quotation.service.FormaliteService;

@Service
public class GuichetUniqueDelegateServiceImpl implements GuichetUniqueDelegateService {

    @Value("${guichet.unique.entry.point}")
    private String guichetUniqueEntryPoint;

    @Value("${guichet.unique.password}")
    private String guichetUniquePassword;

    @Value("${guichet.unique.login}")
    private String guichetUniqueLogin;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FormaliteService formaliteService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String userRequestUrl = "/user";
    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String formalitiesRequestUrl = "/formalities";

    private String bearerValue = null;
    private LocalDateTime bearerExpireDateTime = LocalDateTime.now().minusYears(100);

    HttpHeaders createHeaders() throws OsirisException, OsirisClientMessageException {
        if (bearerValue == null || bearerExpireDateTime.isBefore(LocalDateTime.now()))
            loginUser();
        return new HttpHeaders() {
            {
                add("Cookie", "BEARER=" + bearerValue);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    private void loginUser() throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GuichetUniqueLogin login = new GuichetUniqueLogin();

        login.setUsername(guichetUniqueLogin);
        login.setPassword(guichetUniquePassword);

        ResponseEntity<GuichetUniqueLogin> res;
        try {
            res = new RestTemplate().postForEntity(
                    guichetUniqueEntryPoint + userRequestUrl + loginRequestUrl + ssoRequestUrl,
                    new HttpEntity<Object>(login, headers),
                    GuichetUniqueLogin.class);
        } catch (Exception e) {
            throw new OsirisClientMessageException("Impossible de se connecter pour l'utilisateur " + guichetUniqueLogin
                    + ". Merci de vérifier les identifiants");
        }

        if (res.getHeaders() != null && res.getHeaders().getFirst(HttpHeaders.SET_COOKIE) != null) {
            List<HttpCookie> cookies = HttpCookie.parse(res.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
            bearerValue = cookies.get(0).getValue();
            bearerExpireDateTime = LocalDateTime.now().plusSeconds(cookies.get(0).getMaxAge()).minusMinutes(10);
        } else {
            throw new OsirisException(null, "No bearer cookie found in response");
        }
    }

    @Override
    public List<FormaliteGuichetUnique> getFormalitiesByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getFormalitiesByDatePaginated(page, createdAfter, updatedAfter);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getFormalitiesByDatePaginated(page, createdAfter, updatedAfter);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getFormalitiesByDatePaginated(int page, LocalDateTime createdAfter,
            LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "?page=" + page + "&created[after]="
                        + (createdAfter != null ? formatter.format(createdAfter) : "")
                        + (updatedAfter != null ? "&updated[after]=" + formatter.format(updatedAfter) : ""),
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteGuichetUnique>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    public List<FormaliteGuichetUnique> getFormalitiesByRefenceMandataire(String reference)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getFormalitiesByRefenceMandatairePaginated(page, reference);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getFormalitiesByRefenceMandatairePaginated(page, reference);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getFormalitiesByRefenceMandatairePaginated(int page, String reference)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "?page=" + page + "&search=" + reference,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteGuichetUnique>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "all" })
    public FormaliteGuichetUnique getFormalityById(Integer id) throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<FormaliteGuichetUnique> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "/" + id,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<FormaliteGuichetUnique>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new OsirisException(null, "Guichet unique formality not found for id " + id);
        }
    }

    @Override
    public void refreshFormalitiesFromLastHour()
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = getFormalitiesByDate(LocalDateTime.now().minusYears(10),
                LocalDateTime.now().minusHours(1));
        if (formalites != null && formalites.size() > 0) {
            for (FormaliteGuichetUnique formalite : formalites)
                formaliteGuichetUniqueService.refreshFormaliteGuichetUnique(formalite.getId(), null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAllOpenFormalities()
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        List<Formalite> formalites = formaliteService.getFormaliteForGURefresh();
        if (formalites != null && formalites.size() > 0) {
            for (Formalite formalite : formalites)
                if (formalite.getFormalitesGuichetUnique() != null
                        && formalite.getFormalitesGuichetUnique().size() > 0)
                    for (FormaliteGuichetUnique formaliteGuichetUnique : formalite.getFormalitesGuichetUnique())
                        formaliteGuichetUniqueService
                                .refreshFormaliteGuichetUnique(formaliteGuichetUnique.getId(), formalite);
        }
    }
}
