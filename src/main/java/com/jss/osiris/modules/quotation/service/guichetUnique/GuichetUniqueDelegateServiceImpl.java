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

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueLogin;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueRepository;
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

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    FormaliteGuichetUniqueRepository formaliteGuichetUniqueRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String userRequestUrl = "/user";
    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String formalitiesRequestUrl = "/formalities";
    private String annualAccountsRequestUrl = "/annual_accounts";
    private String formalityStatusHistoriesUrl = "/formality_status_histories";
    private String annualAccountStatusHistoriesUrl = "/annual_account_status_histories";

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
    public List<FormaliteGuichetUnique> getAllFormalitiesByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        formalites.addAll(getFormalitiesByDate(createdAfter, updatedAfter));
        formalites.addAll(getAnnualAccountsByDate(createdAfter, updatedAfter));
        return formalites;
    }

    private List<FormaliteGuichetUnique> getFormalitiesByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
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
    public List<FormaliteGuichetUnique> getAllFormalitiesByRefenceMandataire(String reference)
            throws OsirisException, OsirisClientMessageException {

        List<FormaliteGuichetUnique> formalites = formaliteGuichetUniqueRepository.findByRefenceMandataire(reference);

        if (formalites != null && formalites.size() > 0)
            return formalites;

        formalites = new ArrayList<FormaliteGuichetUnique>();
        formalites.addAll(getFormalitiesByRefenceMandataire(reference));
        formalites.addAll(getAnnualAccountsByRefenceMandataire(reference));

        return formalites;
    }

    private List<FormaliteGuichetUnique> getFormalitiesByRefenceMandataire(String reference)
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
                guichetUniqueEntryPoint + formalitiesRequestUrl + "?page=" + page + "&order[created]=desc&search="
                        + reference,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteGuichetUnique>>() {
                });

        List<FormaliteGuichetUnique> res = response.getBody();
        if (res != null) {
            for (FormaliteGuichetUnique formaliteGuichetUnique : res) {
                formaliteGuichetUnique.setIsFormality(true);
                formaliteGuichetUnique.setIsAnnualAccounts(false);
            }

            return response.getBody();
        }
        return res;
    }

    @Override
    @SuppressWarnings({ "all" })
    public FormaliteGuichetUnique getFormalityById(Integer id) throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<FormaliteGuichetUnique> response;
        try {
            response = new RestTemplate().exchange(
                    guichetUniqueEntryPoint + formalitiesRequestUrl + "/" + id,
                    HttpMethod.GET, new HttpEntity<String>(headers),
                    new ParameterizedTypeReference<FormaliteGuichetUnique>() {
                    });
        } catch (Exception e) {
            throw new OsirisException(e, "Impossible to fetch formality guichet unique " + id);
        }

        if (response.getBody() != null) {
            response.getBody().setIsAnnualAccounts(false);
            response.getBody().setIsFormality(true);
            return response.getBody();
        } else {
            throw new OsirisException(null, "Guichet unique formality not found for id " + id);
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public List<FormaliteStatusHistoryItem> getFormalityStatusHistoriesById(Integer id)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteStatusHistoryItem>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "/" + id + formalityStatusHistoriesUrl,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteStatusHistoryItem>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new OsirisException(null, "Guichet unique formality not found for id " + id);
        }
    }

    private List<FormaliteGuichetUnique> getAnnualAccountsByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getAnnualAccountsByDatePaginated(page, createdAfter, updatedAfter);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getFormalitiesByDatePaginated(page, createdAfter, updatedAfter);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getAnnualAccountsByDatePaginated(int page, LocalDateTime createdAfter,
            LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + annualAccountsRequestUrl + "?page=" + page + "&created[after]="
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

    private List<FormaliteGuichetUnique> getAnnualAccountsByRefenceMandataire(String reference)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getAnnuaAccountsByRefenceMandatairePaginated(page, reference);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getAnnuaAccountsByRefenceMandatairePaginated(page, reference);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getAnnuaAccountsByRefenceMandatairePaginated(int page, String reference)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + annualAccountsRequestUrl + "?page=" + page + "&order[created]=desc&search="
                        + reference,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteGuichetUnique>>() {
                });

        List<FormaliteGuichetUnique> res = response.getBody();
        if (res != null) {
            for (FormaliteGuichetUnique formaliteGuichetUnique : res) {
                formaliteGuichetUnique.setIsFormality(false);
                formaliteGuichetUnique.setIsAnnualAccounts(true);
            }
            return response.getBody();
        }
        return res;
    }

    @Override
    @SuppressWarnings({ "all" })
    public FormaliteGuichetUnique getAnnualAccountById(Integer id)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<FormaliteGuichetUnique> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + annualAccountsRequestUrl + "/" + id,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<FormaliteGuichetUnique>() {
                });

        if (response.getBody() != null) {
            response.getBody().setIsAnnualAccounts(true);
            response.getBody().setIsFormality(false);
            return response.getBody();
        } else {
            throw new OsirisException(null, "Guichet unique formality not found for id " + id);
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public List<FormaliteStatusHistoryItem> getAnnualAccountStatusHistoriesById(Integer id)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteStatusHistoryItem>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + annualAccountsRequestUrl + "/" + id + annualAccountStatusHistoriesUrl,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteStatusHistoryItem>>() {
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
        List<FormaliteGuichetUnique> formalites = getAllFormalitiesByDate(LocalDateTime.now().minusYears(10),
                LocalDateTime.now().minusHours(1));
        if (formalites != null && formalites.size() > 0) {
            for (FormaliteGuichetUnique formalite : formalites) {
                try {
                    formaliteGuichetUniqueService.refreshFormaliteGuichetUnique(formalite, null, true);
                } catch (Exception e) {
                    globalExceptionHandler.handleExceptionOsiris(e);
                }
            }
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
                        try {
                            formaliteGuichetUniqueService.refreshFormaliteGuichetUnique(formaliteGuichetUnique,
                                    formalite, true);
                        } catch (Exception e) {
                            globalExceptionHandler.handleExceptionOsiris(e);
                        }
        }
    }
}
