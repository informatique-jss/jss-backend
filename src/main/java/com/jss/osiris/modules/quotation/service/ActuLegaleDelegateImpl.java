package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.quotation.model.ActuLegaleToken;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;

@Service
public class ActuLegaleDelegateImpl implements ActuLegaleDelegate {
    @Value("${actu.legale.auth.entry.point}")
    private String actuLegaleAuthEntryPoint;
    @Value("${actu.legale.auth.username}")
    private String actuLegaleAuthUsername;
    @Value("${actu.legale.auth.password}")
    private String actuLegaleAuthPassword;
    @Value("${actu.legale.auth.token}")
    private String actuLegaleAuthToken;

    @Value("${actu.legale.publish.entry.point}")
    private String actuLegalePublishEntryPoint;

    @Value("${actu.legale.publish.newpapper.id}")
    private Integer actuLegalePublishNewsPaperId;

    @Value("${actu.legale.is.test}")
    private Boolean actuLegaleIsTest;

    private String bearerValue = null;
    private LocalDateTime bearerExpireDateTime = null;

    private String authUrl = "/oauth/v2/token";
    private String publishUrl = "/annoncelegale/ads";

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

    // @Scheduled(initialDelay = 100, fixedDelay = 1000000000)
    public void test() throws OsirisException {
    }

    @SuppressWarnings({ "null" })
    private void loginUser() throws OsirisException {
        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", actuLegaleAuthToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", actuLegaleAuthUsername);
        map.add("password", actuLegaleAuthPassword);
        map.add("grant_type", "password");
        map.add("scope", "user");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<ActuLegaleToken> res;
        try {
            res = new RestTemplate().postForEntity(actuLegaleAuthEntryPoint + authUrl, request,
                    ActuLegaleToken.class);
        } catch (Exception e) {
            if (e.getMessage().contains("Connection timed out"))
                throw new OsirisException(e, "Service d'authentification d'actulégale indisponible !");
            else
                throw e;
        }
        if (res.getBody() != null) {
            bearerValue = res.getBody().getAccess_token();
            bearerExpireDateTime = LocalDateTime.now().plusSeconds(res.getBody().getExpires_in() - 100);
        }
    }

    @Override
    public ActuLegaleAnnouncement publishAnnouncement(Announcement announcement, Affaire affaire)
            throws OsirisException {
        if (announcement == null || announcement.getNotice() == null || announcement.getNotice().length() == 0
                || announcement.getPublicationDate() == null)
            throw new OsirisException(null,
                    ("Malformed announcement for Actu Légale publication for announcement n°" + (announcement != null
                            ? announcement.getId()
                            : "")));

        SSLHelper.disableCertificateValidation();
        HttpHeaders headers = createHeaders();

        ActuLegaleAnnouncement actuLegaleAnnouncement = new ActuLegaleAnnouncement();
        actuLegaleAnnouncement.setCompanyCity(affaire.getCity() != null ? affaire.getCity().getLabel() : null);
        actuLegaleAnnouncement.setCompanyName(affaire.getDenomination() != null ? affaire.getDenomination()
                : affaire.getFirstname() + " " + affaire.getLastname());
        try {
            actuLegaleAnnouncement.setCompanyZip(Integer.parseInt(affaire.getPostalCode()));
        } catch (Exception e) {
        } // Completely a foul to expect an Integer here ...
        actuLegaleAnnouncement.setCompanyAddress(affaire.getAddress());
        try {
            if (announcement.getDepartment() != null)
                actuLegaleAnnouncement.setDepartementParution(Integer.parseInt(announcement.getDepartment().getCode()));
        } catch (Exception e) {
        } // Completely a foul to expect an Integer here ...
        actuLegaleAnnouncement.setNewspaperId(actuLegalePublishNewsPaperId);
        actuLegaleAnnouncement
                .setParutionDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(announcement.getPublicationDate()));
        try {
            actuLegaleAnnouncement.setSiren(Integer.parseInt(affaire.getSiren()));
        } catch (Exception e) {
        } // Completely a foul to expect an Integer here ...
        actuLegaleAnnouncement.setTest(actuLegaleIsTest);
        actuLegaleAnnouncement.setText(announcement.getNotice().replaceAll("\r?\n", " ")
                .replaceAll("\\<.*?>", "").replaceAll("&nbsp;", " ").replaceAll("\\\\", " "));

        HttpEntity<ActuLegaleAnnouncement> request = new HttpEntity<ActuLegaleAnnouncement>(actuLegaleAnnouncement,
                headers);

        ResponseEntity<ActuLegaleAnnouncement> response = null;
        ;
        try {
            response = new RestTemplate().postForEntity(
                    actuLegalePublishEntryPoint + publishUrl, request, ActuLegaleAnnouncement.class);
        } catch (HttpClientErrorException e) {
            throw new OsirisException(e,
                    "Impossible to publish announcement to actu legale n°" + announcement.getId());
        }

        if (response != null && response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

}
