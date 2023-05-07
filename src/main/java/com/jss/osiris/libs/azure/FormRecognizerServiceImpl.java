package com.jss.osiris.libs.azure;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzedDocument;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;

@Service
public class FormRecognizerServiceImpl implements FormRecognizerService {

    @Value("${azure.form.recognizer.endpoint}")
    private String azureApiEndPoint;

    @Value("${azure.form.recognizer.api.key}")
    private String azureApiKey;

    @Value("${azure.form.recognizer.model.name}")
    private String modelId;

    @Value("${azure.form.recognizer.confidence.threshold}")
    private Integer confidenceThreshold;

    @Autowired
    AzureInvoiceService azureInvoiceService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Override
    public AzureInvoice recongnizeInvoice(Attachment attachment) throws OsirisException {
        Float confidenceLimit = confidenceThreshold / 100f;

        if (attachment == null)
            throw new OsirisException(null, "No attachment provided");

        if (attachment.getUploadedFile() == null)
            throw new OsirisException(null, "No uploaded file in attachment provided");

        byte[] fileData;
        try {
            File file = new File(attachment.getUploadedFile().getPath());
            fileData = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new OsirisException(e, "File not found " + attachment.getUploadedFile().getPath());
        }

        AnalyzeResult analyzeResult = new DocumentAnalysisClientBuilder().endpoint(azureApiEndPoint)
                .credential(new AzureKeyCredential(azureApiKey)).buildClient()
                .beginAnalyzeDocument(modelId, BinaryData.fromBytes(fileData)).getFinalResult();

        final AnalyzedDocument analyzedDocument = analyzeResult.getDocuments().get(0);

        AzureInvoice azureInvoice = new AzureInvoice();
        azureInvoice.setGlobalDocumentConfidence(analyzedDocument.getConfidence());
        azureInvoice.setToCheck(true);
        mapInvoice(azureInvoice, analyzedDocument);

        // Already done
        if (azureInvoice.getInvoiceId() != null) {
            AzureInvoice potentialAzureInvoice = azureInvoiceService
                    .getAzureInvoiceByInvoiceId(azureInvoice.getInvoiceId());
            if (potentialAzureInvoice != null) {
                potentialAzureInvoice.getAttachments().add(attachment);
                azureInvoiceService.addOrUpdateAzureInvoice(potentialAzureInvoice);
                attachment.setAzureInvoice(potentialAzureInvoice);
                attachmentService.addOrUpdateAttachment(attachment);
                return potentialAzureInvoice;
            }
        }

        checkInvoiceAmountConfidence(azureInvoice);
        azureInvoice.setAttachments(Arrays.asList(attachment));
        attachment.setAzureInvoice(azureInvoice);
        attachmentService.addOrUpdateAttachment(attachment);

        if (azureInvoice.getInvoiceTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceTaxTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceNonTaxableTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoicePreTaxTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceDateConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceIdConfidence() >= confidenceLimit
                && azureInvoice.getVendorTaxIdConfidence() >= confidenceLimit)
            azureInvoice.setToCheck(false);

        return azureInvoiceService.addOrUpdateAzureInvoice(azureInvoice);
    }

    private AzureInvoice mapInvoice(AzureInvoice azureInvoice, AnalyzedDocument analyzedDocument) {
        analyzedDocument.getFields().forEach((key, documentField) -> {
            if (key.equals("CustomerId")) {
                if (documentField.getValue() != null)
                    azureInvoice.setCustomerId((String) documentField.getValue());
                azureInvoice.setCustomerIdConfidence(documentField.getConfidence());
            } else if (key.equals("Reference")) {
                if (documentField.getValue() != null)
                    azureInvoice.setReference((String) documentField.getValue());
                azureInvoice.setReferenceConfidence(documentField.getConfidence());
            } else if (key.equals("InvoiceDate")) {
                LocalDate invoiceDate = null;
                if (documentField.getValue() != null) {
                    invoiceDate = (LocalDate) documentField.getValue();
                } else if (documentField.getContent() != null) {
                    try {
                        invoiceDate = LocalDate.parse((String) documentField.getContent(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (DateTimeParseException e) {
                        try {
                            invoiceDate = LocalDate.parse((String) documentField.getContent(),
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } catch (DateTimeParseException e2) {
                            try {
                                invoiceDate = LocalDate.parse((String) documentField.getContent(),
                                        DateTimeFormatter.ofPattern("d MMMM u", Locale.FRENCH));
                            } catch (DateTimeParseException e3) {
                            }
                        }
                    }
                }
                azureInvoice.setInvoiceDate(invoiceDate);
                azureInvoice.setInvoiceDateConfidence(invoiceDate != null ? documentField.getConfidence() : 0f);
            } else if (key.equals("InvoiceId")) {
                if (documentField.getValue() != null)
                    azureInvoice
                            .setInvoiceId(((String) documentField.getValue()).toUpperCase().trim().replaceAll(" ", ""));
                azureInvoice.setInvoiceIdConfidence(documentField.getConfidence());
            } else if (key.equals("InvoiceTotal")) {
                Float invoiceTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceTotal = ((Double) documentField.getValue()).floatValue() / 100f;
                }
                azureInvoice.setInvoiceTotal(invoiceTotal);
                azureInvoice.setInvoiceTotalConfidence(invoiceTotal != null ? documentField.getConfidence() : 0f);
            } else if (key.equals("InvoicePreTaxTotal")) {
                Float invoicePreTaxTotal = 0f;
                if (documentField.getValue() != null) {
                    invoicePreTaxTotal = ((Double) documentField.getValue()).floatValue() / 100f;
                }
                azureInvoice.setInvoicePreTaxTotal(invoicePreTaxTotal);
                azureInvoice.setInvoicePreTaxTotalConfidence(
                        invoicePreTaxTotal != null ? documentField.getConfidence() : 0f);
            } else if (key.equals("InvoiceTaxTotal")) {
                Float invoiceTaxTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceTaxTotal = ((Double) documentField.getValue()).floatValue() / 100f;
                }
                azureInvoice.setInvoiceTaxTotal(invoiceTaxTotal);
                azureInvoice.setInvoiceTaxTotalConfidence(invoiceTaxTotal != null ? documentField.getConfidence() : 0f);
            } else if (key.equals("InvoiceNonTaxableTotal")) {
                Float invoiceNonTaxableTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceNonTaxableTotal = ((Double) documentField.getValue()).floatValue() / 100f;
                }
                azureInvoice.setInvoiceNonTaxableTotal(invoiceNonTaxableTotal);
                azureInvoice.setInvoiceNonTaxableTotalConfidence(
                        invoiceNonTaxableTotal != null ? documentField.getConfidence() : 0f);
            } else if (key.equals("VendorTaxId")) {
                if (documentField.getValue() != null) {
                    azureInvoice.setVendorTaxId(
                            ((String) documentField.getValue()).toUpperCase().trim().replaceAll(" ", ""));

                    CompetentAuthority competentAuthority = null;
                    competentAuthority = competentAuthorityService
                            .getCompetentAuthorityByIntercommunityVat(azureInvoice.getVendorTaxId());
                    if (competentAuthority == null)
                        competentAuthority = competentAuthorityService
                                .getCompetentAuthorityByAzureCustomReference(azureInvoice.getVendorTaxId());

                    if (competentAuthority != null)
                        azureInvoice.setCompetentAuthority(competentAuthority);
                }
                azureInvoice.setVendorTaxIdConfidence(
                        azureInvoice.getCompetentAuthority() != null ? documentField.getConfidence() : 0f);
            }
        });
        return azureInvoice;
    }

    // If sum of all field is correct => confidence full for everyfield
    // Else if one field is not confident and other are, correct last field with
    // difference
    private AzureInvoice checkInvoiceAmountConfidence(AzureInvoice azureInvoice) {
        Float confidenceLimit = confidenceThreshold / 100f;
        boolean resetConfidence = false;

        if (azureInvoice.getInvoiceTotal() == (azureInvoice.getInvoicePreTaxTotal() + azureInvoice.getInvoiceTaxTotal()
                + azureInvoice.getInvoiceNonTaxableTotal())) {
            resetConfidence = true;
        }

        int nbrConfident = 0;
        if (azureInvoice.getInvoiceTotalConfidence() >= confidenceLimit)
            nbrConfident++;
        if (azureInvoice.getInvoicePreTaxTotal() >= confidenceLimit)
            nbrConfident++;
        if (azureInvoice.getInvoiceTaxTotal() >= confidenceLimit)
            nbrConfident++;
        if (azureInvoice.getInvoiceNonTaxableTotal() >= confidenceLimit)
            nbrConfident++;

        if (nbrConfident == 3) {
            if (azureInvoice.getInvoiceTotalConfidence() < confidenceLimit)
                azureInvoice.setInvoiceTotal(azureInvoice.getInvoiceTaxTotal()
                        + azureInvoice.getInvoiceNonTaxableTotal() + azureInvoice.getInvoicePreTaxTotal());
            if (azureInvoice.getInvoicePreTaxTotal() < confidenceLimit)
                azureInvoice.setInvoicePreTaxTotal(azureInvoice.getInvoiceTotal() - azureInvoice.getInvoiceTaxTotal()
                        - azureInvoice.getInvoiceNonTaxableTotal());
            if (azureInvoice.getInvoiceTaxTotal() < confidenceLimit)
                azureInvoice.setInvoiceTaxTotal(azureInvoice.getInvoiceTotal() - azureInvoice.getInvoicePreTaxTotal()
                        - azureInvoice.getInvoiceNonTaxableTotal());
            if (azureInvoice.getInvoiceNonTaxableTotal() < confidenceLimit)
                azureInvoice
                        .setInvoiceNonTaxableTotal(azureInvoice.getInvoiceTotal() - azureInvoice.getInvoiceTaxTotal()
                                - azureInvoice.getInvoicePreTaxTotal());

            resetConfidence = true;
        }

        if (resetConfidence) {
            azureInvoice.setInvoiceTotalConfidence(Math.max(azureInvoice.getInvoiceTotalConfidence(), confidenceLimit));
            azureInvoice
                    .setInvoicePreTaxTotalConfidence(Math.max(azureInvoice.getInvoicePreTaxTotal(), confidenceLimit));
            azureInvoice.setInvoiceTaxTotalConfidence(Math.max(azureInvoice.getInvoiceTaxTotal(), confidenceLimit));
            azureInvoice.setInvoiceNonTaxableTotalConfidence(
                    Math.max(azureInvoice.getInvoiceNonTaxableTotal(), confidenceLimit));
        }

        return azureInvoice;
    }
}
