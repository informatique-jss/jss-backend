package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.SSLHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.repository.infoGreffe.KbisRequestRepository;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;

@org.springframework.stereotype.Service
public class InfogreffeKbisServiceImpl implements InfogreffeKbisService {

    @Autowired
    InfogreffeKbisDelegate infogreffeKbisDelegate;

    @Autowired
    KbisRequestRepository kbisRequestRepository;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    BatchService batchService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Value("${infogreffe.kbis.password}")
    private String infogreffePassword;

    @Value("${infogreffe.kbis.login}")
    private String infogreffeLogin;

    @Override
    public KbisRequest getRequest(Integer id) {
        Optional<KbisRequest> request = kbisRequestRepository.findById(id);
        if (request.isPresent())
            return request.get();
        return null;
    }

    @Override
    public KbisRequest addOrUpdateKbisRequest(KbisRequest kbisRequest) {
        return kbisRequestRepository.save(kbisRequest);
    }

    @Override
    public KbisRequest orderNewKbisForSiret(String siren, Provision provision) throws OsirisException {
        KbisRequest request = new KbisRequest();
        request.setSiret(siren);
        request.setEmployeeInitiator(employeeService.getCurrentEmployee());
        request.setProvision(provision);
        addOrUpdateKbisRequest(request);

        batchService.declareNewBatch(Batch.ORDER_KBIS, request.getId());

        return request;
    }

    @Override
    public Attachment getUpToDateKbisForSiret(String siren) throws OsirisException {
        List<KbisRequest> requests = kbisRequestRepository.findBySiret(siren);
        if (requests != null && requests.size() > 0) {
            List<Affaire> affaires = affaireService.getAffairesBySiren(siren);

            requests.sort(new Comparator<KbisRequest>() {
                @Override
                public int compare(KbisRequest request1, KbisRequest request2) {

                    if (request1 != null && request2 == null)
                        return 1;
                    if (request1 == null && request2 != null)
                        return -1;
                    if (request1.getDateOrder() == null && request2.getDateOrder() == null)
                        return 0;
                    if (request1.getDateOrder() != null && request2.getDateOrder() == null)
                        return 1;
                    if (request1.getDateOrder() == null && request2.getDateOrder() != null)
                        return -1;
                    return request2.getDateOrder().compareTo(request1.getDateOrder());
                }
            });

            if (affaires != null) {
                LocalDate lastUpdate = affaires.stream()
                        .map(a -> (a.getLastRneUpdate() != null ? a.getLastRneUpdate()
                                : LocalDate.now().minusYears(100)))
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now().minusYears(100));
                KbisRequest request = requests.get(0);
                if (request.getDateOrder() != null && request.getDateOrder().isAfter(LocalDate.now().minusMonths(3))) {
                    if (request.getDateOrder().isAfter(lastUpdate))
                        return request.getAttachment();
                }

                AttachmentType kbisType = constantService.getAttachmentTypeKbis();
                for (Affaire affaire : affaires) {
                    List<AssoAffaireOrder> assos = assoAffaireOrderService.getAssoAffaireOrderByAffaire(affaire);
                    if (assos != null)
                        for (AssoAffaireOrder asso : assos) {
                            if (asso.getCustomerOrder() != null) {
                                for (AssoAffaireOrder assoToSearch : asso.getCustomerOrder().getAssoAffaireOrders()) {
                                    if (assoToSearch.getServices() != null) {
                                        for (Service service : assoToSearch.getServices()) {
                                            if (service.getProvisions() != null) {
                                                for (Provision provision : service.getProvisions()) {
                                                    if (provision.getAttachments() != null) {
                                                        for (Attachment attachment : provision.getAttachments()) {
                                                            if (!Boolean.TRUE.equals(attachment.getIsDisabled())
                                                                    && attachment.getAttachmentType().getId()
                                                                            .equals(kbisType.getId())) {
                                                                if (attachment.getAttachmentDate() != null && attachment
                                                                        .getAttachmentDate()
                                                                        .isAfter(LocalDate.now().minusMonths(3))) {
                                                                    if (attachment.getAttachmentDate()
                                                                            .isAfter(lastUpdate))
                                                                        return attachment;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
        return null;
    }

    @Override
    public void orderKbis(Integer requestId) throws OsirisException {
        KbisRequest request = getRequest(requestId);
        if (request != null) {
            if (request.getSiret() == null)
                throw new OsirisException("No siret defined for request " + requestId);

            if (request.getUrlTelechargement() == null || request.getUrlTelechargement().length() == 0) {

                String url = infogreffeKbisDelegate.requestKbisDownloadUrlForSiret(request.getSiret());
                request.setDateOrder(LocalDate.now());
                request.setUrlTelechargement(url);
                addOrUpdateKbisRequest(request);

                if (url == null || url.length() == 0)
                    throw new OsirisException("no url found for download document of request " + requestId);

                downloadAttachment(request);
            }

        }
    }

    private void downloadAttachment(KbisRequest kbisRequest) throws OsirisException {
        SSLHelper.disableCertificateValidation();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(infogreffeLogin, infogreffePassword, StandardCharsets.UTF_8);

        RequestCallback requestCallback = request -> request.getHeaders().addAll(headers);

        ResponseExtractor<ResponseEntity<byte[]>> responseExtractor = response -> ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(StreamUtils.copyToByteArray(response.getBody())); // On lit tout de suite

        ResponseEntity<byte[]> response = new RestTemplate().execute(
                kbisRequest.getUrlTelechargement(),
                HttpMethod.GET,
                requestCallback,
                responseExtractor);

        if (response != null && response.getBody() != null && response.getStatusCode().is2xxSuccessful()) {
            String name = "Kbis-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                    + ".pdf";
            try (InputStream is = new ByteArrayInputStream(response.getBody())) {
                List<Attachment> attachments = attachmentService.addAttachment(is,
                        kbisRequest.getProvision().getId(), null, Provision.class.getSimpleName(),
                        constantService.getAttachmentTypeKbis(), name, false, name, null, null, null);

                for (Attachment attachment : attachments) {
                    if (attachment.getDescription().equals(name)) {
                        kbisRequest.setAttachment(attachment);
                        addOrUpdateKbisRequest(kbisRequest);
                        break;
                    }
                }
            } catch (IOException e) {
                throw new OsirisException(e, "Error during download of kbis for request " + kbisRequest.getId());
            }
            Provision provision = provisionService.getProvision(kbisRequest.getProvision().getId());
            if (provision != null) {
                provision.setKbisOrderedNumber(
                        provision.getKbisOrderedNumber() != null ? provision.getKbisOrderedNumber() + 1 : 1);
                provisionService.addOrUpdateProvision(provision);
            }

            notificationService.notifyKbisAddToProvision(kbisRequest.getProvision(), kbisRequest);
        } else {
            throw new OsirisException("Impossible to download kbis for request " + kbisRequest.getId() + ". Code HTTP: "
                    + (response != null ? response.getStatusCode() : "N/A"));
        }
    }

}
