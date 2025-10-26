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
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;
import com.jss.osiris.modules.osiris.quotation.model.BaloResponse;

@Service
public class BaloDelegateServiceImpl implements BaloDelegateService {

    @Value("${balo.entry.point}")
    private String bodaccEntryPoint;

    private String recordsRequestUrl = "/records";

    @Override
    public List<BaloNotice> getBaloAfterDate(LocalDate createdAfter) {
        List<BaloNotice> balos = new ArrayList<BaloNotice>();
        int page = 0;

        List<BaloNotice> inBalos = getBaloAfterDatePaginated(page, createdAfter);
        while (inBalos.size() > 0) {
            balos.addAll(inBalos);
            page++;
            inBalos = getBaloAfterDatePaginated(page, createdAfter);
        }
        page = 0;

        return balos;
    }

    private List<BaloNotice> getBaloAfterDatePaginated(int page, LocalDate createdAfter) {
        SSLHelper.disableCertificateValidation();

        ResponseEntity<BaloResponse> response = new RestTemplate().exchange(
                bodaccEntryPoint + recordsRequestUrl + "?where=dateparution>='"
                        + createdAfter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + "'&limit=100&offset=" + (Math.max(page * 100 - 1, 0)),
                HttpMethod.GET,
                new HttpEntity<BaloResponse>(new HttpHeaders()), BaloResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults();
        }
        return null;
    }
}
