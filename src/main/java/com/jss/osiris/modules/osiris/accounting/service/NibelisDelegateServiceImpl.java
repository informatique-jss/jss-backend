package com.jss.osiris.modules.osiris.accounting.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.BulletinLigne;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.BulletinResponse;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.LoginNibelis;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.LoginResponse;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.Salarie;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.SalariesResponse;

@Service
public class NibelisDelegateServiceImpl implements NibelisDelegateService {

    @Value("${nibelis.api.baseurl}")
    private String apiBaseUrl;

    @Value("${nibelis.api.username}")
    private String username;

    @Value("${nibelis.api.password}")
    private String password;

    private String loginPath = "/portail/users/login";

    private String salariesPath = "/api/salaries";

    private String bulletinPath = "/api/bulletin/bulletin-salarie";

    private String accessToken = null;
    private long tokenExpirationTime;

    @Override
    public List<Salarie> getSalaries() throws OsirisException {
        checkTokenValidity();
        String url = apiBaseUrl + salariesPath;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<SalariesResponse> response = new RestTemplate().exchange(
                    url, HttpMethod.GET, entity, SalariesResponse.class);

            return response.getBody().getData();
        } catch (Exception e) {
            throw new OsirisException(e, "Error when grabbing Nibelis salaries : ");
        }
    }

    @Override
    public List<BulletinLigne> getBulletin(Long matricule, LocalDate period) throws OsirisException {
        checkTokenValidity();
        String url = apiBaseUrl + bulletinPath + "?type_bulletin=COMP&id_nibelis=" + matricule + "&periode="
                + period.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<BulletinResponse> response = new RestTemplate().exchange(
                    url, HttpMethod.GET, entity, BulletinResponse.class);

            return response.getBody().getData();
        } catch (Exception e) {
            throw new OsirisException(e, "Error when grabbing Nibelis wages for  " + matricule);
        }
    }

    private void login() throws OsirisException {
        try {
            String url = apiBaseUrl + loginPath;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            LoginNibelis loginNibelis = new LoginNibelis();
            loginNibelis.setEmail(username);
            loginNibelis.setPassword(password);

            HttpEntity<LoginNibelis> entity = new HttpEntity<LoginNibelis>(loginNibelis, headers);
            ResponseEntity<LoginResponse> response = new RestTemplate().exchange(
                    url, HttpMethod.POST, entity, LoginResponse.class);

            LoginResponse.LoginData loginData = response.getBody().getData();
            this.accessToken = loginData.getAccess_token();
            this.tokenExpirationTime = System.currentTimeMillis() + 3600000;

        } catch (Exception e) {
            throw new OsirisException(e, "Error when authenticating Nibelis");
        }
    }

    private void checkTokenValidity() throws OsirisException {
        if (accessToken == null || System.currentTimeMillis() > tokenExpirationTime) {
            login();
        }
    }
}