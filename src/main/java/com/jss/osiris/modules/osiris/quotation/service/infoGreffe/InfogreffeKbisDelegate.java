package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.InfogreffeDocument;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.InfogreffeSoapEnveloppeRequest;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.InfogreffeSoapEnveloppeResponse;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisCommandRequest;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisCommandResponse;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.VitrineRequest;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.VitrineResponse;

@Service
public class InfogreffeKbisDelegate {

    @Value("${infogreffe.kbis.entry.point}")
    private String infogreffeEntryPoint;

    @Value("${infogreffe.kbis.password}")
    private String infogreffePassword;

    @Value("${infogreffe.kbis.login}")
    private String infogreffeLogin;

    private RestTemplate getSoapTemplate() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);

        RestTemplate soapRestTemplate = new RestTemplate();
        soapRestTemplate.getMessageConverters().add(converter);
        return soapRestTemplate;
    }

    private VitrineResponse requestVitrineForSiren(String siren) throws OsirisException {
        SSLHelper.disableCertificateValidation();
        VitrineRequest request = new VitrineRequest(siren);
        InfogreffeSoapEnveloppeRequest requestEnvelope = new InfogreffeSoapEnveloppeRequest(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setBasicAuth(infogreffeLogin, infogreffePassword, StandardCharsets.UTF_8);
        headers.add("SOAPAction", "");

        HttpEntity<InfogreffeSoapEnveloppeRequest> entity = new HttpEntity<>(requestEnvelope, headers);

        InfogreffeSoapEnveloppeResponse responseEnvelope = getSoapTemplate().postForObject(infogreffeEntryPoint, entity,
                InfogreffeSoapEnveloppeResponse.class);

        if (responseEnvelope != null && responseEnvelope.getBody() != null) {
            if (responseEnvelope.getBody().getFault() != null) {
                throw new OsirisException("Erreur SOAP: " + responseEnvelope.getBody().getFault().faultstring);
            }
            return responseEnvelope.getBody().getVitrineResponse();
        }

        return null;
    }

    public String getExtraitIdToOrder(String siren) throws OsirisException {
        VitrineResponse reponse = requestVitrineForSiren(siren);
        if (reponse != null && reponse.getDocuments() != null) {
            Optional<InfogreffeDocument> foundDoc = reponse.getDocuments().stream()
                    .filter(doc -> "EXTRAIT".equals(doc.getType()))
                    .findFirst();

            if (!foundDoc.isEmpty())
                return foundDoc.get().getId();
        }
        return null;
    }

    public KbisCommandResponse orderDocument(String siren, String documentId) throws OsirisException {
        SSLHelper.disableCertificateValidation();
        KbisCommandRequest request = new KbisCommandRequest(siren, documentId, "T");
        InfogreffeSoapEnveloppeRequest requestEnvelope = new InfogreffeSoapEnveloppeRequest(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setBasicAuth(infogreffeLogin, infogreffePassword, StandardCharsets.UTF_8);
        headers.add("SOAPAction", "");

        HttpEntity<InfogreffeSoapEnveloppeRequest> entity = new HttpEntity<>(requestEnvelope, headers);

        InfogreffeSoapEnveloppeResponse responseEnvelope = getSoapTemplate().postForObject(infogreffeEntryPoint, entity,
                InfogreffeSoapEnveloppeResponse.class);

        if (responseEnvelope != null && responseEnvelope.getBody() != null) {
            if (responseEnvelope.getBody().getFault() != null) {
                throw new OsirisException("Erreur SOAP: " + responseEnvelope.getBody().getFault().faultstring);
            }
            KbisCommandResponse response = responseEnvelope.getBody().getCommandeResponse();
            return response;
        }

        return null;
    }
}
