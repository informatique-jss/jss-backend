package com.jss.osiris.modules.quotation.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.modules.quotation.model.InseeToken;
import com.jss.osiris.modules.quotation.model.Siren;
import com.jss.osiris.modules.quotation.model.Siret;

@Service
public class SireneDelegateServiceImpl implements SireneDelegateService {
	@Value("${insee.api.sirene.key}")
	private String sireneKey;

	@Value("${insee.api.sirene.secret}")
	private String sireneSecret;

	@Value("${insee.api.entrypoint}")
	private String inseeEntryPoint;

	@Value("${insee.api.sirene.path}")
	private String inseeSirenePath;

	private String sirenUrl = "/siren";
	private String siretUrl = "/siret";
	private String tokenUrl = "/token";

	private InseeToken getToken() throws OsirisClientMessageException {
		SSLHelper.disableCertificateValidation();
		HttpHeaders headers = createHeadersForToken();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "client_credentials");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<InseeToken> res;
		try {
			res = new RestTemplate().postForEntity(inseeEntryPoint + tokenUrl, request,
					InseeToken.class);
		} catch (Exception e) {
			if (e.getMessage().contains("Connection timed out"))
				throw new OsirisClientMessageException("Service de recherche SIRENE de l'INSEE indisponible !");
			else
				throw e;
		}
		if (res.getBody() != null) {
			return res.getBody();
		}
		return null;
	}

	HttpHeaders createHeadersForToken() {
		return new HttpHeaders() {
			{
				String auth = sireneKey + ":" + sireneSecret;
				byte[] encodedAuth = Base64.getEncoder().encode(
						auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
				setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
				setContentType(MediaType.APPLICATION_JSON);
			}
		};
	}

	private HttpHeaders createHeaders() throws OsirisClientMessageException {
		return new HttpHeaders() {
			{
				String authHeader = "Bearer " + getToken().getAccess_token();
				set("Authorization", authHeader);
				setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
				setContentType(MediaType.APPLICATION_JSON);
			}
		};
	}

	@Override
	@SuppressWarnings({ "null" })
	@Cacheable(value = "siren", key = "#siren")
	public List<Siren> getSiren(String siren) throws OsirisClientMessageException {
		try {
			SSLHelper.disableCertificateValidation();
			ResponseEntity<Siren> res = new RestTemplate().exchange(
					inseeEntryPoint + inseeSirenePath + sirenUrl + "/" + siren,
					HttpMethod.GET,
					new HttpEntity<Siren>(this.createHeaders()), Siren.class);
			if (res.getBody() != null) {
				ArrayList<Siren> out = new ArrayList<Siren>();
				out.add(res.getBody());
				return out;
			}
			return null;
		} catch (HttpClientErrorException e) {
			if (e.getMessage() != null && e.getMessage().contains("Aucun élément trouvé pour le siren"))
				return null;
			else
				throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@SuppressWarnings({ "null" })
	@Cacheable(value = "siret", key = "#siret")
	public List<Siret> getSiret(String siret) throws OsirisClientMessageException {
		try {
			SSLHelper.disableCertificateValidation();

			ResponseEntity<Siret> res = new RestTemplate().exchange(
					inseeEntryPoint + inseeSirenePath + siretUrl + "/" + siret,
					HttpMethod.GET,
					new HttpEntity<Siret>(this.createHeaders()), Siret.class);
			if (res.getBody() != null) {
				ArrayList<Siret> out = new ArrayList<Siret>();
				out.add(res.getBody());
				return out;
			}
			return null;
		} catch (HttpClientErrorException e) {
			if (e.getMessage() != null && e.getMessage().contains("Aucun élément trouvé pour le siren"))
				return null;
			else
				throw e;
		} catch (Exception e) {
			throw e;
		}
	}

}
