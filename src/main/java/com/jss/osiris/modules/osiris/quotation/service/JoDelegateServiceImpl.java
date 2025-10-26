package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;
import com.jss.osiris.modules.osiris.quotation.model.JoResponse;

@Service
public class JoDelegateServiceImpl implements JoDelegateService {

    @Value("${jo.entry.point}")
    private String joEntryPoint;

    private String recordsRequestUrl = "/records";

    @Override
    public List<JoNotice> getJoAfterDate(LocalDate createdAfter) {
        List<JoNotice> jos = new ArrayList<JoNotice>();
        int page = 0;

        List<JoNotice> inJos = getJoAfterDatePaginated(page, createdAfter);
        while (inJos.size() > 0) {
            jos.addAll(inJos);
            page++;
            inJos = getJoAfterDatePaginated(page, createdAfter);
        }
        page = 0;

        return jos;
    }

    private List<JoNotice> getJoAfterDatePaginated(int page, LocalDate createdAfter) {
        SSLHelper.disableCertificateValidation();

        ResponseEntity<JoResponse> response = new RestTemplate().exchange(
                joEntryPoint + recordsRequestUrl + "?where=id=dateparution>='"
                        + createdAfter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + "'&limit=100&offset=" + (Math.max(page * 100 - 1, 0)),
                HttpMethod.GET,
                new HttpEntity<JoResponse>(new HttpHeaders()), JoResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults();
        }
        return null;
    }
}
