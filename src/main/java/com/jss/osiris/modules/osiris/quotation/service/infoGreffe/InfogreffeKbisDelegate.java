package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

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
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisDemand;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisEmiter;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisOrder;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequestCode;

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
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);

        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);

        RestTemplate soapRestTemplate = new RestTemplate();
        soapRestTemplate.getMessageConverters().add(converter);
        return soapRestTemplate;
    }

    public String requestKbisDownloadUrlForSiret(String siret) throws OsirisException {
        SSLHelper.disableCertificateValidation();
        KbisDemand demande = new KbisDemand(
                new KbisEmiter(infogreffeLogin, infogreffePassword, new KbisRequestCode()),
                new KbisOrder(siret.substring(0, 6), siret.substring(7)));

        KbisCall appelService = new KbisCall(demande);
        KbisSoapBody soapBody = new KbisSoapBody(appelService);
        KbisSoapEnveloppe soapEnvelope = new KbisSoapEnveloppe(soapBody);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("SOAPAction", "");

            HttpEntity<KbisSoapEnveloppe> entity = new HttpEntity<>(soapEnvelope, headers);

            KbisSoapResponseEnvelope responseEnvelope = getSoapTemplate().postForObject(infogreffeEntryPoint, entity,
                    KbisSoapResponseEnvelope.class);

            KbisResult resultat = responseEnvelope.getBody()
                    .getReponseService()
                    .getResultat();

            if (resultat.isOrderInProgress()) {
                return resultat.getUrlAcces();
            } else {
                throw new OsirisException("API Infogreffe error: " + resultat.getLibelleRetour());
            }

        } catch (Exception e) {
            throw new OsirisException(e, "Erreur when calling  SOAP for Infogreffe with siret " + siret);
        }
    }
}