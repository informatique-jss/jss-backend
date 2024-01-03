package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueLogin;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniquePayment;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueSignature;
import com.jss.osiris.modules.quotation.model.guichetUnique.GuichetUniqueUploadedFile;
import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;
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

    @Value("${guichet.unique.wallet.login}")
    private String walletLogin;

    @Value("${guichet.unique.wallet.password}")
    private String walletPassword;

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

    @Autowired
    BatchService batchService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String userRequestUrl = "/user";
    private String loginRequestUrl = "/login";
    private String ssoRequestUrl = "/sso";
    private String formalitiesRequestUrl = "/formalities";
    private String attachmentsRequestUrl = "/attachments";
    private String signaturesRequestUrl = "/signatures";
    private String paymentRequestUrl = "/payment";
    private String fileRequestUrl = "/file";
    private String annualAccountsRequestUrl = "/annual_accounts";
    private String acteDepositRequestUrl = "/acte_deposits";
    private String formalityStatusHistoriesUrl = "/formality_status_histories";
    private String annualAccountStatusHistoriesUrl = "/annual_account_status_histories";
    private String acteDepositStatusHistoriesUrl = "/acte_deposit_status_histories";

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
        res = new RestTemplate().postForEntity(
                guichetUniqueEntryPoint + userRequestUrl + loginRequestUrl + ssoRequestUrl,
                new HttpEntity<Object>(login, headers),
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
    public List<FormaliteGuichetUnique> getAllFormalitiesByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        formalites.addAll(getFormalitiesByDate(createdAfter, updatedAfter));
        formalites.addAll(getAnnualAccountsByDate(createdAfter, updatedAfter));
        formalites.addAll(getActeDepositsByDate(createdAfter, updatedAfter));
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
        formalites.addAll(getActeDepositsByRefenceMandataire(reference));

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
                formaliteGuichetUnique.setIsActeDeposit(false);
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
            response.getBody().setIsActeDeposit(false);
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

    @Override
    @SuppressWarnings({ "all" })
    public File getAttachmentById(String attachmentId, String customPrefix)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<byte[]> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + attachmentsRequestUrl + "/" + attachmentId + fileRequestUrl, HttpMethod.GET,
                new HttpEntity<String>(headers), byte[].class);

        if (response.getBody() != null) {
            File tempFile = null;
            try {
                ByteArrayInputStream fis = new ByteArrayInputStream(response.getBody());
                // opens input stream from the HTTP connection
                tempFile = File.createTempFile(customPrefix != null ? customPrefix : "attachment_inpi", ".pdf");

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                fis.close();
            } catch (Exception e) {
                throw new OsirisException(e, "Impossible to create inpi attachment " + attachmentId);
            }

            return tempFile;
        } else {
            throw new OsirisException(null, "Guichet unique attachment not found for id " + attachmentId);
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public PiecesJointe uploadAttachment(FormaliteGuichetUnique formaliteGuichetUnique, File file,
            TypeDocument typeDocument,
            String name) throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        GuichetUniqueUploadedFile newAttachment = new GuichetUniqueUploadedFile();
        try {
            newAttachment.setDocumentBase64(
                    new String(Base64.getEncoder().encode(FileUtils.readFileToByteArray(file)),
                            StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new OsirisException(e,
                    "Error when reading attachement to upload for formality " + formaliteGuichetUnique.getId());
        }
        newAttachment.setDocumentExtension("pdf");
        newAttachment.setNomDocument(name);
        newAttachment.setTypeDocument(typeDocument.getCode());

        String entryPoint = formalitiesRequestUrl;
        if (formaliteGuichetUnique.getIsAnnualAccounts())
            entryPoint = annualAccountsRequestUrl;
        else if (formaliteGuichetUnique.getIsActeDeposit())
            entryPoint = acteDepositRequestUrl;

        ResponseEntity<PiecesJointe> res = new RestTemplate().postForEntity(
                guichetUniqueEntryPoint + entryPoint + "/" + formaliteGuichetUnique.getId()
                        + attachmentsRequestUrl,
                new HttpEntity<Object>(newAttachment, headers), PiecesJointe.class);

        if (res != null && res.getBody() != null)
            return res.getBody();
        return null;
    }

    @Override
    @SuppressWarnings({ "all" })
    public void signeFormality(FormaliteGuichetUnique formaliteGuichetUnique,
            PiecesJointe signedSynthesis, PiecesJointe signedBe)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        GuichetUniqueSignature signature = new GuichetUniqueSignature();
        if (signedSynthesis != null)
            signature.setSignedDocument("/api/attachments/" + signedSynthesis.getAttachmentId());
        if (signedBe != null)
            signature.setBeSignedDocument("/api/attachments/" + signedBe.getAttachmentId());

        if (formaliteGuichetUnique.getIsAnnualAccounts())
            signature.setAnnualAccount("/api/annual_accounts/" + formaliteGuichetUnique.getId());
        else if (formaliteGuichetUnique.getIsActeDeposit())
            signature.setActeDeposit("/api/acte_deposits/" + formaliteGuichetUnique.getId());
        else
            signature.setFormality("/api/formalities/" + formaliteGuichetUnique.getId());

        if (signedSynthesis != null)
            signature.setSignedDocument("/api/attachments/" + signedSynthesis.getAttachmentId());
        if (signedBe != null)
            signature.setBeSignedDocument("/api/attachments/" + signedBe.getAttachmentId());

        new RestTemplate().postForEntity(
                guichetUniqueEntryPoint + signaturesRequestUrl, new HttpEntity<Object>(signature, headers),
                String.class);

        return;
    }

    @Override
    @SuppressWarnings({ "all" })
    public void payFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        GuichetUniquePayment payment = new GuichetUniquePayment();
        if (formaliteGuichetUnique.getIsAnnualAccounts())
            payment.setAnnualAccount("/api/annual_accounts/" + formaliteGuichetUnique.getId());
        else if (formaliteGuichetUnique.getIsActeDeposit())
            payment.setActeDeposit("/api/acte_deposits/" + formaliteGuichetUnique.getId());
        else
            payment.setFormality("/api/formalities/" + formaliteGuichetUnique.getId());

        payment.setLogin(walletLogin);
        payment.setPassword(walletPassword);
        payment.setPaymentType("CCL");
        new RestTemplate().postForEntity(
                guichetUniqueEntryPoint + paymentRequestUrl, new HttpEntity<Object>(payment, headers),
                String.class);

        return;
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
        List<FormaliteGuichetUnique> inFormalites = getAnnualAccountsByRefenceMandatairePaginated(page, reference);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getAnnualAccountsByRefenceMandatairePaginated(page, reference);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getAnnualAccountsByRefenceMandatairePaginated(int page, String reference)
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
                formaliteGuichetUnique.setIsActeDeposit(false);
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
            response.getBody().setIsActeDeposit(false);
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

    private List<FormaliteGuichetUnique> getActeDepositsByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getActeDepositsByDatePaginated(page, createdAfter, updatedAfter);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getActeDepositsByDatePaginated(page, createdAfter, updatedAfter);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getActeDepositsByDatePaginated(int page, LocalDateTime createdAfter,
            LocalDateTime updatedAfter)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + acteDepositRequestUrl + "?page=" + page + "&created[after]="
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

    private List<FormaliteGuichetUnique> getActeDepositsByRefenceMandataire(String reference)
            throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = new ArrayList<FormaliteGuichetUnique>();
        int page = 1;
        List<FormaliteGuichetUnique> inFormalites = getActeDepositsByRefenceMandatairePaginated(page, reference);
        while (inFormalites.size() > 0) {
            formalites.addAll(inFormalites);
            page++;
            inFormalites = getActeDepositsByRefenceMandatairePaginated(page, reference);
        }

        return formalites;
    }

    private List<FormaliteGuichetUnique> getActeDepositsByRefenceMandatairePaginated(int page, String reference)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteGuichetUnique>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + acteDepositRequestUrl + "?page=" + page + "&order[created]=desc&search="
                        + reference,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<FormaliteGuichetUnique>>() {
                });

        List<FormaliteGuichetUnique> res = response.getBody();
        if (res != null) {
            for (FormaliteGuichetUnique formaliteGuichetUnique : res) {
                formaliteGuichetUnique.setIsFormality(false);
                formaliteGuichetUnique.setIsAnnualAccounts(false);
                formaliteGuichetUnique.setIsActeDeposit(true);
            }
            return response.getBody();
        }
        return res;
    }

    @Override
    @SuppressWarnings({ "all" })
    public FormaliteGuichetUnique getActeDepositById(Integer id)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<FormaliteGuichetUnique> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + acteDepositRequestUrl + "/" + id,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<FormaliteGuichetUnique>() {
                });

        if (response.getBody() != null) {
            response.getBody().setIsAnnualAccounts(false);
            response.getBody().setIsFormality(false);
            response.getBody().setIsActeDeposit(true);
            return response.getBody();
        } else {
            throw new OsirisException(null, "Guichet unique formality not found for id " + id);
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public List<FormaliteStatusHistoryItem> getActeDepositStatusHistoriesById(Integer id)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<FormaliteStatusHistoryItem>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + acteDepositRequestUrl + "/" + id + acteDepositStatusHistoriesUrl,
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
    @SuppressWarnings({ "all" })
    public List<PiecesJointe> getActeDepositAttachments(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<PiecesJointe>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + acteDepositRequestUrl + "/" + formaliteGuichetUnique.getId()
                        + attachmentsRequestUrl,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<PiecesJointe>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new OsirisException(null,
                    "Guichet unique formality not found for id " + formaliteGuichetUnique.getId());
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public List<PiecesJointe> getAnnualAccountsAttachments(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<PiecesJointe>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + annualAccountsRequestUrl + "/" + formaliteGuichetUnique.getId()
                        + attachmentsRequestUrl,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<PiecesJointe>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new OsirisException(null,
                    "Guichet unique formality not found for id " + formaliteGuichetUnique.getId());
        }
    }

    @Override
    @SuppressWarnings({ "all" })
    public List<PiecesJointe> getFormalityAttachments(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<List<PiecesJointe>> response = new RestTemplate().exchange(
                guichetUniqueEntryPoint + formalitiesRequestUrl + "/" + formaliteGuichetUnique.getId()
                        + attachmentsRequestUrl,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<List<PiecesJointe>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new OsirisException(null,
                    "Guichet unique formality not found for id " + formaliteGuichetUnique.getId());
        }
    }

    @Override
    public void refreshFormalitiesFromLastHour()
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        List<FormaliteGuichetUnique> formalitesGuichetUnique = getAllFormalitiesByDate(
                LocalDateTime.now().minusYears(10),
                LocalDateTime.now().minusHours(1));
        if (formalitesGuichetUnique != null && formalitesGuichetUnique.size() > 0) {
            for (FormaliteGuichetUnique formaliteGuichetUnique : formalitesGuichetUnique) {
                batchService.declareNewBatch(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE, formaliteGuichetUnique.getId());
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
                        batchService.declareNewBatch(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE,
                                formaliteGuichetUnique.getId());
        }
    }
}
