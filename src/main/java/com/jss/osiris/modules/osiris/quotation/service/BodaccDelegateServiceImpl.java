package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;
import com.jss.osiris.modules.osiris.quotation.model.BodaccResponse;

@Service
public class BodaccDelegateServiceImpl implements BodaccDelegateService {

    @Value("${bodacc.entry.point}")
    private String bodaccEntryPoint;

    private String recordsRequestUrl = "/records";

    @Override
    public List<BodaccNotice> getBodaccAfterDate(LocalDate createdAfter) {
        List<BodaccNotice> bodaccs = new ArrayList<BodaccNotice>();
        int page = 0;
        int department = 1;

        while (department < 1000) {
            List<BodaccNotice> inBodaccs = getBodaccAfterDatePaginated(page, department, createdAfter);
            while (inBodaccs.size() > 0) {
                bodaccs.addAll(inBodaccs);
                page++;
                inBodaccs = getBodaccAfterDatePaginated(page, department, createdAfter);
            }
            page = 0;
            department++;
            if (department == 100)
                department = 970;
        }

        return bodaccs;
    }

    private List<BodaccNotice> getBodaccAfterDatePaginated(int page, int departement, LocalDate createdAfter) {
        SSLHelper.disableCertificateValidation();

        ResponseEntity<BodaccResponse> response = new RestTemplate().exchange(
                bodaccEntryPoint + recordsRequestUrl + "?where=numerodepartement="
                        + StringUtils.leftPad(departement + "", 2, "0") + " and dateparution>='"
                        + createdAfter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + "'&limit=100&offset=" + (Math.max(page * 100 - 1, 0)),
                HttpMethod.GET,
                new HttpEntity<BodaccResponse>(new HttpHeaders()), BodaccResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults();
        }
        return null;
    }

}
