package com.jss.osiris.modules.quotation.service.infoGreffe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.infoGreffe.DocumentAssocieInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.InfoGreffeToken;
import com.jss.osiris.modules.quotation.model.infoGreffe.ListingInfogreffe;

@Service
public class InfogreffeDelegateServiceImpl implements InfogreffeDelegateService {

    @Value("${infogreffe.auth.entry.point}")
    private String infoGreffeAuthEntryPoint;
    @Value("${infogreffe.auth.login}")
    private String infoGreffeAuthEntryLogin;
    @Value("${infogreffe.auth.password}")
    private String infoGreffeAuthEntryPassword;
    @Value("${infogreffe.auth.client.id}")
    private String infoGreffeAuthEntryClientId;
    @Value("${infogreffe.auth.grant.type}")
    private String infoGreffeAuthEntryGrantType;
    @Value("${infogreffe.url}")
    private String infoGreffeUrl;

    private String listingFormalites = "/formalites/list";
    private String detailFormalite = "/formalites/detail";
    private String bearerValue = null;
    private LocalDateTime bearerExpireDateTime = null;

    @Autowired
    FormaliteInfogreffeService infogreffeFormaliteService;

    HttpHeaders createHeaders() throws OsirisException {
        if (bearerValue == null || bearerExpireDateTime == null || bearerExpireDateTime.isBefore(LocalDateTime.now()))
            loginUser();
        return new HttpHeaders() {
            {
                add("Authorization", "Bearer " + bearerValue);
                setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    @SuppressWarnings({ "null" })
    private void loginUser() throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", infoGreffeAuthEntryLogin);
        map.add("password", infoGreffeAuthEntryPassword);
        map.add("grant_type", infoGreffeAuthEntryGrantType);
        map.add("client_id", infoGreffeAuthEntryClientId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<InfoGreffeToken> res;
        try {
            res = new RestTemplate().postForEntity(infoGreffeAuthEntryPoint, request,
                    InfoGreffeToken.class);
        } catch (Exception e) {
            if (e.getMessage().contains("Connection timed out"))
                throw new OsirisException(e, "Service d'authentification Infogreffe indisponible !");
            else
                throw e;
        }
        if (res.getBody() != null) {
            bearerValue = res.getBody().getAccess_token();
            bearerExpireDateTime = LocalDateTime.now().plusSeconds(res.getBody().getExpires_in() - 100);
        }
    }

    @Override
    public List<FormaliteInfogreffe> getAllInfogreffeFormalities() throws OsirisException {
        List<FormaliteInfogreffe> formalites = new ArrayList<FormaliteInfogreffe>();
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<ListingInfogreffe> response = new RestTemplate().exchange(
                infoGreffeUrl + listingFormalites,
                HttpMethod.GET, new HttpEntity<String>(headers),
                new ParameterizedTypeReference<ListingInfogreffe>() {
                });

        if (response.getBody() != null) {
            return response.getBody().getItems();
        }
        return formalites;
    }

    @Override
    public FormaliteInfogreffe getInfogreffeFormalite(FormaliteInfogreffe formaliteInfogreffe)
            throws OsirisException {
        String paramIdentifiantFormalite = "";

        if (formaliteInfogreffe != null)
            paramIdentifiantFormalite = formaliteInfogreffe.getIdentifiantFormalite().getFormaliteType() + ";"
                    + formaliteInfogreffe.getIdentifiantFormalite().getEmetteurCodePart() + ";"
                    + formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero();

        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ResponseEntity<FormaliteInfogreffe> response = new RestTemplate().exchange(
                infoGreffeUrl + detailFormalite + "?identifiantFormalite=" + paramIdentifiantFormalite,
                HttpMethod.GET, new HttpEntity<String>(headers),
                FormaliteInfogreffe.class);

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    public InputStream getAttachmentFileFromEvenementInfogreffe(
            DocumentAssocieInfogreffe documentAssocieInfogreffe)
            throws OsirisException {
        SSLHelper.disableCertificateValidation();

        if (bearerValue == null || bearerExpireDateTime == null || bearerExpireDateTime.isBefore(LocalDateTime.now()))
            loginUser();

        URL url;
        try {
            url = new URL(documentAssocieInfogreffe.getUrlTelechargement());
        } catch (MalformedURLException e) {
            throw new OsirisException(e, "Wrong URL Infogreffe for document "
                    + documentAssocieInfogreffe.getUrlTelechargement());
        }
        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to open connection from URL Infogreffe for document "
                    + documentAssocieInfogreffe.getUrlTelechargement());
        }
        httpConn.setRequestProperty("Authorization", "Bearer " + bearerValue);
        httpConn.setRequestProperty("cookie",
                "BIGipServer~DMZ-Extranet-46~POO_Infogref_Prod_FRONT_7021=rd1o00000000000000000000ffffac164cc0o7021;");

        try {
            httpConn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new OsirisException(e, "Impossible to set GET Method HTTP");
        }

        int responseCode;
        try {
            responseCode = httpConn.getResponseCode();
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to get Inputstream from URL Infogreffe for document "
                    + documentAssocieInfogreffe.getUrlTelechargement());
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                return httpConn.getInputStream();
            } catch (IOException e) {
                throw new OsirisException(e, "Impossible to get Inputstream from URL Infogreffe for document "
                        + documentAssocieInfogreffe.getUrlTelechargement());
            }
        }
        return null;
    }
}
