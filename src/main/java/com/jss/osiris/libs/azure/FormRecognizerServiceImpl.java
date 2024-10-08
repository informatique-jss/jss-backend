package com.jss.osiris.libs.azure;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.osiris.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.AzureReceiptService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;

@Service
public class FormRecognizerServiceImpl implements FormRecognizerService {

    @Value("${azure.form.recognizer.endpoint}")
    private String azureApiEndPoint;

    @Value("${azure.form.recognizer.api.key}")
    private String azureApiKey;

    @Value("${azure.form.recognizer.model.invoices.name}")
    private String invoicesModelId;

    @Value("${azure.form.recognizer.model.receipts.name}")
    private String receiptsModelId;

    @Value("${azure.form.recognizer.confidence.threshold}")
    private Integer confidenceThreshold;

    @Autowired
    AzureInvoiceService azureInvoiceService;

    @Autowired
    AzureReceiptService azureReceiptService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Override
    public AzureInvoice recongnizeInvoice(Attachment attachment) throws OsirisException {
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
                .beginAnalyzeDocument(invoicesModelId, BinaryData.fromBytes(fileData)).getFinalResult();

        final AnalyzedDocument analyzedDocument = analyzeResult.getDocuments().get(0);

        AzureInvoice azureInvoice = new AzureInvoice();
        azureInvoice.setIsDisabled(false);
        azureInvoice.setGlobalDocumentConfidence(
                Double.parseDouble(Float.toString(analyzeResult.getDocuments().get(0).getConfidence())));
        azureInvoice.setModelUsed(analyzeResult.getDocuments().get(0).getDocType());
        azureInvoice.setToCheck(true);
        mapInvoice(azureInvoice, analyzedDocument);

        // Already done
        if (azureInvoice.getInvoiceId() != null) {
            AzureInvoice potentialAzureInvoice = azureInvoiceService
                    .getAzureInvoiceByInvoiceId(azureInvoice.getInvoiceId());
            if (potentialAzureInvoice != null) {
                attachment.setAzureInvoice(potentialAzureInvoice);
                attachmentService.addOrUpdateAttachment(attachment);
                return potentialAzureInvoice;
            }
        }

        checkInvoiceAmountConfidence(azureInvoice);
        azureInvoice.setAttachments(Arrays.asList(attachment));
        attachment.setAzureInvoice(azureInvoice);
        attachmentService.addOrUpdateAttachment(attachment);

        return azureInvoiceService.addOrUpdateAzureInvoice(azureInvoice);
    }

    private AzureInvoice mapInvoice(AzureInvoice azureInvoice, AnalyzedDocument analyzedDocument) {
        analyzedDocument.getFields().forEach((key, documentField) -> {
            if (key.equals("CustomerId")) {
                if (documentField.getValue() != null)
                    azureInvoice.setCustomerId((String) documentField.getValue());
                azureInvoice.setCustomerIdConfidence(Double.parseDouble(Float.toString(documentField.getConfidence())));
            } else if (key.equals("Reference")) {
                if (documentField.getValue() != null)
                    azureInvoice.setReference((String) documentField.getValue());
                azureInvoice.setReferenceConfidence(Double.parseDouble(Float.toString(documentField.getConfidence())));
            } else if (key.equals("InvoiceDate")) {
                LocalDate invoiceDate = null;
                if (documentField.getValue() != null) {
                    invoiceDate = (LocalDate) documentField.getValue();
                } else if (documentField.getContent() != null) {
                    String content = (String) documentField.getContent();
                    content = content.replaceAll(".", "").replaceAll(",", "").trim();
                    try {
                        invoiceDate = LocalDate.parse(content,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (DateTimeParseException e) {
                        try {
                            invoiceDate = LocalDate.parse(content,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } catch (DateTimeParseException e2) {
                            try {
                                DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                                builder.parseCaseInsensitive();
                                builder.appendPattern("d MMMM yyyy");
                                DateTimeFormatter dateFormat = builder.toFormatter();

                                invoiceDate = LocalDate.parse(content, dateFormat);
                            } catch (DateTimeParseException e3) {
                            }
                        }
                    }
                }
                azureInvoice.setInvoiceDate(invoiceDate);
                azureInvoice.setInvoiceDateConfidence(
                        invoiceDate != null ? Double.parseDouble(Float.toString(documentField.getConfidence())) : 0.f);
            } else if (key.equals("InvoiceId")) {
                if (documentField.getValue() != null)
                    azureInvoice
                            .setInvoiceId(((String) documentField.getValue()).toUpperCase().trim().replaceAll(" ", ""));
                azureInvoice.setInvoiceIdConfidence(Double.parseDouble(Float.toString(documentField.getConfidence())));
            } else if (key.equals("InvoiceTotal")) {
                Float invoiceTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceTotal = ((Double) documentField.getValue()).floatValue();
                }
                azureInvoice.setInvoiceTotal(Double.parseDouble(Float.toString(invoiceTotal)));
                azureInvoice.setInvoiceTotalConfidence(
                        invoiceTotal != null ? Double.parseDouble(Float.toString(documentField.getConfidence())) : 0f);
            } else if (key.equals("InvoicePreTaxTotal")) {
                Float invoicePreTaxTotal = 0f;
                if (documentField.getValue() != null) {
                    invoicePreTaxTotal = ((Double) documentField.getValue()).floatValue();
                }
                azureInvoice.setInvoicePreTaxTotal(Double.parseDouble(Float.toString(invoicePreTaxTotal)));
                azureInvoice.setInvoicePreTaxTotalConfidence(
                        invoicePreTaxTotal != null ? Double.parseDouble(Float.toString(documentField.getConfidence()))
                                : 0f);
            } else if (key.equals("InvoiceTaxTotal")) {
                Float invoiceTaxTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceTaxTotal = ((Double) documentField.getValue()).floatValue();
                }
                azureInvoice.setInvoiceTaxTotal(Double.parseDouble(Float.toString(invoiceTaxTotal)));
                azureInvoice.setInvoiceTaxTotalConfidence(
                        invoiceTaxTotal != null ? Double.parseDouble(Float.toString(documentField.getConfidence()))
                                : 0f);
            } else if (key.equals("InvoiceNonTaxableTotal")) {
                Float invoiceNonTaxableTotal = 0f;
                if (documentField.getValue() != null) {
                    invoiceNonTaxableTotal = ((Double) documentField.getValue()).floatValue();
                }
                azureInvoice.setInvoiceNonTaxableTotal(Double.parseDouble(Float.toString(invoiceNonTaxableTotal)));
                azureInvoice.setInvoiceNonTaxableTotalConfidence(
                        invoiceNonTaxableTotal != null
                                ? Double.parseDouble(Float.toString(documentField.getConfidence()))
                                : 0f);
            } else if (key.equals("VendorTaxId")) {
                if (documentField.getValue() != null) {
                    azureInvoice.setVendorTaxId(
                            ((String) documentField.getValue()).toUpperCase().trim().replaceAll(" ", ""));

                    CompetentAuthority competentAuthority = null;
                    if (azureInvoice.getVendorTaxId() != null && !azureInvoice.getVendorTaxId().equals("")) {
                        competentAuthority = competentAuthorityService
                                .getCompetentAuthorityByIntercommunityVat(azureInvoice.getVendorTaxId());
                        if (competentAuthority == null)
                            competentAuthority = competentAuthorityService
                                    .getCompetentAuthorityByAzureCustomReference(azureInvoice.getVendorTaxId());
                    }

                    if (competentAuthority != null)
                        azureInvoice.setCompetentAuthority(competentAuthority);
                }
                azureInvoice.setVendorTaxIdConfidence(
                        azureInvoice.getCompetentAuthority() != null
                                ? Double.parseDouble(Float.toString(documentField.getConfidence()))
                                : 0f);
            }
        });
        return azureInvoice;
    }

    // If sum of all field is correct => confidence full for everyfield
    // Else if one field is not confident and other are, correct last field with
    // difference
    @Override
    public AzureInvoice checkInvoiceAmountConfidence(AzureInvoice azureInvoice) {
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
            resetConfidence = true;
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

            if (azureInvoice.getInvoiceTotal() < 0 || azureInvoice.getInvoicePreTaxTotal() < 0 ||
                    azureInvoice.getInvoiceTaxTotal() < 0 || azureInvoice.getInvoiceNonTaxableTotal() < 0)
                resetConfidence = false;
        }

        if (resetConfidence) {
            azureInvoice.setInvoiceTotalConfidence(Math.max(azureInvoice.getInvoiceTotalConfidence(), confidenceLimit));
            azureInvoice
                    .setInvoicePreTaxTotalConfidence(Math.max(azureInvoice.getInvoicePreTaxTotal(), confidenceLimit));
            azureInvoice.setInvoiceTaxTotalConfidence(Math.max(azureInvoice.getInvoiceTaxTotal(), confidenceLimit));
            azureInvoice.setInvoiceNonTaxableTotalConfidence(
                    Math.max(azureInvoice.getInvoiceNonTaxableTotal(), confidenceLimit));
        }

        if (azureInvoice.getInvoiceTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceTaxTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceNonTaxableTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoicePreTaxTotalConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceDateConfidence() >= confidenceLimit
                && azureInvoice.getInvoiceIdConfidence() >= confidenceLimit
                && azureInvoice.getVendorTaxIdConfidence() >= confidenceLimit)
            azureInvoice.setToCheck(false);

        return azureInvoice;
    }

    @Override
    public AzureReceipt recongnizeRecipts(Attachment attachment) throws OsirisException {
        if (attachment == null)
            throw new OsirisException(null, "No attachment provided");

        if (attachment.getUploadedFile() == null)
            throw new OsirisException(null, "No uploaded file in attachment provided");

        if (attachment.getCompetentAuthority() == null)
            throw new OsirisException(null, "Not a comptetent authority attachment");

        byte[] fileData;
        try {
            File file = new File(attachment.getUploadedFile().getPath());
            fileData = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new OsirisException(e, "File not found " + attachment.getUploadedFile().getPath());
        }

        AnalyzeResult analyzeResult = new DocumentAnalysisClientBuilder().endpoint(azureApiEndPoint)
                .credential(new AzureKeyCredential(azureApiKey)).buildClient()
                .beginAnalyzeDocument(receiptsModelId, BinaryData.fromBytes(fileData)).getFinalResult();

        final AnalyzedDocument analyzedDocument = analyzeResult.getDocuments().get(0);

        AzureReceipt azureReceipt = new AzureReceipt();
        azureReceipt.setModelUsed(analyzeResult.getDocuments().get(0).getDocType());
        azureReceipt.setGlobalDocumentConfidence(
                Double.parseDouble(Float.toString(analyzeResult.getDocuments().get(0).getConfidence())));
        azureReceipt.setAzureReceiptInvoices(new ArrayList<AzureReceiptInvoice>());
        analyzedDocument.getFields().forEach((key, documentField) -> {
            if (key.equals("Invoices")) {
                List<com.azure.ai.formrecognizer.documentanalysis.models.DocumentField> fields = documentField
                        .getValueAsList();

                Float currentInvoiceTotal = null;
                String currentInvoiceId = null;
                AzureReceiptInvoice receiptInvoice = new AzureReceiptInvoice();
                for (com.azure.ai.formrecognizer.documentanalysis.models.DocumentField field : fields) {
                    Map<String, com.azure.ai.formrecognizer.documentanalysis.models.DocumentField> fieldMap = field
                            .getValueAsMap();

                    for (String keyMap : fieldMap.keySet()) {
                        // If some field are null, complete with next one
                        // Occurs when invoice is on two pages
                        if (currentInvoiceId != null && currentInvoiceTotal != null) {
                            receiptInvoice = new AzureReceiptInvoice();
                            currentInvoiceTotal = null;
                            currentInvoiceId = null;
                        }
                        receiptInvoice.setAzureReceipt(azureReceipt);

                        if (keyMap.equals("InvoiceTotal") && fieldMap.get(keyMap).getValue() != null) {
                            currentInvoiceTotal = ((Double) fieldMap.get(keyMap).getValue()).floatValue();
                            receiptInvoice.setInvoiceTotal(Double.parseDouble(Float.toString(currentInvoiceTotal)));
                        } else if (keyMap.equals("InvoiceId") && fieldMap.get(keyMap).getValue() != null) {
                            currentInvoiceId = ((String) fieldMap.get(keyMap).getValue()).toUpperCase().trim()
                                    .replaceAll(" ",
                                            "");
                            receiptInvoice.setInvoiceId(currentInvoiceId);
                        }

                        if (currentInvoiceId != null && currentInvoiceTotal != null) {
                            azureReceipt.getAzureReceiptInvoices().add(receiptInvoice);
                        }
                    }

                }
            }
        });

        azureReceipt.setAttachments(Arrays.asList(attachment));
        attachment.setAzureReceipt(azureReceipt);
        attachmentService.addOrUpdateAttachment(attachment);

        return azureReceiptService.addOrUpdateAzureReceipt(azureReceipt);
    }
}
