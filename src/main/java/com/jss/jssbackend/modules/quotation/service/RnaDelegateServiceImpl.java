package com.jss.jssbackend.modules.quotation.service;

import com.jss.jssbackend.libs.SSLHelper;
import com.jss.jssbackend.modules.quotation.model.Rna;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class RnaDelegateServiceImpl implements RnaDelegateService {

	@Value("${rna.api.entrypoint}")
	private String rnaEntryPoint;

	@Value("${rna.api.path}")
	private String rnaPath;

	private String idUrl = "/id";

	@Override
	public Rna getRna(String rna) throws HttpStatusCodeException, Exception {
		try {
			SSLHelper.disableCertificateValidation();
			ResponseEntity<Rna> res = new RestTemplate().exchange(
					rnaEntryPoint + rnaPath + idUrl + "/" + rna,
					HttpMethod.GET,
					new HttpEntity<Rna>(new HttpHeaders()), Rna.class);
			if (res.getBody() != null) {
				return res.getBody();
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

}
