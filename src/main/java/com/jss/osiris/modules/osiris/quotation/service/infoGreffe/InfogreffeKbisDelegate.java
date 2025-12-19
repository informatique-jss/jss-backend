package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

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
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(getXmlMapper());

        RestTemplate soapRestTemplate = new RestTemplate();
        soapRestTemplate.getMessageConverters().add(converter);
        return soapRestTemplate;
    }

    private XmlMapper getXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        return xmlMapper;
    }

    public String requestKbisDownloadUrlForSiret(String siret) throws OsirisException {
        SSLHelper.disableCertificateValidation();
        KbisDemand demande = new KbisDemand(
                new KbisEmiter(infogreffeLogin, infogreffePassword, new KbisRequestCode()),
                new KbisOrder(siret.substring(0, 9), siret.substring(9)));

        try {
            String demandeXmlString = getXmlMapper().writeValueAsString(demande);

            KbisArg arg0 = new KbisArg(demandeXmlString);
            KbisGetProduitsWebServicesXML op = new KbisGetProduitsWebServicesXML(arg0);

            String finalSoapRequest = String.format(
                    "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                            "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                            "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                            "xmlns:urn=\"urn:WebServicesProduits\">\n" +
                            "   <soapenv:Header/>\n" +
                            "   <soapenv:Body>\n" +
                            "      <urn:getProduitsWebServicesXML soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
                            +
                            "         <arg0 xsi:type=\"xsd:string\">\n" +
                            "            %s\n" +
                            "         </arg0>\n" +
                            "      </urn:getProduitsWebServicesXML>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>",
                    demandeXmlString);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("SOAPAction", "");

            HttpEntity<String> entity = new HttpEntity<>(finalSoapRequest, headers);

            String responseEnvelope = getSoapTemplate().postForObject(infogreffeEntryPoint, entity,
                    String.class);

            Pattern pattern = Pattern.compile("<url_acces>(.*?)</url_acces>");
            Matcher matcher = pattern.matcher(responseEnvelope);

            if (matcher.find()) {
                String rawUrl = matcher.group(1);
                return HtmlUtils.htmlUnescape(rawUrl);
            }
            return null;

        } catch (Exception e) {
            throw new OsirisException(e, "Erreur when calling  SOAP for Infogreffe with siret " + siret);
        }
    }
}