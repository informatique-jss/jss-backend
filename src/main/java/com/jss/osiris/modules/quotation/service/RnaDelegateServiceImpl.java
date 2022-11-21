package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.modules.quotation.model.Rna;

@Service
public class RnaDelegateServiceImpl implements RnaDelegateService {

	@Value("${rna.api.entrypoint}")
	private String rnaEntryPoint;

	@Value("${rna.api.path}")
	private String rnaPath;

	private String idUrl = "/id";

	@Override
	@Cacheable(value = "rna", key = "#rna")
	public List<Rna> getRna(String rna) {
		try {
			SSLHelper.disableCertificateValidation();
			ResponseEntity<Rna> res = new RestTemplate().exchange(
					rnaEntryPoint + rnaPath + idUrl + "/" + rna,
					HttpMethod.GET,
					new HttpEntity<Rna>(new HttpHeaders()), Rna.class);
			if (res.getBody() != null) {
				ArrayList<Rna> out = new ArrayList<Rna>();
				out.add(res.getBody());
				return out;
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

}
